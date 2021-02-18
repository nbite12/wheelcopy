package com.carusel.app.lib.clipboardfx.util;

import com.carusel.app.lib.clipboardfx.constants.AppConstants;
import com.carusel.app.lib.clipboardfx.data.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public final class ClipboardFXUtil{
	/**
	 * Compare two images based on color pixel. But at first, this method
	 * will check the null value and image size.
	 * If both images are null, {@code true} is returned.
	 * If two images have different size, {@code false} is returned.
	 * @param firstImage First image to be compared
	 * @param secondImage Second image to be compared
	 * @return {@code true} if the two images are equal to each other and {@code false} otherwise
	 */
	public static boolean isImageEqual(Image firstImage, Image secondImage){
		// Prevent `NullPointerException`
		if(firstImage != null && secondImage == null) return false;
		if(firstImage == null) return secondImage == null;

		// Compare images size
		if(firstImage.getWidth() != secondImage.getWidth()) return false;
		if(firstImage.getHeight() != secondImage.getHeight()) return false;

		// Compare images color
		for(int x = 0; x < firstImage.getWidth(); x++){
			for(int y = 0; y < firstImage.getHeight(); y++){
				int firstArgb = firstImage.getPixelReader().getArgb(x, y);
				int secondArgb = secondImage.getPixelReader().getArgb(x, y);

				if(firstArgb != secondArgb) return false;
			}
		}

		return true;
	}

	/**
	 * Compare two files based on absolute path.
	 * If two images have different size, {@code false} is returned.
	 * @param firstFile First file to be compared
	 * @param secondFile Second file to be compared
	 * @return {@code true} if the two files are equal to each other and {@code false} otherwise
	 */
	public static boolean isFileEqual(File firstFile, File secondFile){
		// Prevent `NullPointerException`
		if(firstFile != null && secondFile == null) return false;
		if(firstFile == null) return secondFile == null;

		// Compare absolute path
		String firstPath = firstFile.getAbsolutePath();
		String secondPath = secondFile.getAbsolutePath();
		return firstPath.equals(secondPath);
	}

	public static ClipboardFXData convertToClipboardFXData(ClipboardFXContentData data, File dir){
		// Text
		if(data.getTextContent() != null){
			String text = data.getTextContent();

			var result = new ClipboardFXTextData(text);
			return result;
		}

		// Image
		else if(data.getImageContent() != null){
			Image image = data.getImageContent();
			File file = writeImageToFile(image, dir);

			var result = new ClipboardFXImageData(file, image);
			return result;
		}

		else if(data.getFileContent() != null){
			File file = data.getFileContent();
			String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();

			// Image
			if(AppConstants.IMAGE_EXTENSIONS.contains(ext)){
				try{
					String url = file.toURI().toURL().toExternalForm();
					Image image = new Image(url);
					var result = new ClipboardFXImageData(file, image);
					return result;
				}catch(MalformedURLException e){
					e.printStackTrace();
				}
			}

			// Raw
			var result = new ClipboardFXRawData(file);
			return result;
		}

		var result = new ClipboardFXEmptyData();
		return result;
	}

	/**
	 * Write an image to file with a name is random alphabetic string
	 * and the extension is png
	 * @param image Image to be written to file
	 * @return File associated with the image
	 * @throws RuntimeException if there is an error while writing the image to file
	 */
	public static File writeImageToFile(Image image, File dir){
		String imageFileName = RandomStringUtils.randomAlphabetic(10);
		String imageFileExt = "png";
		String imageFile = imageFileName + "." + imageFileExt;

		File file = new File(dir, imageFile);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		try{
			ImageIO.write(bufferedImage, imageFileExt, file);
			return file;
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}
