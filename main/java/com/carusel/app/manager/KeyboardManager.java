package com.carusel.app.manager;

import com.carusel.app.model.key.KeyCombination;
import com.carusel.app.model.key.KeyEvent;
import javafx.application.Platform;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyboardManager{
    // Singleton
    private static KeyboardManager instance;
    public static KeyboardManager getInstance(){
        if(instance == null){
            synchronized(KeyboardManager.class){
                if(instance == null){
                    instance = new KeyboardManager();
                }
            }
        }
        return instance;
    }

    // Field
    private final Map<ListenerKey, NativeKeyListener> keyListenerMap;
    private boolean isMultipleKeyCombinationActive = true;

    // Constructor
    private KeyboardManager(){
        this.keyListenerMap = new HashMap<>();

        init();
    }

    // Initialize
    private void init(){
        initGlobalScreen();
    }

    // Init - Global screen
    private void initGlobalScreen(){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        try{
            GlobalScreen.registerNativeHook();
        }catch(NativeHookException e){
            e.printStackTrace();
        }
    }

    // Mouse
    public void registerMouseWheelListener(MouseWheelListener listener, ThreadType threadType){
        GlobalScreen.addNativeMouseWheelListener(new NativeMouseWheelListener(){
            @Override
            public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent){
                Runnable callback = new Runnable(){
                    @Override
                    public void run(){
                        int wheelRotation = nativeMouseWheelEvent.getWheelRotation();
                        if(wheelRotation == -1){
                            listener.wheelUp();
                        }
                        else if(wheelRotation == 1){
                            listener.wheelDown();
                        }
                    }
                };

                // Execute callback either on
                // background or main thread
                if(threadType == ThreadType.BACKGROUND) callback.run();
                else Platform.runLater(callback);
            }
        });
    }
    public void registerMouseWheelListener(MouseWheelListener listener){
        registerMouseWheelListener(listener, ThreadType.MAIN);
    }

    // Keyboard
    public void registerKeyboardListener(Listener listener){
        // Unregister previous listener
        NativeKeyListener previousKeyListener = keyListenerMap.get(ListenerKey.DEFAULT);
        if(previousKeyListener != null){
            unregisterKeyboardListener(previousKeyListener);
        }

        Set<KeyEvent> previousKeyEvents = new HashSet<>();

        // Key listener
        NativeKeyListener keyListener = new NativeKeyListener(){
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent){}

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent){
                for(KeyEvent keyEvent : KeyEvent.getKeyEventList()){
                    if(keyEvent.getKeyCode() == nativeKeyEvent.getKeyCode()){
                        if(!previousKeyEvents.contains(keyEvent)){
                            previousKeyEvents.add(keyEvent);

                            listener.onCallback(keyEvent);
                        }
                    }
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent){}
        };

        // Record listener
        keyListenerMap.put(ListenerKey.DEFAULT, keyListener);

        // Register listener
        GlobalScreen.addNativeKeyListener(keyListener);
    }

    // Register listener
    public void registerKeyboardListener(ListenerKey listenerKey,
                                         KeyCombination keyCombination,
                                         Runnable callback,
                                         ListenerType listenerType,
                                         ThreadType threadType){

        // Unregister previous listener
        NativeKeyListener previousKeyListener = keyListenerMap.get(listenerKey);
        if(previousKeyListener != null){
            unregisterKeyboardListener(previousKeyListener);
        }

        // Key listener
        NativeKeyListener keyListener = new NativeKeyListener(){
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent){}

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent){
                if(isMultipleKeyCombinationActive){
                    for(KeyEvent keyEvent : keyCombination.getKeyEvents()){
                        if(keyEvent.getKeyCode() == nativeKeyEvent.getKeyCode()){
                            keyEvent.setPressed(true);
                        }
                    }

                    boolean isAllPressed = true;
                    for(KeyEvent keyEvent : keyCombination.getKeyEvents()){
                        if(!keyEvent.isPressed()){
                            isAllPressed = false;
                            break;
                        }
                    }

                    // Callback
                    if(isAllPressed){
                        if(listenerType != ListenerType.DEFAULT){
                            try{
                                Thread.sleep(1_000);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }

                        // Execute callback either on
                        // background or main thread
                        if(threadType == ThreadType.BACKGROUND) callback.run();
                        else Platform.runLater(callback);
                    }
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent){
                if(isMultipleKeyCombinationActive){
                    for(KeyEvent keyEvent : keyCombination.getKeyEvents()){
                        if(keyEvent.getKeyCode() == nativeKeyEvent.getKeyCode()){
                            keyEvent.setPressed(false);
                            break;
                        }
                    }
                }
            }
        };

        // Record listener
        keyListenerMap.put(listenerKey, keyListener);

        // Register listener
        GlobalScreen.addNativeKeyListener(keyListener);
    }

    public void registerKeyboardListener(ListenerKey listenerKey,
                                         KeyCombination keyCombination,
                                         Runnable callback,
                                         ListenerType listenerType){
        registerKeyboardListener(listenerKey, keyCombination, callback, listenerType, ThreadType.MAIN);
    }


    // Unregister listener
    private void unregisterKeyboardListener(NativeKeyListener keyListener){
        GlobalScreen.removeNativeKeyListener(keyListener);
    }

    public void unregisterSingleKeyboardListener(){
        NativeKeyListener keyListener = keyListenerMap.get(ListenerKey.DEFAULT);
        if(keyListener != null){
            GlobalScreen.removeNativeKeyListener(keyListener);
        }
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    public void setMultipleKeyCombinationActive(boolean multipleKeyCombinationActive){
        isMultipleKeyCombinationActive = multipleKeyCombinationActive;
    }

    // *****************************************************************************************
    // *** Listener ****************************************************************************
    // *****************************************************************************************

    public interface MouseWheelListener{
        void wheelUp();
        void wheelDown();
    }

    // *****************************************************************************************
    // *** Enum ********************************************************************************
    // *****************************************************************************************

    public interface Listener{
        void onCallback(KeyEvent keyEvent);
    }

    public enum ThreadType{
        MAIN,
        BACKGROUND,
    }

    public enum ListenerType{
        DEFAULT,
        DELAY
    }

    public enum ListenerKey{
        DEFAULT,
        OPEN_CLOSE_WHEEL,
    }
}
