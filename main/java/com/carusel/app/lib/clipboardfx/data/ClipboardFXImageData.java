package com.carusel.app.lib.clipboardfx.data;

import javafx.scene.image.Image;

import java.io.File;

public class ClipboardFXImageData extends ClipboardFXData{
	private final File file;
	private final Image image;

	// Constructor
	public ClipboardFXImageData(File file, Image image){
		this.file = file;
		this.image = image;
	}

	public File getFile(){
		return file;
	}

	public Image getImage(){
		return image;
	}
}
