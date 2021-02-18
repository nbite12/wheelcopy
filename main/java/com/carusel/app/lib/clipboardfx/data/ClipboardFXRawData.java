package com.carusel.app.lib.clipboardfx.data;

import java.io.File;

public class ClipboardFXRawData extends ClipboardFXData{
	private final File file;

	// Constructor
	public ClipboardFXRawData(File file){
		this.file = file;
	}

	public File getFile(){
		return file;
	}
}
