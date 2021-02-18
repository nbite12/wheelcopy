package com.carusel.app.model.key;

import java.util.Arrays;
import java.util.List;

public class KeyCombination{
    private final List<KeyEvent> keyEvents;

    public KeyCombination(KeyEvent... keyEvents){
        this.keyEvents = Arrays.asList(keyEvents);
    }

    public List<KeyEvent> getKeyEvents(){
        return keyEvents;
    }
}
