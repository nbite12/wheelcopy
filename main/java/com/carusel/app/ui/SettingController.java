package com.carusel.app.ui;

import com.carusel.app.base.BaseController;
import com.carusel.app.manager.StageManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.CustomTextField;

public class SettingController extends BaseController{
    // Model
    private final SettingModel model = new SettingModel();

    // Controls
    @FXML
    private TextField fieldKeyCombinations;
    @FXML
    private JFXButton buttonRecord;
    @FXML
    private CustomTextField fieldRequestCode;
    @FXML
    private CustomTextField fieldActivationCode;

    @Override
    protected void init(){
        fieldKeyCombinations.textProperty().bind(model.optionKeyProperty());
        buttonRecord.textProperty().bind(model.recordingButtonName());

        fieldRequestCode.textProperty().bind(model.requestCodeProperty());
        fieldActivationCode.textProperty().bindBidirectional(model.activationCodeProperty());
        fieldActivationCode.rightProperty().bind(model.getActivationCodeIndicator());
        fieldActivationCode.editableProperty().bind(model.activationCodeDisableProperty().not());
    }

    @FXML
    private void handleOkClick(){
        model.saveChange();

        close();
    }

    @FXML
    private void handleCancelClick(){
        close();
    }

    @FXML
    private void handleRecordClick(){
        model.toggleRecordSave();
    }

    @FXML
    private void handleUnlockAllClick(){
        model.unlockAllFeatures();
    }

    private void close(){
        closeStage();


        StageManager.getInstance().startWheelStage();
        model.reset();

//        StageManager.getInstance().hideSetting();
    }
}
