package com.example.honorsproj_covid19;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;


public final class launch_screenmodel extends ViewModel{
    private final MutableLiveData showDialogLiveData = new MutableLiveData();

    @NotNull
    public final LiveData getShowDialog() {
        return (LiveData)this.showDialogLiveData;
    }

    public final void onConfirmOnboardingClicked() {
        this.showDialogLiveData.postValue(true);
    }

    public final void onDialogDismiised() {
        this.showDialogLiveData.postValue(false);
    }
}
