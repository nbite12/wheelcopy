package com.carusel.app.model.clipboard;

import com.carusel.app.constants.ClipboardType;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import lombok.ToString;

import java.io.File;
import java.util.Objects;

@ToString
public class ClipboardData{
    private final StringProperty text;
    //    private final ObjectProperty<File> thumbnailFile;
    private final ObjectProperty<Image> thumbnail;
    private final BooleanProperty isThumbnailSupported;
    private final ObjectProperty<File> contentFile;
    private final ObjectProperty<ClipboardType> clipboardType;
    // private ClipboardType clipboardType;

    // Constructor
    public ClipboardData(){
        this.contentFile = new SimpleObjectProperty<>();
//        this.thumbnailFile = new SimpleObjectProperty<>();
        this.thumbnail = new SimpleObjectProperty<>();
        this.text = new SimpleStringProperty();
        this.isThumbnailSupported = new SimpleBooleanProperty();
        this.clipboardType = new SimpleObjectProperty<>(ClipboardType.EMPTY);
        // this.clipboardType = ClipboardType.EMPTY;
    }

    // *****************************************************************************************
    // *** Override ****************************************************************************
    // *****************************************************************************************

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ClipboardData that = (ClipboardData) o;

        // Text
        if(getClipboardType() == ClipboardType.TEXT){
            return Objects.equals(getText(), that.getText());
        }

        // Other
        if(getContentFile() == null){
            return that.getContentFile() == null;
        }
        else{
            if(that.getContentFile() == null) return false;
            else{
                if(getClipboardType() == ClipboardType.PICTURE){
                    return Objects.equals(getContentFile().getName(), that.getContentFile().getName());
                }

                return Objects.equals(getContentFile().getAbsolutePath(), that.getContentFile().getAbsolutePath());
            }
        }
    }

    @Override
    public int hashCode(){
        return Objects.hash(contentFile, thumbnail, text, isThumbnailSupported, clipboardType);
    }

    // *****************************************************************************************
    // *** Field *******************************************************************************
    // *****************************************************************************************

    // Content file
    public ObjectProperty<File> contentFileProperty(){
        return contentFile;
    }
    public File getContentFile(){
        return contentFile.get();
    }
    public void setContentFile(File contentFile){
        this.contentFile.set(contentFile);
    }
    public String getContentFilePath(){
        if(contentFile.get() != null){
            return contentFile.get().getAbsolutePath();
        }

        return "";
    }

//    // Thumnail file
//    public File getThumbnailFile(){
//        return thumbnailFile.get();
//    }
//    public void setThumbnailFile(File thumbnailFile){
//        this.thumbnailFile.set(thumbnailFile);
//    }
//    public String getThumbnailFilePath(){
//        if(thumbnailFile.get() != null){
//            return thumbnailFile.get().getAbsolutePath();
//        }
//
//        return "";
//    }

    // Thumbnail
    public ObjectProperty<Image> thumbnailProperty(){
        return thumbnail;
    }
    public Image getThumbnail(){
        return thumbnail.get();
    }
    public void setThumbnail(Image thumbnail){
        this.thumbnail.set(thumbnail);
    }

    // Text
    public StringProperty textProperty(){
        return text;
    }
    public String getText(){
        return text.get();
    }
    public void setText(String text){
        this.text.set(text);
    }

    // Thumbnail supported
    public boolean isThumbnailSupported(){
        return isThumbnailSupported.get();
    }
    public void setThumbnailSupported(boolean isThumbnailSupported){
        this.isThumbnailSupported.set(isThumbnailSupported);
    }

    // Clipboard type
    public ObjectProperty<ClipboardType> clipboardTypeProperty(){
        return clipboardType;
    }
    public ClipboardType getClipboardType(){
        return clipboardType.get();
    }
    public void setClipboardType(ClipboardType clipboardType){
        this.clipboardType.set(clipboardType);
    }

    public void bind(ClipboardData clipboardData){
        setClipboardType(clipboardData.getClipboardType());
        setContentFile(clipboardData.getContentFile());
        setThumbnail(clipboardData.getThumbnail());
        setThumbnailSupported(clipboardData.isThumbnailSupported());
        setText(clipboardData.getText());
    }
}
