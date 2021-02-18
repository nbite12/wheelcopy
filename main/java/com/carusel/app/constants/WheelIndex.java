package com.carusel.app.constants;

public enum WheelIndex{
    FIRST,
    SECOND,
    THIRD,
    FOURTH;

    public static WheelIndex getNextWheelIndex(WheelIndex currentWheelIndex){
        if(currentWheelIndex == FIRST) return SECOND;
        else if(currentWheelIndex == SECOND) return THIRD;
        else if(currentWheelIndex == THIRD) return FOURTH;
        return FIRST;
    }

    public static WheelIndex getPreviousWheelIndex(WheelIndex currentWheelIndex){
        if(currentWheelIndex == FIRST) return FOURTH;
        else if(currentWheelIndex == SECOND) return FIRST;
        else if(currentWheelIndex == THIRD) return SECOND;
        return THIRD;
    }
}
