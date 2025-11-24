package com.carusel.app.manager;

import com.carusel.app.App;
import com.carusel.app.model.Database;
import com.carusel.app.model.key.KeyCombination;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppManager{
    // Singleton
    private static AppManager instance;
    public static AppManager getInstance(){
        if(instance == null){
            synchronized(AppManager.class){
                if(instance == null){
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    // Field
    private final ObjectProperty<App> app;

    // Constructor
    private AppManager(){
        this.app = new SimpleObjectProperty<>();

        init();
    }

    // Initialize
    private void init(){
        ClipboardManager.getInstance();

        // ClipboardManager.getInstance();
        // LicenseManager.getInstance();
        // DatabaseManager.getInstance();

        LicenseManager.getInstance().startTimer();
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public void openUrl(String url){
        getApp().getHostServices().showDocument(url);
    }

    // *****************************************************************************************
    // *** Keyboard Manager ********************************************************************
    // *****************************************************************************************

    public void registerKeyboardForOpenCloseWheel(){
        // Listener Key
        KeyboardManager.ListenerKey listenerKey = KeyboardManager.ListenerKey.OPEN_CLOSE_WHEEL;

        // Key combination
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        KeyCombination keyCombination = database.getOpenCloseWheelKeyCombination();

        // Callback
        Runnable callback = new Runnable(){
            @Override
            public void run(){
                StageManager
                        .getInstance()
                        .toggleShowOrHideWheel();
            }
        };

        // Type
        KeyboardManager.ListenerType type = KeyboardManager.ListenerType.DEFAULT;

        // Register listener
        KeyboardManager
                .getInstance()
                .registerKeyboardListener(
                        listenerKey,
                        keyCombination,
                        callback,
                        type
                );
    }

//    public void registerKeyboardForDebug(){
//        // Listener Key
//        KeyboardManager.ListenerKey listenerKey = KeyboardManager.ListenerKey.DEFAULT;
//
//        // Key combination
//        KeyCombination keyCombination = new KeyCombination(KeyEvent.D);
//
//        // Callback
//        Runnable callback = new Runnable(){
//            @Override
//            public void run(){
//                WheelManager wheelManager = WheelManager.getInstance();
//                wheelManager.setClipboardDataDev(WheelIndex.FIRST, 0, DebugHelper.getRandomClipboardData());
//            }
//        };
//
//        // Type
//        KeyboardManager.ListenerType type = KeyboardManager.ListenerType.DEFAULT;
//
//        // Register listener
//        KeyboardManager
//                .getInstance()
//                .registerKeyboardListener(
//                        listenerKey,
//                        keyCombination,
//                        callback,
//                        type
//                );
//    }

    public void registerMouseWheelForSwicthWheel(){
        // Listener
        KeyboardManager.MouseWheelListener listener = new KeyboardManager.MouseWheelListener(){
            private long lastScrollTime = 0;
            private static final long SCROLL_COOLDOWN = 75; // 75ms cooldown

            @Override
            public void wheelUp(){
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastScrollTime >= SCROLL_COOLDOWN) {
                    StageManager
                            .getInstance()
                            .spinUpWheel();
                    lastScrollTime = currentTime;
                }
            }

            @Override
            public void wheelDown(){
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastScrollTime >= SCROLL_COOLDOWN) {
                    StageManager
                            .getInstance()
                            .spinDownWheel();
                    lastScrollTime = currentTime;
                }
            }
        };

        // Register listener
        KeyboardManager
                .getInstance()
                .registerMouseWheelListener(listener);
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    public App getApp(){
        return app.get();
    }

    public void bindApp(App app){
        this.app.set(app);
    }
}
