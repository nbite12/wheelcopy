package com.carusel.app.manager;

import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceManager{
    // Singleton
    private static ResourceManager instance;
    public static ResourceManager getInstance(){
        if(instance == null){
            synchronized(ResourceManager.class){
                if(instance == null){
                    instance = new ResourceManager();
                }
            }
        }
        return instance;
    }

    // Constants
    private static final String IMAGE_DIR = "/image/";
    private static final String STYLE_DIR = "/style/";
    private static final String FILE_DIR = "/file/";

    // *****************************************************************************************
    // *** Image *******************************************************************************
    // *****************************************************************************************

    public Image loadImage(String fileName){
        String path = IMAGE_DIR + fileName;
        String url = getClass().getResource(path).toExternalForm();
        Image image = new Image(url);
        return image;
    }

    public Image loadImage(String fileName, double requestWidth, double requestHeight){
        String path = IMAGE_DIR + fileName;
        String url = getClass().getResource(path).toExternalForm();
        Image image = new Image(url, requestWidth, requestHeight, true, true);
        return image;
    }

    public Image loadImage(String fileName, double requestSize){
        return loadImage(fileName, requestSize, requestSize);
    }

    // *****************************************************************************************
    // *** Style *******************************************************************************
    // *****************************************************************************************

    public String loadStyle(String fileName){
        String path = STYLE_DIR + fileName;
        String url = getClass().getResource(path).toExternalForm();
        return url;
    }

    // *****************************************************************************************
    // *** File ********************************************************************************
    // *****************************************************************************************

    public File loadFile(String fileName){
        String path = FILE_DIR + fileName;
        //  URL url = getClass().getResource(path);
        String url = getClass().getResource(path).toExternalForm();
        InputStream inputStream = getClass().getResourceAsStream(path);
        // File file = new File(url.toURI());
        File file = new File(url);
        return file;

//        try{
//            String path = FILE_DIR + fileName;
//            //  URL url = getClass().getResource(path);
//            String url = getClass().getResource(path).toExternalForm();
//            // File file = new File(url.toURI());
//            File file = new File(url);
//            return file;
//
//        }catch(URISyntaxException e){
//            throw new RuntimeException(e.getMessage());
//        }
    }
}
