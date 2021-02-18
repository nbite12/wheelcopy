package com.carusel.app.factory;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.UserType;
import com.carusel.app.constants.WheelIndex;
import com.carusel.app.manager.DatabaseManager;
import com.carusel.app.manager.ResourceManager;
import com.carusel.app.model.Database;
import com.carusel.app.model.Navigation;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationFactory{
    // Constants
    private static final double POSITION_RADIUS = 95.0;
    private static final double CIRCLE_RADIUS = 14.0;
    private static final double INNER_LINE_RADIUS = 12.0;
    private static final double ICON_SIZE = 12.0;
    private static final double NAVIGATION_GAP = 35.0;
    private static final List<Double> NAVIGATION_GAP_POSITIONS = Arrays.asList(1.5, 0.5, -0.5, -1.5);
    private static final List<WheelIndex> NAVIGATION_WHEEL_INDEX = Arrays.asList(WheelIndex.values());

    // CSS
    private static final String NAVIGATION_CONTAINER_CLASS = "navigation-container";
    private static final String NAVIGATION_BASE_CIRCLE_CLASS = "navigation-base-circle";
    private static final String NAVIGATION_INNER_CIRCLE_CLASS = "navigation-inner-circle";

    public static List<Navigation> createNavigations(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        UserType userType = database.getUserType();

        List<Navigation> navigations = new ArrayList<>();

        int counter = 0;
        for(Double gapPosition : NAVIGATION_GAP_POSITIONS){
            Group container = new Group();
            container.getStyleClass().add(NAVIGATION_CONTAINER_CLASS);

            double position = gapPosition * NAVIGATION_GAP;
            double angle = position / (Math.PI * POSITION_RADIUS) * 180.0;
            double radian = -angle * Math.PI / 180.0;
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);

            Circle baseCircle = new Circle(CIRCLE_RADIUS);
            baseCircle.getStyleClass().add(NAVIGATION_BASE_CIRCLE_CLASS);
            baseCircle.setTranslateX(sin * POSITION_RADIUS);
            baseCircle.setTranslateY(cos * POSITION_RADIUS);
            container.getChildren().add(baseCircle);

            Circle innerCircle = new Circle(INNER_LINE_RADIUS);
            innerCircle.getStyleClass().add(NAVIGATION_INNER_CIRCLE_CLASS);
            innerCircle.setTranslateX(sin * POSITION_RADIUS);
            innerCircle.setTranslateY(cos * POSITION_RADIUS);
            container.getChildren().add(innerCircle);

            // Lock view
            Image lockImage = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_LOCK, ICON_SIZE);
            ImageView lockView = new ImageView(lockImage);
            lockView.setDisable(true);
            lockView.setTranslateX(sin * POSITION_RADIUS - lockImage.getWidth() / 2.0);
            lockView.setTranslateY(cos * POSITION_RADIUS - lockImage.getHeight() / 2.0);
            container.getChildren().add(lockView);

            WheelIndex wheelIndex = NAVIGATION_WHEEL_INDEX.get(counter);
            boolean isLocked = userType.getNavigationLockMap().get(wheelIndex);
            boolean isSelected = counter == 0;

            Navigation navigation = new Navigation(wheelIndex, container, lockView, isSelected, isLocked);
            navigations.add(navigation);

            counter++;
        }

        return navigations;
    }
}
