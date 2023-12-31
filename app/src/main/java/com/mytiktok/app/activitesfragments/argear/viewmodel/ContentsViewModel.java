package com.mytiktok.app.activitesfragments.argear.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mytiktok.app.activitesfragments.argear.api.ContentsResponse;
import com.mytiktok.app.Constants;

public class ContentsViewModel extends AndroidViewModel {

    private MutableLiveData<ContentsResponse> mutableLiveData;
    private ContentsRepository contentsRepository;

    public ContentsViewModel(Application application) {
        super(application);
        contentsRepository = ContentsRepository.getInstance();
        mutableLiveData = contentsRepository.getContents(Constants.API_KEY_ARGEAR);
    }

    public LiveData<ContentsResponse> getContents() {
        return mutableLiveData;
    }
}
