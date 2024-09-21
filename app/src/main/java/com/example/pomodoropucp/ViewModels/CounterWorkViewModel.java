package com.example.pomodoropucp.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CounterWorkViewModel extends ViewModel {
    private final MutableLiveData<Integer>counter=new MutableLiveData<>();
    private final MutableLiveData<Integer>restCounter=new MutableLiveData<>();
    private final MutableLiveData<Integer>inRest=new MutableLiveData<>();
    private final MutableLiveData<Integer>taskExecutingCounter=new MutableLiveData<>();
    public MutableLiveData<Integer>getCounter(){
        return counter;
    }
    public MutableLiveData<Integer>getRestCounter(){
        return restCounter;
    }
    public MutableLiveData<Integer>getInRest(){
        return inRest;
    }
    public MutableLiveData<Integer>getTaskExecutingCounter(){
        return taskExecutingCounter;
    }
}
