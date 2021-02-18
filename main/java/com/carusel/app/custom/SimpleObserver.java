package com.carusel.app.custom;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class SimpleObserver<T> implements Observer<T>{
    @Override
    public void onSubscribe(Disposable disposable){

    }

    @Override
    public void onError(Throwable throwable){

    }

    @Override
    public void onComplete(){

    }
}
