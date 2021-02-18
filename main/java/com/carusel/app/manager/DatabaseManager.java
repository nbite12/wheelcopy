package com.carusel.app.manager;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.ClipboardType;
import com.carusel.app.constants.WheelIndex;
import com.carusel.app.model.Database;
import com.carusel.app.model.clipboard.ClipboardJson;
import com.google.gson.Gson;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.hildan.fxgson.FxGson;
import org.hildan.fxgson.FxGsonBuilder;

import java.io.File;

public class DatabaseManager{
    // Singleton
    private static DatabaseManager instance;
    public static DatabaseManager getInstance(){
        if(instance == null){
            synchronized(DatabaseManager.class){
                if(instance == null){
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    // Fields
    private final ObjectProperty<File> databaseFile;
    private final ObjectProperty<Database> database;
    // private static final Duration TRIAL_LIMIT = Duration.ofSeconds(30);

    // Constants
    // private static final String DATABASE_FILE_NAME = "win_con.dll";
    // private static final String DATABASE_FILE_NAME = "win_config.dll";
    private static final String DATABASE_FILE_NAME = "win_config32.dll";
    // private static final String DATABASE_FILE_NAME = "ncnzm-jyvaf-bmntj-aavag-idved.dll";
    // private static final String DATABASE_FILE_NAME_DEBUG = "win_con_debug.dll";

    // Constructor
    private DatabaseManager(){
        this.database = new SimpleObjectProperty<>();
        this.databaseFile = new SimpleObjectProperty<>();

        init();
    }

    // Initialize
    private void init(){
        initDatabaseFile();
        initDatabase();
        listenDatabaseChange();
    }

    private void initDatabaseFile(){
        String appLocalDataDir = System.getenv(AppConstants.APP_ENV_LOCAL);
        File file = new File(appLocalDataDir, DATABASE_FILE_NAME);
        setDatabaseFile(file);
    }

    private void initDatabase(){
        // Manager
        FileManager fileManager = FileManager.getInstance();
        CryptoManager cryptoManager = CryptoManager.getInstance();

        File databaseFile = getDatabaseFile();

        // Not exist
        if(!databaseFile.exists()){
            // Db
            Database database = new Database();
            setDatabase(database);

            // Data
            Gson gson = FxGson.create();
            String json = gson.toJson(database);

            // Encrypt
            byte[] encrypted = cryptoManager.encrypt(json);
            fileManager.writeByteArrayToFile(databaseFile, encrypted);

            // Without encrypt
            // fileManager.writeStringToFile(databaseFile, json);
        }

        // Exist
        else{
            // Data
            Gson gson = FxGson.create();

            // Decrypt
            byte[] encrypted = fileManager.readFileToByteArray(databaseFile);
            String decrypted = cryptoManager.decrypt(encrypted);

            // Without decrypt
            // String decrypted = fileManager.readFileToString(databaseFile);

            // Db
            Database database = gson.fromJson(decrypted, Database.class);
            evaluateDatabase(database);
            setDatabase(database);
        }
    }

    private void listenDatabaseChange(){
        Database database = getDatabase();
        database.userTypeProperty().addListener((observableValue, oldValue, newValue) -> saveDatabase());
        database.timeTrackerProperty().addListener((observableValue, oldValue, newValue) -> saveDatabase());
        database.requestCodeProperty().addListener((observableValue, oldValue, newValue) -> saveDatabase());
        database.openCloseWheelKeyCombinationProperty().addListener((observableValue, oldValue, newValue) -> saveDatabase());

        var clipboardMap = database.getClipboardMap();
        for(var entryParent : clipboardMap.entrySet()){
            for(var entryChild : entryParent.getValue().entrySet()){
                var property = entryChild.getValue();
                property.addListener((observable, oldValue, newValue) -> saveDatabase());
            }
        }
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    private void saveDatabase(){
        // Manager
        FileManager fileManager = FileManager.getInstance();
        CryptoManager cryptoManager = CryptoManager.getInstance();

        File databaseFile = getDatabaseFile();
        Database database = getDatabase();
        Gson gson = new FxGsonBuilder().builder().setPrettyPrinting().create();
        String json = gson.toJson(database);

        // Encrypt
        byte[] encrypted = cryptoManager.encrypt(json);
        fileManager.writeByteArrayToFile(databaseFile, encrypted);

        // Without encrypt
        // fileManager.writeStringToFile(databaseFile, json);
    }

    private void evaluateDatabase(Database database){
        for(var entryParent : database.getClipboardMap().entrySet()){
            for(var entryChild : entryParent.getValue().entrySet()){
                ClipboardJson clipboardJson = entryChild.getValue().get();
                if(clipboardJson != null){
                    ClipboardType clipboardType = clipboardJson.getClipboardType();
                    if(clipboardType != ClipboardType.EMPTY && clipboardType != ClipboardType.TEXT){
                        String filePath = clipboardJson.getFilePath();

                        boolean isExist = FileManager.getInstance().isFileExist(filePath);
                        if(!isExist){
                            WheelIndex wheelIndex = entryParent.getKey();
                            int elementIndex = entryChild.getKey();
                            database.removeClipboard(wheelIndex, elementIndex);
                        }
                    }
                }
            }
        }
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // Database
    public Database getDatabase(){
        return database.get();
    }
    public void setDatabase(Database database){
        this.database.set(database);
    }

    // Database file
    public File getDatabaseFile(){
        return databaseFile.get();
    }
    public void setDatabaseFile(File databaseFile){
        this.databaseFile.set(databaseFile);
    }
}
