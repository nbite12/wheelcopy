package com.carusel.app.lib.clipboardfx.data;

public class ClipboardFXTextData extends ClipboardFXData{
	private final String text;

	// Constructor
	public ClipboardFXTextData(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}
}
