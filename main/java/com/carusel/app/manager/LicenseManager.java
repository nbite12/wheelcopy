package com.carusel.app.manager;

import com.carusel.app.constants.UserType;
import com.carusel.app.model.Database;
import javafx.beans.property.*;

import java.io.File;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class LicenseManager{
    // Singleton
    private static LicenseManager instance;
    public static LicenseManager getInstance(){
        if(instance == null){
            synchronized(LicenseManager.class){
                if(instance == null){
                    instance = new LicenseManager();
                }
            }
        }
        return instance;
    }

    // Fields
    private final ObjectProperty<File> serialNumberKeyFile;
    private final BooleanProperty trialLimitExceed;
    private final StringProperty requestCode;
    private Timer timer;

    // Constants
    private static final Duration TIMER_DELAY = Duration.ofSeconds(5);
    private static final Duration TIMER_INTERVAL = Duration.ofSeconds(5);
//    public static final Duration TRIAL_LIMIT = Duration.ofSeconds(30);
    private static final String SERIAL_NUMBER_KEY_FILE_NAME = "carusel-key.dll";
    private static final String TIME_TRACKER_FILE_NAME = "wincon32.dll";

    // Constructor
    private LicenseManager(){
        this.serialNumberKeyFile = new SimpleObjectProperty<>();
        this.trialLimitExceed = new SimpleBooleanProperty();
        this.requestCode = new SimpleStringProperty();

        init();
    }

    // Initialize
    private void init(){
        // TODO Sementara mati
        // initSerialNumberKeyFile();


//        initTrialLimitExceed();
        // listenTimeTrackerChange();
    }

//    private void initRequestCode(){
//        // Manager
//        FileManager fileManager = FileManager.getInstance();
//        ResourceManager resourceManager = ResourceManager.getInstance();
//
//        // Key file
//        File keyFile = resourceManager.loadFile(FILE_KEYS);
//        List<String> keys = fileManager.readFileToList(keyFile);
//
//        // Random index between 0-999
//        Random random = new Random();
//        int randomIndex = random.nextInt(1000);
//
//        // Key
//        String requestCode = keys.get(randomIndex);
//        setRequestCode(requestCode);
//    }

//    private void initSerialNumberKeyFile(){
//        String appLocalDataDir = System.getenv(AppConstants.APP_DATA_LOCAL);
//        File file = new File(appLocalDataDir, SERIAL_NUMBER_KEY_FILE_NAME);
//        setSerialNumberKeyFile(file);
//
//        if(!file.exists()){
//            // Manager
//            FileManager fileManager = FileManager.getInstance();
//            CryptoManager cryptoManager = CryptoManager.getInstance();
//
//            // Key file
//            ResourceManager resourceManager = ResourceManager.getInstance();
//            File keyFile = resourceManager.loadFile(FILE_KEYS);
//            List<String> keys = fileManager.readFileToList(keyFile);
//
//            // Random index
//            Random random = new Random();
//            int randomIndex = random.nextInt(1000);
//
//            // Key
//            String key = keys.get(randomIndex);
//
//            // Encrypt
//            byte[] encrypted = cryptoManager.encrypt(key);
//            fileManager.writeByteArrayToFile(file, encrypted);
//
//            // Without encrypt
//            // fileManager.writeStringToFile(file, key);
//        }
//
////        else{
////
////        }
//    }

//    private void initTrialLimitExceed(){
//        DatabaseManager databaseManager = DatabaseManager.getInstance();
//        Database database = databaseManager.getDatabase();
//        trialLimitExceed.bind(database.timeTrackerProperty().greaterThan(TRIAL_LIMIT.toSeconds()));
////        if(database.getTimeTracker() > TRIAL_LIMIT.toSeconds()){
////
////        }
//    }

    // *****************************************************************************************
    // *** Starting ****************************************************************************
    // *****************************************************************************************

    public void start(){

    }

    public void startTimer(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        UserType userType = database.getUserType();

        if(userType != UserType.PREMIUM){
            timer = new Timer();
            TimerTask timerTask = new TimerTask(){
                @Override
                public void run(){
                    if(database.getTimeTracker() > userType.getTimeExpired()){
                        setTrialLimitExceed(true);
                        stopTimer();
                        // generateRequestCode();
                        return;
                    }

                    database.incrementTimeTrackerTotal(TIMER_INTERVAL.toSeconds());
                }
            };
            timer.schedule(timerTask, TIMER_DELAY.toMillis(), TIMER_INTERVAL.toMillis());
        }
    }

//    private void generateRequestCode(){
//        RetrofitManager retrofitManager = RetrofitManager.getInstance();
//        CaruselAPI caruselAPI = retrofitManager.create(CaruselAPI.class);
//        caruselAPI.getRequestCode()
//                .subscribeOn(Schedulers.io())
//                .observeOn(JavaFxScheduler.platform())
//                .subscribe(new SimpleObserver<GetRequestCodeSchema>(){
//                    @Override
//                    public void onNext(GetRequestCodeSchema schema){
//                        DatabaseManager databaseManager = DatabaseManager.getInstance();
//                        Database database = databaseManager.getDatabase();
//                        database.setRequestCode(schema.getRequestCode());
//                        setRequestCode(schema.getRequestCode());
//                    }
//                });
//    }


    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

//    public String getSerialNumberKey(){
//        return "A";
//
////        // Manager
////        CryptoManager cryptoManager = CryptoManager.getInstance();
////        FileManager fileManager = FileManager.getInstance();
////
////        // Decrypt
////        byte[] encrypted = fileManager.readFileToByteArray(getSerialNumberKeyFile());
////        String decrypted = cryptoManager.decrypt(encrypted);
////
////        // Without decrypt
////        // String decrypted = fileManager.readFileToString(getSerialNumberKeyFile());
////
////        return decrypted;
//    }

    public void stopTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // Serial number - Key
    private File getSerialNumberKeyFile(){
        return serialNumberKeyFile.get();
    }
    private void setSerialNumberKeyFile(File serialNumberKeyFile){
        this.serialNumberKeyFile.set(serialNumberKeyFile);
    }

    // Trial limit
    public BooleanProperty trialLimitExceedProperty(){
        return trialLimitExceed;
    }
    public boolean getTrialLimitExceed(){
        return trialLimitExceed.get();
    }
    public void setTrialLimitExceed(boolean isTrialLimitExceed){
        this.trialLimitExceed.set(isTrialLimitExceed);
    }

    // Request code
    public void setRequestCode(String requestCode){
        this.requestCode.set(requestCode);
    }
}
