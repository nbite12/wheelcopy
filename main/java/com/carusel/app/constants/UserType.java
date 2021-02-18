package com.carusel.app.constants;

import java.util.HashMap;
import java.util.Map;

public enum UserType{
    PREMIUM(getPremiumElementLockMap(), getPremiumNavigationLockMap(), -1),
    TRIAL_DEFAULT(getPremiumElementLockMap(), getTrialNavigationLockMap(), 259_200),
    TRIAL_MONTHLY(getPremiumElementLockMap(), getPremiumNavigationLockMap(), 7_776_000),
    TRIAL_ANNUAL(getPremiumElementLockMap(), getPremiumNavigationLockMap(), 31_104_000);

    private final Map<WheelIndex, Map<Integer, Boolean>> elementLockMap;
    private final Map<WheelIndex, Boolean> navigationLockMap;
    private final long timeExpired;

    // Constructor
    UserType(Map<WheelIndex, Map<Integer, Boolean>> elementLockMap,
             Map<WheelIndex, Boolean> navigationLockMap,
             long timeExpired){
        this.elementLockMap = elementLockMap;
        this.navigationLockMap = navigationLockMap;
        this.timeExpired = timeExpired;
    }

    public Map<WheelIndex, Map<Integer, Boolean>> getElementLockMap(){
        return elementLockMap;
    }

    public Map<WheelIndex, Boolean> getNavigationLockMap(){
        return navigationLockMap;
    }

    public long getTimeExpired(){
        return timeExpired;
    }

    // *****************************************************************************************
    // *** Helper ******************************************************************************
    // *****************************************************************************************

    private static Map<WheelIndex, Map<Integer, Boolean>> getPremiumElementLockMap(){
        Map<WheelIndex, Map<Integer, Boolean>> parentMap = new HashMap<>();
        for(WheelIndex wheelIndex : WheelIndex.values()){
            Map<Integer, Boolean> childMap = parentMap.computeIfAbsent(
                    wheelIndex,
                    k -> new HashMap<>()
            );

            for(int i = 0; i < 8; i++){
                childMap.put(i, false);
            }
        }
        return parentMap;
    }

    private static Map<WheelIndex, Boolean> getPremiumNavigationLockMap(){
        Map<WheelIndex, Boolean> navigationLockMap = new HashMap<>();
        for(WheelIndex wheelIndex : WheelIndex.values()){
            navigationLockMap.put(wheelIndex, false);
        }
        return navigationLockMap;
    }

    private static Map<WheelIndex, Map<Integer, Boolean>> getTrialElementLockMap(){
        Map<WheelIndex, Map<Integer, Boolean>> parentMap = new HashMap<>();
        for(WheelIndex wheelIndex : WheelIndex.values()){
            Map<Integer, Boolean> childMap = parentMap.computeIfAbsent(
                    wheelIndex,
                    k -> new HashMap<>()
            );

            for(int i = 0; i < 8; i++){
                if(wheelIndex == WheelIndex.FIRST){
                    childMap.put(i, i > 3);
                }

                else childMap.put(i, false);
            }
        }
        return parentMap;
    }

    private static Map<WheelIndex, Boolean> getTrialNavigationLockMap(){
        Map<WheelIndex, Boolean> navigationLockMap = new HashMap<>();
        for(WheelIndex wheelIndex : WheelIndex.values()){
            if(wheelIndex == WheelIndex.FIRST || wheelIndex == WheelIndex.SECOND){
                navigationLockMap.put(wheelIndex, false);
            }

            else{
                navigationLockMap.put(wheelIndex, true);
            }
        }
        return navigationLockMap;
    }
}
