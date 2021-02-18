package com.carusel.app.model;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.ClipboardType;
import com.carusel.app.factory.ElementFactory;
import com.carusel.app.lib.clipboardfx.ClipboardFX;
import com.carusel.app.lib.clipboardfx.data.ClipboardFXData;
import com.carusel.app.manager.*;
import com.carusel.app.model.clipboard.ClipboardData;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Element{
    private final int index;
    private final Group containerBase;
    private final Group containerMain;
    private final Path clipImage;
    private final Text clipText;
    private final ImageView iconView;
    private final ImageView lockView;
    private final ImageView pinView;
    private final ImageView trashView;

    private final Timeline moveOutAnim;
    private final Timeline moveInAnima;

    private final Tooltip tooltip;

    // Property
    private final BooleanProperty isSelected;
    private final BooleanProperty isEmpty;
    private final BooleanProperty isPinned;
    private final BooleanProperty isLocked;
    private final ObjectProperty<ClipboardData> clipboardData;

    // CSS
    private static final String ELEMENT_CONTAINER_SELECTED_CLASS = "element-container-selected";

    // Constructor
    public Element(int index,
                   Group containerBase, Group containerMain,
                   Path clipImage, Text clipText,
                   ImageView iconView, ImageView lockView, ImageView pinView, ImageView trashView,
                   Timeline moveOutAnim, Timeline moveInAnima,
                   boolean isLocked,
                   ObjectProperty<ClipboardData> clipboardData){

        this.index = index;
        this.containerBase = containerBase;
        this.containerMain = containerMain;
        this.clipImage = clipImage;
        this.clipText = clipText;
        this.iconView = iconView;
        this.lockView = lockView;
        this.pinView = pinView;
        this.trashView = trashView;

        this.moveOutAnim = moveOutAnim;
        this.moveInAnima = moveInAnima;

        this.isSelected = new SimpleBooleanProperty();
        this.isEmpty = new SimpleBooleanProperty(true);
        this.tooltip = new Tooltip();
        this.isLocked = new SimpleBooleanProperty(isLocked);
        this.isPinned = new SimpleBooleanProperty();
        this.clipboardData = clipboardData;

        init();
    }

    // Initialize
    private void init(){
        initGeneral();
        initContainer();
        listenClipboardDataChange();
    }

    private void listenClipboardDataChange(){
        clipboardDataProperty().addListener(new ChangeListener<ClipboardData>(){
            @Override
            public void changed(ObservableValue<? extends ClipboardData> observable, ClipboardData oldValue, ClipboardData newValue){
                setContent(newValue);
            }
        });
    }

    private void initGeneral(){
        lockView.visibleProperty().bind(isLocked);
//        pinView.setVisible(false);

        pinView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                if(isPinned()){
                    setPinned(false);
                    Image image = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_PIN_BLACK, 20);
                    pinView.setImage(image);
//                    pinView.getTransforms().add(new Rotate(45, 0, 0));
                }

                else{
                    setPinned(true);
                    Image image = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_PIN, 20);
                    pinView.setImage(image);
//                    pinView.getTransforms().clear();
                }
            }
        });

        trashView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                deleteContent();
            }
        });
    }

    private void initContainer(){
        containerMain.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                if(!isSelected() && !isLocked()){
                    // Left click
                    if(event.getButton() == MouseButton.PRIMARY){
                        select();
                    }
                }

                else if(isLocked.get()){
                    AppManager appManager = AppManager.getInstance();
                    appManager.openUrl("https://www.google.com/");
                }
            }
        });

        containerMain.setOnMouseEntered(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                if(tooltip.getText() != null && !tooltip.getText().isEmpty()){
                    Tooltip.install(containerMain, tooltip);
                }
            }
        });

        containerMain.setOnMouseExited(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                Tooltip.uninstall(containerMain, tooltip);
            }
        });
    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public void select(){
        setSelected(true);
        moveOutAnim.play();
        containerMain.getStyleClass().add(ELEMENT_CONTAINER_SELECTED_CLASS);

        ClipboardData clipboardData = getClipboardData();
        if(clipboardData != null && clipboardData.getClipboardType() != ClipboardType.EMPTY){
            ClipboardManager clipboardManager = ClipboardManager.getInstance();
            StageManager stageManager = StageManager.getInstance();

            // Hide the app first
            // before paste the data

            stageManager.hideStage();
            // stageManager.hideStageOnly();

            // Put data on the clipboard
            // and trigger paste event
            ClipboardFX clipboardFX = clipboardManager.getClipboardFX();
            ClipboardFXData clipboardFXData = clipboardManager.convertToClipboardFXData(clipboardData);
            clipboardFX.paste(clipboardFXData);
        }

        // Unselect other element
        for(OnSelectedListener listener : onSelectedListeners){
            listener.onSelected(this);
        }
    }

    public void unselect(){
        setSelected(false);
        moveOutAnim.stop();
        moveInAnima.play();
        containerMain.getStyleClass().remove(ELEMENT_CONTAINER_SELECTED_CLASS);
    }

    // *****************************************************************************************
    // *** Content *****************************************************************************
    // *****************************************************************************************

    private void setContent(ClipboardData clipboardData){
        if(clipboardData != null && clipboardData.getClipboardType() != ClipboardType.EMPTY){
            setClipboardData(clipboardData);

            // Text
            if(clipboardData.getClipboardType() == ClipboardType.TEXT){
                setEmpty(false);

                setClipTextContent(clipboardData.getText());
                setTooltip(clipboardData.getText());
                clearIconType();
            }

            // Image
            else if(clipboardData.getClipboardType() == ClipboardType.PICTURE){
                setEmpty(false);

                setClipImageContent(clipboardData.getThumbnail());
                setTooltip(clipboardData.getContentFile().getAbsolutePath());
                setIconType(getIconTypeImage());
            }

            // Video
            else if(clipboardData.getClipboardType() == ClipboardType.VIDEO){
                setEmpty(false);

                if(clipboardData.isThumbnailSupported()) setClipImageContent(clipboardData.getThumbnail());
                else setClipTextContent(clipboardData.getContentFile().getAbsolutePath());

                setTooltip(clipboardData.getContentFile().getAbsolutePath());
                setIconType(getIconTypeVideo());
            }

            // Document
            else if(clipboardData.getClipboardType() == ClipboardType.DOCUMENT){
                setEmpty(false);

                setClipTextContent(clipboardData.getContentFile().getAbsolutePath());
                setTooltip(clipboardData.getContentFile().getAbsolutePath());
                setIconType(getIconTypeDocument());
            }
        }
    }

    private void deleteContent(){
        ClipboardData clipboardData = getClipboardData();
        if(clipboardData != null){
            setEmpty(true);
            setClipboardData(null);

            clearClipTextContent();
            clearClipImageContent();
            clearIconType();
            cleatTooltip();

            WheelManager wheelManager = WheelManager.getInstance();
            wheelManager.removeClipboard(getIndex());
        }
    }

    // *****************************************************************************************
    // *** Tooltip *****************************************************************************
    // *****************************************************************************************

    private void setTooltip(String text){
        tooltip.setText(getFormatString(text, 75, 5));
    }

    // *****************************************************************************************
    // *** Clip ********************************************************************************
    // *****************************************************************************************

    private void clearClipTextContent(){
        clipText.setTranslateX(clipText.getTranslateX() + clipText.getLayoutBounds().getWidth() / 2);
        clipText.setTranslateY(clipText.getTranslateY() + clipText.getLayoutBounds().getDepth() / 2);
        clipText.setText(null);
    }

    private void clearClipImageContent(){
        clipImage.setFill(null);
    }

    private void clearIconType(){
        // iconView.setImage(null);
        iconView.setVisible(false);
    }

    private void cleatTooltip(){
        tooltip.setText(null);
    }


    private void setClipTextContent(String content){
        clearClipTextContent();
        clearClipImageContent();

        clipText.setText(getFormatString(content, 12, 2));
        clipText.setTranslateX(clipText.getTranslateX() - clipText.getLayoutBounds().getWidth() / 2);
        clipText.setTranslateY(clipText.getTranslateY() - clipText.getLayoutBounds().getDepth() / 2);
    }

    private void setClipImageContent(Image content){
        clearClipTextContent();
        clearClipImageContent();

        if(content != null){
            clipImage.setFill(new ImagePattern(content));
        }
    }

    // *****************************************************************************************
    // *** Icon Type ***************************************************************************
    // *****************************************************************************************

    private Image getIconTypeText(){
        String fileName = AppConstants.IMAGE_ELEMENT_TYPE_TEXT;
        return getIconType(fileName);
    }

    private Image getIconTypeImage(){
        String fileName = AppConstants.IMAGE_ELEMENT_TYPE_IMAGE;
        return getIconType(fileName);
    }

    private Image getIconTypeVideo(){
        String fileName = AppConstants.IMAGE_ELEMENT_TYPE_VIDEO;
        return getIconType(fileName);
    }

    private Image getIconTypeDocument(){
        String fileName = AppConstants.IMAGE_ELEMENT_TYPE_DOCUMENT;
        return getIconType(fileName);
    }

    private Image getIconType(String fileName){
        ResourceManager resourceManager = ResourceManager.getInstance();
        double size = ElementFactory.ELEMENT_ICON_SIZE;
        Image image = resourceManager.loadImage(fileName, size);
        return image;
    }

    private void setIconType(Image image){
        iconView.setImage(image);
        iconView.setVisible(true);
    }

    // *****************************************************************************************
    // *** Helper ******************************************************************************
    // *****************************************************************************************

    private String getFormatString(String content, int letterLimit, int lineLimit){
        // Replace new line with white space
        content = content
                .replace("\r\n", " ")
                .replace("\n", " ")
                .replace("\t", " ");

        StringJoiner stringJoiner = new StringJoiner("\n");

        int startIndex = 0;
        int counter = 0;

        while(startIndex < content.length()){
            if(counter < lineLimit){
                if(startIndex + letterLimit - 1 < content.length()){
                    String substring = content.substring(startIndex, startIndex + letterLimit);
                    stringJoiner.add(substring);
                }

                else{
                    int remainingLetter = content.length() - startIndex;
                    String substring = content.substring(startIndex, startIndex + remainingLetter);
                    stringJoiner.add(substring);
                }
            }else{
                stringJoiner.add("...");
                break;
            }

            startIndex += letterLimit;
            counter++;
        }

        String format = stringJoiner.toString();
        return format;
    }

    // *****************************************************************************************
    // *** Fields ******************************************************************************
    // *****************************************************************************************

    public int getIndex(){
        return index;
    }

    public Node getNode(){
        return containerBase;
    }

    // Selected
    public boolean isSelected(){
        return isSelected.get();
    }
    public void setSelected(boolean isSelected){
        this.isSelected.set(isSelected);
    }

    // Empty
    public boolean isEmpty(){
        return isEmpty.get();
    }
    public void setEmpty(boolean empty){
        isEmpty.set(empty);
    }

    public boolean isLocked(){
        return isLocked.get();
    }

    // Pinned
    public BooleanProperty isPinnedProperty(){
        return isPinned;
    }
    public boolean isPinned(){
        return isPinned.get();
    }
    public void setPinned(boolean isPinned){
        this.isPinned.set(isPinned);
    }

    public BooleanProperty isLockedProperty(){
        return isLocked;
    }

    public void unlock(){
        this.isLocked.set(true);
    }

    // Clipboard data
    public ObjectProperty<ClipboardData> clipboardDataProperty(){
        return clipboardData;
    }
    public ClipboardData getClipboardData(){
        return clipboardData.get();
    }
    public void setClipboardData(ClipboardData clipboardData){
        this.clipboardData.set(clipboardData);
    }
    // *****************************************************************************************
    // *** Listener ****************************************************************************
    // *****************************************************************************************

    public interface OnSelectedListener{
        void onSelected(Element selectedElement);
    }
    private final List<OnSelectedListener> onSelectedListeners = new ArrayList<>();
    public void registerListener(OnSelectedListener listener){
        onSelectedListeners.add(listener);
    }
}
