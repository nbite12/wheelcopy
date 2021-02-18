package com.carusel.app.constants;

import com.carusel.app.model.key.KeyCombination;
import com.carusel.app.model.key.KeyEvent;

public class DefaultValue{
	public static KeyCombination getOpenCloseWheelKeyCombination(){
		KeyCombination keyCombination = new KeyCombination(
				KeyEvent.CONTROL,
				KeyEvent.WINDOWS,
				KeyEvent.V
		);

		return keyCombination;
	}
}
