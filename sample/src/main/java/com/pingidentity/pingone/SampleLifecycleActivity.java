package com.pingidentity.pingone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.pingidentity.pingone.util.ActivityLifecycleViewModel;

public class SampleLifecycleActivity extends AppCompatActivity implements LifecycleOwner {

    private ActivityLifecycleViewModel activityLifecycleViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLifecycleViewModel = new ActivityLifecycleViewModel();
        activityLifecycleViewModel.updateLifecycleState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityLifecycleViewModel.updateLifecycleState(Lifecycle.State.RESUMED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityLifecycleViewModel.updateLifecycleState(Lifecycle.State.DESTROYED);
    }
}
