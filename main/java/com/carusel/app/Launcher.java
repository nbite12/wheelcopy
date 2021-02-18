package com.carusel.app;

import com.carusel.app.utils.AppUtils;

public class Launcher{
    public static void main(String[] args){
        if(!AppUtils.isAnotherAppIsRunning()){
            App.main(args);
        }
    }
}