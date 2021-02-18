package com.carusel.app.manager;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.ClipboardType;
import com.carusel.app.lib.clipboardfx.ClipboardFX;
import com.carusel.app.lib.clipboardfx.ClipboardFXChangeListener;
import com.carusel.app.lib.clipboardfx.data.*;
import com.carusel.app.model.clipboard.ClipboardData;
import com.carusel.app.usecase.ConvertVideoFileToClipboardDataUseCase;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ClipboardManager{
	// Singleton
	private static ClipboardManager instance;
	public static ClipboardManager getInstance(){
		if(instance == null){
			synchronized(ClipboardManager.class){
				if(instance == null){
					instance = new ClipboardManager();
				}
			}
		}
		return instance;
	}

	// Fields
	private final ClipboardFX clipboardFX;

	// Constructor
	private ClipboardManager(){
		FileManager fileManager = FileManager.getInstance();
		this.clipboardFX = new ClipboardFX
				.Builder(fileManager.getCaruselDir())
				.build();

		init();
	}

	// Initialize
	private void init(){
		listenClipboardChange();
	}

	private void listenClipboardChange(){
		clipboardFX.registerClipboardChangeListener(new ClipboardFXChangeListener(){
			@Override
			public void onChange(ClipboardFXData data){
				ClipboardData clipboardData = convertToClipboardData(data);
				WheelManager wheelManager = WheelManager.getInstance();
				wheelManager.addClipboard(clipboardData);
			}
		});
	}

	// *****************************************************************************************
	// *** API *********************************************************************************
	// *****************************************************************************************

	public ClipboardFXData convertToClipboardFXData(ClipboardData data){
		// Text
		if(data.getClipboardType() == ClipboardType.TEXT){
			String text = data.getText();
			var result = new ClipboardFXTextData(text);
			return result;
		}

		// Image
		else if(data.getClipboardType() == ClipboardType.PICTURE){
			File file = data.getContentFile();
			Image image = data.getThumbnail();
			var result = new ClipboardFXImageData(file, image);
			return result;
		}

		// Raw
		else if(data.getClipboardType() == ClipboardType.VIDEO || data.getClipboardType() == ClipboardType.DOCUMENT){
			File file = data.getContentFile();
			var result = new ClipboardFXRawData(file);
			return result;
		}

		var result = new ClipboardFXEmptyData();
		return result;
	}

	public ClipboardData convertToClipboardData(ClipboardFXData data){
		ClipboardData result = new ClipboardData();

		// Text
		if(data instanceof ClipboardFXTextData){
			var textData = (ClipboardFXTextData) data;
			result.setText(textData.getText());
			result.setClipboardType(ClipboardType.TEXT);
		}

		// Image
		else if(data instanceof ClipboardFXImageData){
			var imageData = (ClipboardFXImageData) data;
			result.setThumbnail(imageData.getImage());
			result.setContentFile(imageData.getFile());
			result.setThumbnailSupported(true);
			result.setClipboardType(ClipboardType.PICTURE);
		}

		else if(data instanceof ClipboardFXRawData){
			var rawData = (ClipboardFXRawData) data;
			File file = rawData.getFile();
			String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();

			// Video
			if(AppConstants.VIDEO_EXTENSIONS.contains(ext)){
				var useCase = new ConvertVideoFileToClipboardDataUseCase(file);
				ClipboardData clipboardData = useCase.execute();
				return clipboardData;
			}

			// Doc
			result.setContentFile(file);
			result.setClipboardType(ClipboardType.DOCUMENT);
		}

		return result;
	}

	// *****************************************************************************************
	// *** Field *******************************************************************************
	// *****************************************************************************************

	public ClipboardFX getClipboardFX(){
		return clipboardFX;
	}

	// *****************************************************************************************
	// *** Helper ******************************************************************************
	// *****************************************************************************************
}
