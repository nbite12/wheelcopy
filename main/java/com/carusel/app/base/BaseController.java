package com.carusel.app.base;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable{
    @FXML
    protected Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        init();
    }

    protected abstract void init();

    public Parent getRoot(){
        return root;
    }
    public Window getWindow(){
        return getRoot().getScene().getWindow();
    }
    public Stage getStage(){
        return (Stage) getWindow();
    }
    public void closeStage(){
        getStage().close();
    }
}