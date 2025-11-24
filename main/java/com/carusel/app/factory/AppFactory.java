package com.carusel.app.factory;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.manager.ResourceManager;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class AppFactory{
    // Constants
    private static final double POSITION_RADIUS = 60.0;
    private static final double CIRCLE_RADIUS = 14.0;

    // CSS
    public static final String SETTING_CONTAINER = "setting-container";
    public static final String SETTING_BASE_CIRCLE = "setting-base-circle";
    public static final String TRIAL_LIMIT_CONTAINER = "trial-limit-container";
    public static final String TRIAL_LIMIT_RECT = "trial-limit-rect";
    public static final String TRIAL_LIMIT_TEXT = "trial-limit-text";

    // Setting button
    public static Group createSettingButton(){
        Group container = new Group();
        container.getStyleClass().add(SETTING_CONTAINER);

        Circle baseCircle = new Circle(CIRCLE_RADIUS);
        baseCircle.getStyleClass().add(SETTING_BASE_CIRCLE);
        baseCircle.setTranslateY(POSITION_RADIUS);
        container.getChildren().add(baseCircle);

        Image iconImage = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_SETTING, 14);
        ImageView iconView = new ImageView(iconImage);
        iconView.setDisable(true);
        iconView.setTranslateX(-iconImage.getWidth() / 2.0);
        iconView.setTranslateY(POSITION_RADIUS - iconImage.getHeight() / 2.0);
        container.getChildren().add(iconView);

        return container;
    }

    // Trial limit
    public static Group createTrialLimit(){
        Group container = new Group();
        container.getStyleClass().add(TRIAL_LIMIT_CONTAINER);

        Rectangle rect = new Rectangle(-75, -25, 150, 50);
        rect.getStyleClass().add(TRIAL_LIMIT_RECT);
        container.getChildren().add(rect);

        Text text = new Text("Testing");
        text.setFill(javafx.scene.paint.Color.YELLOW);
        text.setTranslateX(-27.5);
        text.setTranslateY(5);
        text.getStyleClass().add(TRIAL_LIMIT_TEXT);
        container.getChildren().add(text);

        return container;
    }
}
