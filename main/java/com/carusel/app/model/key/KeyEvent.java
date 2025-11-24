package com.carusel.app.model.key;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.util.Arrays;
import java.util.List;

public class KeyEvent{
    // Fields
    private final int keyCode;
    private final String name;
    private boolean isPressed;

    // Constants
    public static final KeyEvent CONTROL = new KeyEvent(NativeKeyEvent.VC_CONTROL, "Control");
    public static final KeyEvent SHIFT = new KeyEvent(NativeKeyEvent.VC_SHIFT, "Shift");
    public static final KeyEvent WINDOWS = new KeyEvent(NativeKeyEvent.VC_META, "Windows");
    public static final KeyEvent A = new KeyEvent(NativeKeyEvent.VC_A, "A");
    public static final KeyEvent B = new KeyEvent(NativeKeyEvent.VC_B, "B");
    public static final KeyEvent C = new KeyEvent(NativeKeyEvent.VC_C, "C");
    public static final KeyEvent D = new KeyEvent(NativeKeyEvent.VC_D, "D");
    public static final KeyEvent E = new KeyEvent(NativeKeyEvent.VC_E, "E");
    public static final KeyEvent F = new KeyEvent(NativeKeyEvent.VC_F, "F");
    public static final KeyEvent G = new KeyEvent(NativeKeyEvent.VC_G, "G");
    public static final KeyEvent H = new KeyEvent(NativeKeyEvent.VC_H, "H");
    public static final KeyEvent I = new KeyEvent(NativeKeyEvent.VC_I, "I");
    public static final KeyEvent J = new KeyEvent(NativeKeyEvent.VC_J, "J");
    public static final KeyEvent K = new KeyEvent(NativeKeyEvent.VC_K, "K");
    public static final KeyEvent L = new KeyEvent(NativeKeyEvent.VC_L, "L");
    public static final KeyEvent M = new KeyEvent(NativeKeyEvent.VC_M, "M");
    public static final KeyEvent N = new KeyEvent(NativeKeyEvent.VC_N, "N");
    public static final KeyEvent O = new KeyEvent(NativeKeyEvent.VC_N, "O");
    public static final KeyEvent P = new KeyEvent(NativeKeyEvent.VC_P, "P");
    public static final KeyEvent Q = new KeyEvent(NativeKeyEvent.VC_Q, "Q");
    public static final KeyEvent R = new KeyEvent(NativeKeyEvent.VC_R, "R");
    public static final KeyEvent S = new KeyEvent(NativeKeyEvent.VC_S, "S");
    public static final KeyEvent T = new KeyEvent(NativeKeyEvent.VC_T, "T");
    public static final KeyEvent U = new KeyEvent(NativeKeyEvent.VC_U, "U");
    public static final KeyEvent V = new KeyEvent(NativeKeyEvent.VC_V, "V");
    public static final KeyEvent W = new KeyEvent(NativeKeyEvent.VC_W, "W");
    public static final KeyEvent X = new KeyEvent(NativeKeyEvent.VC_X, "X");
    public static final KeyEvent Y = new KeyEvent(NativeKeyEvent.VC_Y, "Y");
    public static final KeyEvent Z = new KeyEvent(NativeKeyEvent.VC_Z, "Z");

    // Constructor
    public KeyEvent(int keyCode, String name){
        this.keyCode = keyCode;
        this.name = name;
    }

    public int getKeyCode(){
        return keyCode;
    }

    public String getName(){
        return name;
    }

    public void setPressed(boolean pressed){
        isPressed = pressed;
    }

    public boolean isPressed(){
        return isPressed;
    }

    @Override
    public String toString(){
        return name;
    }

    public static List<KeyEvent> getKeyEventList(){
        return Arrays.asList(
                KeyEvent.CONTROL,
                KeyEvent.SHIFT,
                KeyEvent.WINDOWS,
                KeyEvent.A,
                KeyEvent.B,
                KeyEvent.C,
                KeyEvent.D,
                KeyEvent.E,
                KeyEvent.F,
                KeyEvent.G,
                KeyEvent.H,
                KeyEvent.I,
                KeyEvent.J,
                KeyEvent.K,
                KeyEvent.L,
                KeyEvent.M,
                KeyEvent.N,
                KeyEvent.O,
                KeyEvent.P,
                KeyEvent.Q,
                KeyEvent.R,
                KeyEvent.S,
                KeyEvent.T,
                KeyEvent.U,
                KeyEvent.V,
                KeyEvent.W,
                KeyEvent.X,
                KeyEvent.Y,
                KeyEvent.Z
        );
    }
}
