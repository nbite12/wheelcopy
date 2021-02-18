package com.carusel.app.utils;

import com.carusel.app.constants.AppConstants;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

public class AppUtils{
    public static boolean isAnotherAppIsRunning(){
        try{
            JUnique.acquireLock(AppConstants.APP_ID);
            return false;
        }catch(AlreadyLockedException e){
            return true;
        }
    }
}