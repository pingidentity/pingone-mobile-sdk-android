package com.pingidentity.pingone;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.pingidentity.pingidsdkv2.PingOne;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*
         * Starting Since August 17 2020 all Firebase versions have been updated and now you have to
         * call Firebase.initializeApp() before using any Firebase product
         */
        FirebaseApp.initializeApp(this);
        PingOne.setSafetyNetApiKey(this, BuildConfig.API_KEY);
    }
}
