package com.carusel.app.lib.clipboardfx;

import com.carusel.app.lib.clipboardfx.data.*;
import com.carusel.app.lib.clipboardfx.util.ClipboardFXUtil;
import com.sun.glass.ui.ClipboardAssistance;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.robot.Robot;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClipboardFX{
	public static class Builder{
		// Required parameters
		private final File imageCacheDir;

		// Optional parameters
		private boolean isClearClipboard = true;

		// Constructor
		public Builder(File imageCacheDir){
			this.imageCacheDir = imageCacheDir;
		}

		public Builder clearClipboard(boolean isClearClipboard){
			this.isClearClipboard = isClearClipboard;
			return this;
		}

		public ClipboardFX build(){
			ClipboardFX clipboardFX = new ClipboardFX(this);

			if(isClearClipboard){
				clipboardFX.clear();
			}

			return clipboardFX;
		}
	}

	// Fields
	private final Clipboard systemClipboard;
	private final List<ClipboardFXChangeListener> clipboardFXChangeListeners;

	// Property
	private final ObjectProperty<ClipboardFXContentData> lastClipboardContentData;
	private final ObjectProperty<File> imageCacheDir;
	private final BooleanProperty isClipboardChangeListenerDisable;

	// Constructor
	private ClipboardFX(Builder builder){
		this.systemClipboard = Clipboard.getSystemClipboard();
		this.clipboardFXChangeListeners = new ArrayList<>();
		this.lastClipboardContentData = new SimpleObjectProperty<>();
		this.imageCacheDir = new SimpleObjectProperty<>(builder.imageCacheDir);
		this.isClipboardChangeListenerDisable = new SimpleBooleanProperty(false);

		init();
	}

	/**
	 * Initializer
	 */
	private void init(){
		listenClipboardDataChange();
	}

	/**
	 * Create timer to check clipboard change with a specific time interval
	 */
	private void listenClipboardDataChange(){
		new ClipboardAssistance(com.sun.glass.ui.Clipboard.SYSTEM){
			@Override
			public void contentChanged(){
				super.contentChanged();

				if(!isClipboardChangeListenerDisable()){
					boolean isChange = isClipboardDataChange();
					if(isChange) notifyClipboardDataChange();
				}
			}
		};
	}

	public void checkClipboardDataChange(){
		boolean isChange = isClipboardDataChange();
		if(isChange) notifyClipboardDataChange();
	}

	/**
	 * Check if there is a clipboard content change or not.
	 * If there is a clipboard content change, the current content
	 * stored as {@code lastClipboardFXContentData} to be used
	 * when this method called again.
	 *
	 * @return {@code true} if the there is a clipboard change and {@code false} otherwise
	 */
	private boolean isClipboardDataChange(){
		var currentClipboardFXContentData = getCurrentClipboardFXContentData();
		System.out.println("Current: " + currentClipboardFXContentData);

		var lastClipboardFXContentData = getLastClipboardFXContentData();
		if(lastClipboardFXContentData == null){
			if(!currentClipboardFXContentData.isEmpty()){
				setLastClipboardData(currentClipboardFXContentData);
				return true;
			}
		}
		else if(!lastClipboardFXContentData.equals(currentClipboardFXContentData)){
			setLastClipboardData(currentClipboardFXContentData);
			return true;
		}

		return false;
	}

//	private boolean isClipboardDataEqual(ClipboardFXData firstData, ClipboardFXData secondData){
//		// Text
//		if(firstData instanceof ClipboardFXTextData){
//			var firstTextData = (ClipboardFXTextData) firstData;
//			if(secondData instanceof ClipboardFXTextData){
//				var secondTextData = (ClipboardFXTextData) secondData;
//				boolean isEqual = firstTextData.getText().equals(secondTextData.getText());
//				return isEqual;
//			}
//			else return false;
//		}
//
//		// Image
//		else if(firstData instanceof ClipboardFXImageData){
//			var firstImageData = (ClipboardFXImageData) firstData;
//			if(secondData instanceof ClipboardFXImageData){
//				var secondImageData = (ClipboardFXImageData) secondData;
//				Image firstImage = firstImageData.getImage();
//				Image secondImage = secondImageData.getImage();
//
//				boolean isEqual = ClipboardFXUtil.isImageEqual(firstImage, secondImage);
//				return isEqual;
//			}
//			else return false;
//		}
//
//		return false;
//	}

	/**
	 * Notify all registered listeners if there is a content change on clipbord.
	 * But at first will check if list of listeners is empty or not for optimization purpose
	 */
	private void notifyClipboardDataChange(){
		if(clipboardFXChangeListeners.size() > 0){
			ClipboardFXContentData lastClipboardFXContentData = getLastClipboardFXContentData();
			ClipboardFXData data = ClipboardFXUtil.convertToClipboardFXData(lastClipboardFXContentData, getImageCacheDir());
			for(ClipboardFXChangeListener listener : clipboardFXChangeListeners){
				listener.onChange(data);
			}
		}
	}

//	/**
//	 * Get the current {@link ClipboardFXType} based on data stored on clipboard system.
//	 * This method will check first text and image data.
//	 * If there is no text and image data, this method will check a list of files.
//	 * If a list of files is more than 1, this method will ignore the data and will return ClipboardFXType.EMPTY.
//	 * If there is no data, this method will return ClipboardFXType.EMPTY.
//	 *
//	 * @return {@link ClipboardFXType}
//	 */
//	public ClipboardFXType getCurrentClipboardFXType(){
//		String textContent = systemClipboard.getString();
//		if(textContent != null) return ClipboardFXType.TEXT;
//
//		Image imageContent = systemClipboard.getImage();
//		if(imageContent != null){
//			return ClipboardFXType.IMAGE;
//		}
//
//		List<File> files = systemClipboard.getFiles();
//		if(files.size() > 0){
//			File file = files.get(0);
//			String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();
//
//			if(IMAGE_EXTENSIONS.contains(ext)){
//				return ClipboardFXType.IMAGE;
//			}
//
//			else if(VIDEO_EXTENSIONS.contains(ext)){
//				return ClipboardFXType.VIDEO;
//			}
//
//			else return ClipboardFXType.RAW;
//		}
//
//		return ClipboardFXType.EMPTY;
//	}

//	public void refreshClipboardData(){
//
//	}

	/**
	 * Get the current {@link ClipboardFXContentData} based on content stored on clipboard system.
	 * @return {@link ClipboardFXContentData}
	 */
	public ClipboardFXContentData getCurrentClipboardFXContentData(){
		String textContent = systemClipboard.getString();
		Image imageContent = systemClipboard.getImage();

		File file = null;
		List<File> files = systemClipboard.getFiles();
		if(files.size() > 0){
			file = files.get(0);
		}

		var data = new ClipboardFXContentData(textContent, imageContent, file);
		return data;
	}

//	public ClipboardFXData getCurrentClipboardFXData(){
//		ClipboardFXType clipboardFXType = getCurrentClipboardFXType();
//
//		// Text
//		if(clipboardFXType == ClipboardFXType.TEXT){
//			String text = systemClipboard.getString();
//			var data = new ClipboardFXTextData(text);
//			return data;
//		}
//
//		// Image
//		else if(clipboardFXType == ClipboardFXType.IMAGE){
//			File file = null;
//			Image image = systemClipboard.getImage();
//			if(image != null){
//				file = ClipboardFXUtil.writeImageToFile(image, getImageCacheDir());
//			}
//			else{
//				List<File> files = systemClipboard.getFiles();
//				if(files.size() > 0){
//					try{
//						file = files.get(0);
//						String url = file.toURI().toURL().toExternalForm();
//						image = new Image(url);
//
//					}catch(MalformedURLException e){
//						e.printStackTrace();
//					}
//				}else throw new DataNotFoundException();
//			}
//
//			var data = new ClipboardFXImageData(file, image, true);
//			return data;
//		}
//
//		return new ClipboardFXEmptyData();
//	}

	/**
	 * Clears all data from the clipboard
	 */
	public void clear(){
		systemClipboard.clear();
	}

	/**
	 * Trigger native paste event.
	 */
	public void paste(){
		Robot robot = new Robot();
		robot.keyPress(KeyCode.CONTROL);
		robot.keyPress(KeyCode.V);
		robot.keyRelease(KeyCode.V);
		robot.keyRelease(KeyCode.CONTROL);
	}

	/**
	 * Trigger native paste event with temporary data.
	 * After the paste event is finished, the temporary data
	 * will be replaced by last data from the clipboard.
	 */
	public void paste(ClipboardFXData clipboardFXData){
		setClipboardChangeListenerDisable(true);
		setData(clipboardFXData);

		Thread thread = new Thread(new Runnable(){
			@SneakyThrows
			@Override
			public void run(){
				Thread.sleep(250);
				Platform.runLater(new Runnable(){
					@Override
					public void run(){
						paste();
					}
				});

				Thread.sleep(1_000);
				Platform.runLater(new Runnable(){
					@Override
					public void run(){
						setData(getLastClipboardFXContentData());
						setClipboardChangeListenerDisable(false);
					}
				});
			}
		});
		thread.start();
	}

	/**
	 * Convert {@link ClipboardFXData} into {@link ClipboardContent}
	 * and puts as content onto the clipboard.
	 * @param data The data content to put on the clipboard. If data is null, this method will ignore it.
	 */
	public void setData(ClipboardFXData data){
		if(data != null){
			var clipboardContent = new ClipboardContent();

			// Text
			if(data instanceof ClipboardFXTextData){
				var textData = (ClipboardFXTextData) data;
				clipboardContent.putString(textData.getText());
				systemClipboard.setContent(clipboardContent);
			}

			// Image
			else if(data instanceof ClipboardFXImageData){
				var imageData = (ClipboardFXImageData) data;
				clipboardContent.putImage(imageData.getImage());
				clipboardContent.putFiles(Collections.singletonList(imageData.getFile()));
				systemClipboard.setContent(clipboardContent);
			}

			// Video
			else if(data instanceof ClipboardFXRawData){
				var videoData = (ClipboardFXRawData) data;
				clipboardContent.putFiles(Collections.singletonList(videoData.getFile()));
				systemClipboard.setContent(clipboardContent);
			}
		}
	}

	/**
	 * Convert {@link ClipboardFXContentData} into {@link ClipboardContent}
	 * and puts as content onto the clipboard.
	 * @param data The data content to put on the clipboard. If data is null, this method will ignore it.
	 */
	public void setData(ClipboardFXContentData data){
		if(data != null){
			var clipboardContent = new ClipboardContent();

			// Text
			if(data.getTextContent() != null){
				String textData = data.getTextContent();
				clipboardContent.putString(textData);
			}

			// Image
			else if(data.getImageContent() != null){
				Image imageData = data.getImageContent();
				clipboardContent.putImage(imageData);
			}

			// File
			else if(data.getFileContent() != null){
				File fileData = data.getFileContent();
				clipboardContent.putFiles(Collections.singletonList(fileData));
			}

			systemClipboard.setContent(clipboardContent);
		}
	}

	/**
	 * Register a callback listener that will receive a callback
	 * instance of {@link ClipboardFXData}
	 * when there is a data change on clipboard.
	 * @param listener Callback interface
	 */
	public void registerClipboardChangeListener(ClipboardFXChangeListener listener){
		this.clipboardFXChangeListeners.add(listener);
	}

	/**
	 * Unregister a callback listener.
	 * @param listener Callback interface
	 */
	public void unregisterClipboardChangeListener(ClipboardFXChangeListener listener){
		this.clipboardFXChangeListeners.remove(listener);
	}

	/**
	 * Remove all callback listeners.
	 */
	public void clearClipboardChangeListener(){
		this.clipboardFXChangeListeners.clear();
	}

	// *****************************************************************************************
	// *** Field *******************************************************************************
	// *****************************************************************************************

	/**
	 * Record last clipboard data before clipboard make a change
	 * @return Last clipboard data
	 */
	public ObjectProperty<ClipboardFXContentData> lastClipboardContentDataProperty(){
		return lastClipboardContentData;
	}

	public ClipboardFXContentData getLastClipboardFXContentData(){
		return lastClipboardContentData.get();
	}

	public void setLastClipboardData(ClipboardFXContentData data){
		this.lastClipboardContentData.set(data);
	}

	/**
	 * Directory to store temprorary image that has not stored on computer
	 * @return Image cache directory
	 */
	public ObjectProperty<File> imageCacheDirProperty(){
		return imageCacheDir;
	}

	public File getImageCacheDir(){
		return imageCacheDir.get();
	}

	public void setImageCacheDir(File imageCacheDir){
		this.imageCacheDir.set(imageCacheDir);
	}

	/**
	 * If the value is {@code true}, the timer for listen clipboard change
	 * will disable and will enable again until it value change to {@code false}
	 *
	 * @return Boolean property of {@code isTimerDisable}
	 */
	public BooleanProperty timerDisableProperty(){
		return isClipboardChangeListenerDisable;
	}

	public boolean isClipboardChangeListenerDisable(){
		return isClipboardChangeListenerDisable.get();
	}

	public void setClipboardChangeListenerDisable(boolean value){
		this.isClipboardChangeListenerDisable.set(value);
	}
}
