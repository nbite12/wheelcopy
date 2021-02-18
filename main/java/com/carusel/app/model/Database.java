package com.carusel.app.model;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.DefaultValue;
import com.carusel.app.constants.UserType;
import com.carusel.app.constants.WheelIndex;
import com.carusel.app.factory.ElementFactory;
import com.carusel.app.model.clipboard.ClipboardJson;
import com.carusel.app.model.key.KeyCombination;
import javafx.beans.property.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Database{
    private final ObjectProperty<KeyCombination> openCloseWheelKeyCombination;
    private final IntegerProperty databaseVersion;
    private final ObjectProperty<UserType> userType;
    private final LongProperty timeTracker;
    private final StringProperty requestCode;
    private final Map<WheelIndex, Map<Integer, ObjectProperty<ClipboardJson>>> clipboardMap;

    // Constructor
    public Database(){
        this.openCloseWheelKeyCombination = new SimpleObjectProperty<>(DefaultValue.getOpenCloseWheelKeyCombination());
        this.databaseVersion = new SimpleIntegerProperty(AppConstants.DATABASE_VERSION);
        this.userType = new SimpleObjectProperty<>(UserType.TRIAL_DEFAULT);
        this.timeTracker = new SimpleLongProperty();
        this.requestCode = new SimpleStringProperty();
        this.clipboardMap = new LinkedHashMap<>();

        init();
    }

    // Initialize
    private void init(){
        initClipboardData();
    }

    private void initClipboardData(){
        for(WheelIndex wheelIndex : WheelIndex.values()){
            for(int index = 0; index < ElementFactory.TOTAL_ELEMENT; index++){
                Map<Integer, ObjectProperty<ClipboardJson>> map = clipboardMap.computeIfAbsent(
                        wheelIndex,
                        k -> new LinkedHashMap<>()
                );
                ObjectProperty<ClipboardJson> property = new SimpleObjectProperty<>();
                map.put(index, property);
            }
        }
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // KeyCombination - Open close wheel
    public ObjectProperty<KeyCombination> openCloseWheelKeyCombinationProperty(){
        return openCloseWheelKeyCombination;
    }
    public KeyCombination getOpenCloseWheelKeyCombination(){
        return openCloseWheelKeyCombination.get();
    }
    public void setOpenCloseWheelKeyCombination(KeyCombination openCloseWheelKeyCombination){
        this.openCloseWheelKeyCombination.set(openCloseWheelKeyCombination);
    }

    // User type
    public ObjectProperty<UserType> userTypeProperty(){
        return userType;
    }
    public UserType getUserType(){
        return userType.get();
    }
    public void setUserType(UserType userType){
        this.userType.set(userType);
    }

    // Time tracker
    public LongProperty timeTrackerProperty(){
        return timeTracker;
    }
    public long getTimeTracker(){
        return timeTracker.get();
    }
    public void incrementTimeTrackerTotal(long increment){
        this.timeTracker.set(getTimeTracker() + increment);
    }
    public void resetTimeTracker(){
        timeTracker.set(0);
    }

    // Request code
    public String getRequestCode(){
        return requestCode.get();
    }
    public StringProperty requestCodeProperty(){
        return requestCode;
    }
    public void setRequestCode(String requestCode){
        this.requestCode.set(requestCode);
    }

    // Clipboard
    public Map<WheelIndex, Map<Integer, ObjectProperty<ClipboardJson>>> getClipboardMap(){
        return clipboardMap;
    }
    public ObjectProperty<ClipboardJson> clipboardProp(WheelIndex wheelIndex, int index){
        return clipboardMap.get(wheelIndex).get(index);
    }
    public void setClipboard(WheelIndex wheelIndex, int index, ClipboardJson value){
        clipboardMap.get(wheelIndex).get(index).set(value);
    }
    public void removeClipboard(WheelIndex wheelIndex, int elementIndex){
        clipboardMap.get(wheelIndex).get(elementIndex).set(null);
    }
}
