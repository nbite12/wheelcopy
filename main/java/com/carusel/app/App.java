package com.carusel.app;

import com.carusel.app.manager.AppManager;
import com.carusel.app.manager.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        AppManager appManager = AppManager.getInstance();
        appManager.bindApp(this);

        StageManager stageManager = StageManager.getInstance();
        stageManager.bindPrimaryStage(primaryStage);
        stageManager.showStage();

        appManager.registerKeyboardForOpenCloseWheel();
        appManager.registerMouseWheelForSwicthWheel();
    }

    public static void main(String[] args){
        launch();
    }
}
