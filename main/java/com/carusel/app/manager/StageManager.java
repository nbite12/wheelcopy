package com.carusel.app.manager;

import animatefx.animation.AnimationFX;
import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import com.carusel.app.constants.ResourceConstants;
import com.carusel.app.lib.fxtrayicon.FXTrayIcon;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class StageManager{
    // Singleton
    private static StageManager instance;
    public static StageManager getInstance(){
        if(instance == null){
            synchronized(StageManager.class){
                if(instance == null){
                    instance = new StageManager();
                }
            }
        }
        return instance;
    }

    // Fields
    private Stage primaryStage;
    private Stage wheelStage;
    private Stage settingStage;
    private Scene wheelScene;
    private Scene settingScene;
    private boolean isShow = true;
//    private boolean isFocus = true;

    // Animation
    private final AnimationFX showSetting;
    private final AnimationFX hideSetting;

    // Constants
    private static final String MAIN_CSS = "main.css";

    // Constructor
    private StageManager(){
        this.showSetting = new ZoomIn();
        this.hideSetting = new ZoomOut();

        init();
    }

    // Initialize
    private void init(){
        initGeneral();
        initMainScene();
        initSettingScene();
    }

    private void initGeneral(){
        Platform.setImplicitExit(false);
    }

    private void initMainScene(){
        WheelManager wheelManager = WheelManager.getInstance();

        // Root
        StackPane root = wheelManager.getRoot();

        // Scene
        wheelScene = new Scene(root, Color.RED);
        wheelScene.getStylesheets().add(ResourceManager.getInstance().loadStyle(MAIN_CSS));
        wheelScene.setFill(Color.TRANSPARENT);
    }

    private void initSettingScene(){
        try{
            // Root
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ResourceConstants.SETTING_LAYOUT));
            Parent root = loader.load();

            showSetting.setNode(root);
            hideSetting.setNode(root);

            showSetting.setSpeed(4.0);
            showSetting.setDelay(Duration.millis(300));

            // Scene
            settingScene = new Scene(root);
            settingScene.setFill(Color.TRANSPARENT);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public void bindPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;

        buildPrimaryStage();
        buildWheelStage();
        buildSettingStage();
    }

    private void buildPrimaryStage(){
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setOpacity(0);
        primaryStage.show();
    }

    private void buildWheelStage(){
        wheelStage = new Stage();
        buildSecondaryStage(wheelStage, wheelScene);
        wheelStage.setMaximized(true);

        // Tray icon
        setupTrayIcon();
    }

    private void buildSettingStage(){
        settingStage = new Stage();
        buildSecondaryStage(settingStage, settingScene);
    }

    private void buildSecondaryStage(Stage stage, Scene scene){
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
//        stage.sizeToScene();
    }

    public void startWheelStage(){
        wheelStage.show();

//        showStage();
        showWheel();

//        WheelManager wheelManager = WheelManager.getInstance();
//        wheelManager.loadData();

//        settingStage.show();
//        hideSetting();
        settingStage.hide();
    }

    public void startSettingStage(){
        settingStage.show();

        hideWheel();
//        showSetting();
    }

    private void setupTrayIcon(){
        URL iconUrl = getClass().getResource("/image/icon.png");
        FXTrayIcon trayIcon = new FXTrayIcon(primaryStage, iconUrl);
        trayIcon.setTrayIconTooltip("Carusel");
        trayIcon.registerListener(this::showStage);
        trayIcon.show();
    }

    // *****************************************************************************************
    // *** Wheel *******************************************************************************
    // *****************************************************************************************

    public void toggleShowOrHideWheel(){
        if(!settingStage.isShowing()){
            if(isShow) hideStage();
            else showStage();
        }
    }

    public void spinUpWheel(){
        if(isShow){
            WheelManager wheelManager = WheelManager.getInstance();
            wheelManager.changeToNextWheel();
        }
    }

    public void spinDownWheel(){
        if(isShow){
            WheelManager wheelManager = WheelManager.getInstance();
            wheelManager.changeToPreviousWheel();
        }
    }

    private void showWheel(){
        isShow = true;

//        primaryStage.show();
//        wheelStage.show();

//        buildPrimaryStage();
//        primaryStage.show();
//        wheelStage.show();
//        if(!primaryStage.isShowing()) primaryStage.show();
//        if(!wheelStage.isShowing()) wheelStage.show();

        WheelManager wheelManager = WheelManager.getInstance();
        wheelManager.showWheel();

//        // Should wait a couple of milli-second
//        // before request stage focus
//        try{
//            Thread.sleep(50);
//            // primaryStage.requestFocus();
//            wheelStage.requestFocus();
//            isFocus = true;
//
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }
    }

//    public void showStageOnly(){
//        isShow = true;
//        primaryStage.show();
//        wheelStage.show();
//    }


    public void showStage(){
        isShow = true;

        primaryStage.show();
        wheelStage.show();

//        Whe/elManager wheelManager = WheelManager.getInstance();
//        wheelManager.getRoot().requestFocus();
        // wheelStage.requestFocus();

//        // Should wait a couple of milli-second
//        // before request stage focus
//        new Thread(new Runnable(){
//            @Override
//            public void run(){
//                try{
//                    Thread.sleep(100);
//                    Platform.runLater(new Runnable(){
//                        @Override
//                        public void run(){
//                            System.out.println("Request bor");
//                            wheelStage.requestFocus();
//                        }
//                    });
//
//                }catch(InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    public void hideWheel(){
        isShow = false;
//        isFocus = false;

        WheelManager wheelManager = WheelManager.getInstance();
        wheelManager.hideWheel();

//        settingStage.toBack();
//        wheelStage.hide();
//        primaryStage.hide();
    }

//    public void hideStageOnly(){
//        isShow = false;
//        primaryStage.hide();
//    }

    public void hideStage(){
        isShow = false;

        WheelManager wheelManager = WheelManager.getInstance();
        wheelManager.unselectAllElements();

        primaryStage.hide();
        // wheelStage.hide();
//        isShow = false;
//        isFocus = false;

//        settingStage.toBack();
//        wheelStage.toBack();
//        primaryStage.toBack();

//        wheelStage.hide();
//        primaryStage.hide();
    }

//    public void showSetting(){
////        settingStage.show();
////        settingStage.setOpacity(0);
//        showSetting.play();
//    }
//
//    public void hideSetting(){
//        hideSetting.play();
//    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************
}
