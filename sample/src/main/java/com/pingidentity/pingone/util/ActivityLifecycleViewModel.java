package com.pingidentity.pingone.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivityLifecycleViewModel extends ViewModel {

    private MutableLiveData<Lifecycle.State> stateMutableLiveData;

    //required public c-tor
    public ActivityLifecycleViewModel(){
        stateMutableLiveData = new MutableLiveData<>();
    }

    public void updateLifecycleState(Lifecycle.State state){
        this.stateMutableLiveData.postValue(state);
    }

    public MutableLiveData<Lifecycle.State> getStateMutableLiveData(){
        return this.stateMutableLiveData;
    }
}
