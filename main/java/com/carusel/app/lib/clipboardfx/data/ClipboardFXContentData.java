package com.carusel.app.lib.clipboardfx.data;

import com.carusel.app.lib.clipboardfx.util.ClipboardFXUtil;
import javafx.scene.image.Image;

import java.io.File;

public class ClipboardFXContentData{
	private final String textContent;
	private final Image imageContent;
	private final File fileContent;

	// Constructor
	public ClipboardFXContentData(String textContent, Image imageContent, File fileContent){
		this.textContent = textContent;
		this.imageContent = imageContent;
		this.fileContent = fileContent;
	}

	public String getTextContent(){
		return textContent;
	}

	public Image getImageContent(){
		return imageContent;
	}

	public File getFileContent(){
		return fileContent;
	}

	public boolean isEmpty(){
		return textContent == null && imageContent == null && fileContent == null;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ClipboardFXContentData that = (ClipboardFXContentData) o;

		// Text
		if(textContent != null){
			if(that.textContent != null){
				return textContent.equals(that.textContent);
			}
			else return false;
		}
		else if(that.textContent != null) return false;

		// Image
		if(imageContent != null){
			if(that.imageContent != null){
				return ClipboardFXUtil.isImageEqual(imageContent, that.imageContent);
			}
			else return false;
		}
		else if(that.imageContent != null) return false;

		// File
		if(fileContent != null){
			if(that.fileContent != null){
				return ClipboardFXUtil.isFileEqual(fileContent, that.fileContent);
			}
			else return false;
		}
		else return that.fileContent != null;
	}

	@Override
	public String toString(){
		if(textContent != null) return textContent;
		else if(imageContent != null) return imageContent.toString();
		else if(fileContent != null) return fileContent.getAbsolutePath();
		return "Empty";
	}
}
