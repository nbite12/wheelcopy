package com.carusel.app.model.clipboard;

import com.carusel.app.constants.ClipboardType;
import com.carusel.app.usecase.ConvertVideoFileToClipboardDataUseCase;
import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;

public class ClipboardJson{
    private final String contentFilePath;
//    private final String thumbnailFilePath;
    private final String text;
    private final boolean isThumbnailSupported;
    private final ClipboardType clipboardType;

    // Constructor
    public ClipboardJson(String filePath,
//                         String thumbnailFilePath,
                         String text,
                         boolean isThumbnailSupported,
                         ClipboardType clipboardType){
        this.contentFilePath = filePath;
//        this.thumbnailFilePath = filePath;
        this.text = text;
        this.isThumbnailSupported = isThumbnailSupported;
        this.clipboardType = clipboardType;
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // Thumnail file path
    public String getFilePath(){
        return contentFilePath;
    }

//    // Thumnail file path
//    public String getThumbnailFilePath(){
//        return thumbnailFilePath;
//    }

    // Text
    public String getText(){
        return text;
    }

    // Thumbnail supported
    public boolean isThumbnailSupported(){
        return isThumbnailSupported;
    }

    // Clipboard type
    public ClipboardType getClipboardType(){
        return clipboardType;
    }

    // *****************************************************************************************
    // *** Converter ***************************************************************************
    // *****************************************************************************************

    public static ClipboardData getClipboardRaw(ClipboardJson clipboardJson){
        if(clipboardJson.getClipboardType() == ClipboardType.VIDEO){
            File file = new File(clipboardJson.getFilePath());
            return new ConvertVideoFileToClipboardDataUseCase(file).execute();
        }

        ClipboardData clipboardData = new ClipboardData();
        clipboardData.setClipboardType(clipboardJson.clipboardType);
        clipboardData.setContentFile(new File(clipboardJson.getFilePath()));
        if(clipboardJson.getClipboardType() == ClipboardType.PICTURE){
            try{
                File file = new File(clipboardJson.getFilePath());
                String url = file.toURI().toURL().toExternalForm();
                Image image = new Image(url);
                clipboardData.setThumbnail(image);
                clipboardData.setThumbnailSupported(true);
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
        }
        else if(clipboardJson.getClipboardType() == ClipboardType.TEXT){
            String text = clipboardJson.getText();
            clipboardData.setText(text);
        }
        else if(clipboardJson.getClipboardType() == ClipboardType.DOCUMENT){
            File file = new File(clipboardJson.getFilePath());
            clipboardData.setContentFile(file);
        }

        return clipboardData;
    }

    public static ClipboardJson getClipboardJson(ClipboardData clipboardData){
        ClipboardJson clipboardJson = new ClipboardJson(
                clipboardData.getContentFilePath(),
//                clipboardRaw.getThumbnailFilePath(),
                clipboardData.getText(),
                clipboardData.isThumbnailSupported(),
                clipboardData.getClipboardType()
        );

        return clipboardJson;
    }
}
