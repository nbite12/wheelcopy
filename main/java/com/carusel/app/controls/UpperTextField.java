package com.carusel.app.controls;

import org.controlsfx.control.textfield.CustomTextField;

public class UpperTextField extends CustomTextField{
    @Override
    public void replaceText(int start, int end, String text){
        super.replaceText(start, end, text.toUpperCase());
    }
}
