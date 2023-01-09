package com.pingidentity.pingone.camera;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.pingidentity.pingone.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrReaderFragment extends Fragment {

    /*
     * Blocking camera operations are performed using this executor
     */
    private ExecutorService cameraExecutor;

    /*
     * Custom View that displays the camera feed for CameraX's Preview use case.
     */
    private PreviewView previewView;

    /*
     * ActivityResultLauncher, as an instance variable.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private NavController controller;

    private boolean detected;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize background executor each time the view is recreated
        cameraExecutor = Executors.newSingleThreadExecutor();
        previewView = view.findViewById(R.id.camera_preview);

        controller = Navigation.findNavController(view);

        /*
         * Register the permissions callback, which handles the user's response to the
         * system permissions dialog.
         */
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        /*
                         * Permission is granted. Continue the action or workflow in your app
                         */
                        startCameraPreview();
                    } else {
                        /*
                         * Explain to the user that the feature is unavailable because the
                         * features requires a permission that the user has denied.
                         */

                    }
                });


        EditText authCodeInput = view.findViewById(R.id.manual_authentication_input);

        view.findViewById(R.id.button_authenticate)
                .setOnClickListener(button -> onQrCodeDetected(authCodeInput.getText().toString()));
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * Check for the camera permission before accessing the camera. If the
         * permission is not granted yet, request permission.
         */
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startCameraPreview();
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            /*
             * prompt the user with explanation about the reason you request camera permission
             */
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor!=null && !cameraExecutor.isShutdown()){
            cameraExecutor.shutdown();
        }
    }

    private void startCameraPreview(){
        previewView.post(this::bindCameraUseCases);
    }

    /*
     * Declare and bind preview and analysis use cases
     */
    private void bindCameraUseCases(){

        DisplayMetrics metrics = new DisplayMetrics();
        previewView.getDisplay().getRealMetrics(metrics);
        final int rotation = previewView.getDisplay().getRotation();
        final int screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels);
        //camera selector
        final CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        final ListenableFuture<ProcessCameraProvider> futureCameraProvider = ProcessCameraProvider.getInstance(requireContext());
        futureCameraProvider.addListener(() -> {
            try{
                ProcessCameraProvider processCameraProvider = futureCameraProvider.get();

                // Preview
                Preview preview = new Preview.Builder()
                        // We request aspect ratio but no resolution
                        .setTargetAspectRatio(screenAspectRatio)
                        // Set initial target rotation
                        .setTargetRotation(rotation)
                        .build();
                preview.setSurfaceProvider(cameraExecutor, previewView.getSurfaceProvider());

                //QrCode analyzer
                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        // We request aspect ratio but no resolution
                        .setTargetAspectRatio(screenAspectRatio)
                        // Set initial target rotation, we will have to call this again if rotation changes
                        // during the lifecycle of this use case
                        .setTargetRotation(rotation)
                        .build();
                analysis.setAnalyzer(cameraExecutor, new QRCodeAnalyzer());

                processCameraProvider.unbindAll();

                processCameraProvider.bindToLifecycle(QrReaderFragment.this, cameraSelector, preview, analysis);
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void onQrCodeDetected(String qrCodeContent){
        requireActivity().runOnUiThread(() -> {
            NavDirections action = QrReaderFragmentDirections.actionQrReaderFragmentToQrParserFragment(qrCodeContent);
            controller.navigate(action);
        });
    }

    /*
     * [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     * [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     * Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     * of preview ratio to one of the provided values.
     *
     * @param width - preview width
     * @param height - preview height
     * @return suitable aspect ratio
     */
    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) /min(width, height);
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private class QRCodeAnalyzer implements ImageAnalysis.Analyzer{
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            Image image = imageProxy.getImage();
            if (image == null) {
                return;
            }
            InputImage inputImage = InputImage.fromMediaImage(image,
                    imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        if (barcodes.size() > 0 && !detected) {
                            detected = true;
                            cameraExecutor.shutdown();
                            onQrCodeDetected(barcodes.get(0).getRawValue());

                        }
                        imageProxy.close();

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        imageProxy.close();
                    });
        }
    }
}
