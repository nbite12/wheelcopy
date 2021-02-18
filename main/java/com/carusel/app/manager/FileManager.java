package com.carusel.app.manager;

import com.carusel.app.constants.AppConstants;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileManager{
    // Singleton
    private static FileManager instance;
    public static FileManager getInstance(){
        if(instance == null){
            synchronized(FileManager.class){
                if(instance == null){
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    // Field
    private final File caruselDir;

    // Constructor
    private FileManager(){
        String appLocalDataDir = System.getenv(AppConstants.APP_ENV_LOCAL);
        this.caruselDir = new File(appLocalDataDir, AppConstants.APP_NAME);
        if(!caruselDir.exists()){
            caruselDir.mkdir();
        }

        init();
    }

    // Initialize
    private void init(){

    }

    public void writeStringToFile(File file, String data){
        try{
            FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void writeByteArrayToFile(File file, byte[] data){
        try{
            FileUtils.writeByteArrayToFile(file, data);

        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String readFileToString(File file){
        try{
            if(file.exists()){
                String result = FileUtils.readFileToString(file, Charset.defaultCharset());
                return result;
            }

            return null;
        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] readFileToByteArray(File file){
        try{
            if(file.exists()){
                byte[] result = FileUtils.readFileToByteArray(file);
                return result;
            }

            return new byte[0];

        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public File createTempFile(File src){
        try{
//            String ext = FilenameUtils.getExtension(src.getAbsolutePath()).toLowerCase();
//            String uuid = UUID.randomUUID().toString();
//            String filaName = uuid + "." + ext;


            String filaName = src.getName();
            File dest = new File(caruselDir, filaName);
            if(!dest.exists()){
                // System.out.println("Copy file");
                FileUtils.copyFile(src, dest);
            }
            return dest;
        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isFileExist(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

    public Image getImage(File file){
        Image image = new Image(file.toURI().toString());
        return image;
    }

    public void saveImageToFile(Image image){
//        String imageFileName = RandomStringUtils.randomAlphabetic(5);
//        String imageFileExt = "png";
//        String imageFile = imageFileName + "." + imageFileExt;
//
//        File file = new File(caruselDir, imageFile);
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//        try{
//            ImageIO.write(bufferedImage, imageFileExt, file);
//        }catch(IOException e){
//            throw new RuntimeException(e);
//        }
    }

    public File getCaruselDir(){
        return caruselDir;
    }
}
