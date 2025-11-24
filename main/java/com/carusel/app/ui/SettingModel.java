package com.carusel.app.ui;

import com.carusel.app.constants.ActivationType;
import com.carusel.app.constants.UserType;
import com.carusel.app.controls.CircularProgressIndicator;
import com.carusel.app.custom.SimpleObserver;
import com.carusel.app.manager.*;
import com.carusel.app.model.Database;
import com.carusel.app.model.dto.CaruselAPI;
import com.carusel.app.model.key.KeyCombination;
import com.carusel.app.model.key.KeyEvent;
import com.carusel.app.model.schema.ActivationSchema;
import com.carusel.app.model.schema.GetRequestCodeSchema;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class SettingModel{
    private final ObservableList<KeyEvent> optionKeyEvents;
    private final BooleanProperty isRecording;
    private final StringProperty requestCode;
    private final StringProperty activationCode;
    private final ObjectProperty<Node> activationCodeIndicator;
    private final ObjectProperty<ActivationCodeState> activationCodeState;
    private final BooleanProperty activationCodeDisable;

    // Constructor
    public SettingModel(){
        this.optionKeyEvents = FXCollections.observableArrayList();
        this.isRecording = new SimpleBooleanProperty();
        this.requestCode = new SimpleStringProperty();
        this.activationCode = new SimpleStringProperty();
        this.activationCodeIndicator = new SimpleObjectProperty<>();
        this.activationCodeState = new SimpleObjectProperty<>(ActivationCodeState.DEFAULT);
        this.activationCodeDisable = new SimpleBooleanProperty();

        init();
    }

    // Initialize
    private void init(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        KeyCombination keyCombination = database.getOpenCloseWheelKeyCombination();
        List<KeyEvent> keyEvents = keyCombination.getKeyEvents();
        optionKeyEvents.setAll(keyEvents);

        listenActivationCodeChange();
        bindActivationCodeState();
        initRequestCode();
    }

    private void listenActivationCodeChange(){
        activationCode.addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue){
                String regex = "\\w{5}\\-\\w{5}\\-\\w{5}\\-\\w{5}\\-\\w{5}";
                boolean isMatch = Pattern.matches(regex, newValue);

                if(isMatch){
                    validateActivationCode();
                }
            }
        });
    }

    private void validateActivationCode(){
        activationCodeState.set(ActivationCodeState.VALIDATING);

        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        CaruselAPI caruselAPI = retrofitManager.create(CaruselAPI.class);
        caruselAPI.registerActivation(getRequestCode(), getActivationCode())
                .delay(1_000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(new SimpleObserver<ActivationSchema>(){
                    @Override
                    public void onNext(ActivationSchema activationSchema){
                        if(!activationSchema.isError()){
                            setActivationCodeState(ActivationCodeState.VALID);

                            // Manager
                            LicenseManager licenseManager = LicenseManager.getInstance();
                            DatabaseManager databaseManager = DatabaseManager.getInstance();
                            Database database = databaseManager.getDatabase();
                            // database.setActivationCode(getActivationCode());
                            database.resetTimeTracker();

                            if(activationSchema.getActivationType() == ActivationType.LIFETIME){
                                database.setUserType(UserType.PREMIUM);
                                licenseManager.stopTimer();
                            }

                            else if(activationSchema.getActivationType() == ActivationType.ANNUAL){
                                database.setUserType(UserType.TRIAL_ANNUAL);
                            }

                            else if(activationSchema.getActivationType() == ActivationType.MONTHLY){
                                database.setUserType(UserType.TRIAL_MONTHLY);
                            }

                            licenseManager.setTrialLimitExceed(false);
                            licenseManager.startTimer();
                            setActivationCodeDisable(true);
                        }

                        else{
                            setActivationCodeState(ActivationCodeState.INVALID);
                        }
                    }
                });
    }

    private void bindActivationCodeState(){
        ObjectBinding<Node> binding = Bindings.createObjectBinding(new Callable<Node>(){
            @Override
            public Node call() throws Exception{
                ActivationCodeState state = getActivationCodeState();

                // Default
                if(state == ActivationCodeState.DEFAULT){
                    FontIcon icon = new FontIcon(MaterialDesign.MDI_KEY);
                    icon.setFill(Color.GREY);
                    icon.setIconSize(16);
                    return icon;
                }

                // Valid
                else if(state == ActivationCodeState.VALID){
                    FontIcon icon = new FontIcon(MaterialDesign.MDI_CHECK);
                    icon.setFill(Color.GREEN);
                    icon.setIconSize(16);
                    return icon;
                }

                // Invalid
                else if(state == ActivationCodeState.INVALID){
                    FontIcon icon = new FontIcon(MaterialDesign.MDI_CLOSE);
                    icon.setFill(Color.RED);
                    icon.setIconSize(16);
                    return icon;
                }

                // VALIDATING
                else{
                    CircularProgressIndicator indicator = new CircularProgressIndicator();
                    return indicator;
                }
            }
        }, activationCodeState);

        activationCodeIndicator.bind(binding);
    }

    private void initRequestCode(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        String requestCode = database.getRequestCode();

        if(requestCode == null) generateRequestCode();
        else setRequestCode(requestCode);
    }

    public void generateRequestCode(){
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        CaruselAPI caruselAPI = retrofitManager.create(CaruselAPI.class);
        caruselAPI.getRequestCode()
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(new SimpleObserver<GetRequestCodeSchema>(){
                    @Override
                    public void onNext(GetRequestCodeSchema schema){
                        DatabaseManager databaseManager = DatabaseManager.getInstance();
                        Database database = databaseManager.getDatabase();
                        database.setRequestCode(schema.getRequestCode());
                        setRequestCode(schema.getRequestCode());
                    }
                });
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public void unlockAllFeatures(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        LicenseManager licenseManager = LicenseManager.getInstance();

        database.setUserType(UserType.PREMIUM);
        licenseManager.stopTimer();
        licenseManager.setTrialLimitExceed(false);

        setActivationCodeState(ActivationCodeState.VALID);
        setActivationCodeDisable(true);
    }

    public void saveChange(){
        KeyCombination keyCombination = new KeyCombination(getOptionKeyEventsAsArray());
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        database.setOpenCloseWheelKeyCombination(keyCombination);

        AppManager appManager = AppManager.getInstance();
        appManager.registerKeyboardForOpenCloseWheel();
    }

    public void toggleRecordSave(){
        if(isRecording()){
            setRecording(false);

            KeyboardManager keyboardManager = KeyboardManager.getInstance();
            keyboardManager.unregisterSingleKeyboardListener();
            keyboardManager.setMultipleKeyCombinationActive(true);
        }

        else{
            setRecording(true);
            clearOptionKeyEvents();

            KeyboardManager.Listener listener = this::addOptionKeyEvents;
            KeyboardManager keyboardManager = KeyboardManager.getInstance();
            keyboardManager.setMultipleKeyCombinationActive(false);
            keyboardManager.registerKeyboardListener(listener);
        }
    }

    // *****************************************************************************************
    // *** Genral ******************************************************************************
    // *****************************************************************************************

    public void reset(){
        if(getActivationCodeState() == ActivationCodeState.VALID){
            generateRequestCode();
        }

        setActivationCode("");
        setActivationCodeDisable(false);
        setActivationCodeState(ActivationCodeState.DEFAULT);
    }

    public StringBinding recordingButtonName(){
        StringBinding binding = Bindings.createStringBinding(new Callable<String>(){
            @Override
            public String call() throws Exception{
                if(isRecording.get()) return "Save";
                else return "Record";
            }
        }, isRecording);

        return binding;
    }

    public StringBinding optionKeyProperty(){
        StringBinding binding = Bindings.createStringBinding(new Callable<String>(){
            @Override
            public String call() throws Exception{
                StringJoiner stringJoiner = new StringJoiner(" + ");
                optionKeyEvents
                        .forEach(new Consumer<KeyEvent>(){
                            @Override
                            public void accept(KeyEvent keyEvent){
                                stringJoiner.add(keyEvent.getName());
                            }
                        });
                String format = stringJoiner.toString();
                return format;
            }
        }, optionKeyEvents);

        return binding;
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // Option keys
    public ObservableList<KeyEvent> getOptionKeyEvents(){
        return optionKeyEvents;
    }
    private KeyEvent[] getOptionKeyEventsAsArray(){
        return optionKeyEvents.toArray(new KeyEvent[0]);
    }
    public void addOptionKeyEvents(KeyEvent keyEvent){
        optionKeyEvents.add(keyEvent);
    }
    public void clearOptionKeyEvents(){
        optionKeyEvents.clear();
    }

    // Request code
    public StringProperty requestCodeProperty(){
        return requestCode;
    }
    public String getRequestCode(){
        return requestCode.get();
    }
    private void setRequestCode(String requestCode){
        this.requestCode.set(requestCode);
    }

    // Recording
    public BooleanProperty isRecordingProperty(){
        return isRecording;
    }
    public boolean isRecording(){
        return isRecording.get();
    }
    public void setRecording(boolean isRecording){
        this.isRecording.set(isRecording);
    }

    // Activation code
    public StringProperty activationCodeProperty(){
        return activationCode;
    }
    public String getActivationCode(){
        return activationCode.get();
    }
    public void setActivationCode(String activationCode){
        this.activationCode.set(activationCode);
    }

    // Indicator
    public ObjectProperty<Node> getActivationCodeIndicator(){
        return activationCodeIndicator;
    }

    // Activation code state
    public ActivationCodeState getActivationCodeState(){
        return activationCodeState.get();
    }
    public void setActivationCodeState(ActivationCodeState activationCodeState){
        this.activationCodeState.set(activationCodeState);
    }

    // Activation code disable
    public BooleanProperty activationCodeDisableProperty(){
        return activationCodeDisable;
    }
    public void setActivationCodeDisable(boolean activationCodeDisable){
        this.activationCodeDisable.set(activationCodeDisable);
    }

    // *****************************************************************************************
    // *** Enum *******************************************************************************
    // *****************************************************************************************

    private enum ActivationCodeState{
        DEFAULT,
        VALID,
        INVALID,
        VALIDATING
    }
}
