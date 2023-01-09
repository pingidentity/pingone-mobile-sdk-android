package com.pingidentity.pingone;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class ManualAuthActivity extends SampleActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_auth);
    }
}
