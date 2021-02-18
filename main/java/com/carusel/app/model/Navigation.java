package com.carusel.app.model;

import com.carusel.app.constants.WheelIndex;
import com.carusel.app.manager.WheelManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class Navigation{
    private final WheelIndex wheelIndex;
    private final Group container;
    private final ImageView lockView;
    private final BooleanProperty isSelected;
    private final BooleanProperty isLocked;

    // CSS
    private static final String NAVIGATION_CONTAINER_SELECTED_CLASS = "navigation-container-selected";

    // Constructor
    public Navigation(WheelIndex wheelIndex, Group container, ImageView lockView, boolean isSelected, boolean isLocked){
        this.wheelIndex = wheelIndex;
        this.container = container;
        this.lockView = lockView;
        this.isSelected = new SimpleBooleanProperty(isSelected);
        this.isLocked = new SimpleBooleanProperty(isLocked);

        init();
    }

    // Initialize
    private void init(){
        initGeneral();

        container.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent){
                if(!isSelected.get() && !isLocked.get()){
                    select();
                }
            }
        });

        isSelected.addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue){
                if(newValue){
                    container.getStyleClass().add(NAVIGATION_CONTAINER_SELECTED_CLASS);

                    // Unselect other navigation
                    for(OnSelectedListener listener : onSelectedListeners){
                        listener.onSelected();
                    }
                }
                else container.getStyleClass().remove(NAVIGATION_CONTAINER_SELECTED_CLASS);
            }
        });

        if(isSelected()){
            container.getStyleClass().add(NAVIGATION_CONTAINER_SELECTED_CLASS);
        }
    }

    private void initGeneral(){
        lockView.visibleProperty().bind(isLocked);
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public void select(){
        if(!isSelected()){
            setSelected(true);
//            isSelected = true;
//            container.getStyleClass().add(NAVIGATION_CONTAINER_SELECTED_CLASS);

            WheelManager.getInstance().changeWheel(wheelIndex);
        }
    }

    public void unselect(){
        setSelected(false);
//        isSelected = false;
//        container.getStyleClass().remove(NAVIGATION_CONTAINER_SELECTED_CLASS);
    }

    // *****************************************************************************************
    // *** Fields ******************************************************************************
    // *****************************************************************************************

    public WheelIndex getWheelIndex(){
        return wheelIndex;
    }

    public Node getNode(){
        return container;
    }

    public boolean isLocked(){
        return isLocked.get();
    }

    // Selected
    public boolean isSelected(){
        return isSelected.get();
    }
    public void setSelected(boolean isSelected){
        this.isSelected.set(isSelected);
    }

    public BooleanProperty isSelectedProperty(){
        return isSelected;
    }

    // *****************************************************************************************
    // *** Listener ****************************************************************************
    // *****************************************************************************************

    public interface OnSelectedListener{
        void onSelected();
    }
    private final List<OnSelectedListener> onSelectedListeners = new ArrayList<>();
    public void registerListener(OnSelectedListener listener){
        onSelectedListeners.add(listener);
    }
}
