package com.carusel.app.factory;

import com.carusel.app.constants.AppConstants;
import com.carusel.app.constants.UserType;
import com.carusel.app.constants.WheelIndex;
import com.carusel.app.manager.DatabaseManager;
import com.carusel.app.manager.ResourceManager;
import com.carusel.app.model.Database;
import com.carusel.app.model.Element;
import com.carusel.app.model.clipboard.ClipboardData;
import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ElementFactory{
    // Constants
    private static final double CENTER = 0.0;
    private static final double INNER_RADIUS = 100.0;
    private static final double OUTER_RADIUS = 250.0;
    private static final double CENTER_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0;
    private static final double TRANSLATE = 15.0;
    private static final double WHEEL_ROTATION = -45.0 - 45.0 / 2;
    private static final double ANGLE_PER_SEGMENT = 45.0;
    public static final double TOTAL_ELEMENT = 8;
    public static final double ELEMENT_ICON_SIZE = 60.0;

    // CSS
    private static final String ELEMENT_CONTAINER_CLASS = "element-container";
    private static final String ELEMENT_MAIN_CLASS = "element-main";
    private static final String ELEMENT_CLIP_IMAGE_CLASS = "element-clip-image";
    private static final String ELEMENT_CLIP_TEXT_CLASS = "element-clip-text";
    private static final String ELEMENT_INNER_LINE_CLASS = "element-inner-line";
    private static final String ELEMENT_ICON_VIEW_CLASS = "element-icon-view";
    private static final String ELEMENT_OVERLAY_CLASS = "element-overlay";

    public static Element createElement(WheelIndex wheelIndex, Integer elementIndex, ObjectProperty<ClipboardData> clipboardData){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        UserType userType = database.getUserType();

        double rotation = elementIndex * ANGLE_PER_SEGMENT;
        double baseRotationAngle = rotation + WHEEL_ROTATION;

        double centralRotationAngle = rotation - ANGLE_PER_SEGMENT / 2 + WHEEL_ROTATION;
        double centralRotationRadian = -centralRotationAngle * Math.PI / 180.0;
        double centralRotationCos = Math.cos(centralRotationRadian);
        double centralRotationSin = -Math.sin(centralRotationRadian);

        Path elementMain = createElementMain(baseRotationAngle, centralRotationCos, centralRotationSin);
        Path clipImage = createClipImage(baseRotationAngle, centralRotationCos, centralRotationSin);
        Text clipText = createClipText(centralRotationCos, centralRotationSin);
        Path innerLine = createInnerLine(baseRotationAngle, centralRotationCos, centralRotationSin);
        ImageView iconView = createIconView(centralRotationCos, centralRotationSin);
        ImageView lockView = createLockView(centralRotationCos, centralRotationSin);
        ImageView pinView = createPinView(rotation);
        ImageView trashView = createTrashView(rotation);

        Group containerMain = new Group(clipImage, elementMain, innerLine, clipText, iconView, lockView);
        containerMain.getStyleClass().add(ELEMENT_CONTAINER_CLASS);
        Group containerOverlay = new Group(pinView, trashView);

        Group containerBase = new Group(containerMain, containerOverlay);

        // Ripple effect
        addRippleEfect(containerMain, baseRotationAngle, centralRotationCos, centralRotationSin);

        Timeline moveOutAnim = AnimationFactory.getMoveOutElementAnimation(containerBase, centralRotationCos, centralRotationSin, TRANSLATE);
        Timeline moveInAnim = AnimationFactory.getMoveInElementAnimation(containerBase);

        boolean isLocked = userType.getElementLockMap().get(wheelIndex).get(elementIndex);

        Element element = new Element(
                elementIndex,
                containerBase,
                containerMain,
                clipImage,
                clipText,
                iconView,
                lockView,
                pinView,
                trashView,

                moveOutAnim,
                moveInAnim,

                isLocked,
                clipboardData
        );

        return element;
    }

    public static List<Element> createElements(WheelIndex wheelIndex){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Database database = databaseManager.getDatabase();
        UserType userType = database.getUserType();

        List<Element> elements = new ArrayList<>();

        for(int elementIndex = 0; elementIndex < TOTAL_ELEMENT; elementIndex++){
            double rotation = elementIndex * ANGLE_PER_SEGMENT;
            double baseRotationAngle = rotation + WHEEL_ROTATION;

            double centralRotationAngle = rotation - ANGLE_PER_SEGMENT / 2 + WHEEL_ROTATION;
            double centralRotationRadian = -centralRotationAngle * Math.PI / 180.0;
            double centralRotationCos = Math.cos(centralRotationRadian);
            double centralRotationSin = -Math.sin(centralRotationRadian);

            Path elementMain = createElementMain(baseRotationAngle, centralRotationCos, centralRotationSin);
            Path clipImage = createClipImage(baseRotationAngle, centralRotationCos, centralRotationSin);
            Text clipText = createClipText(centralRotationCos, centralRotationSin);
            Path innerLine = createInnerLine(baseRotationAngle, centralRotationCos, centralRotationSin);
            ImageView iconView = createIconView(centralRotationCos, centralRotationSin);
            ImageView lockView = createLockView(centralRotationCos, centralRotationSin);
            ImageView pinView = createPinView(rotation);
            ImageView trashView = createTrashView(rotation);

            Group containerMain = new Group(clipImage, elementMain, innerLine, clipText, iconView, lockView);
            containerMain.getStyleClass().add(ELEMENT_CONTAINER_CLASS);
            Group containerOverlay = new Group(pinView, trashView);

            Group containerBase = new Group(containerMain, containerOverlay);

            // Ripple effect
            addRippleEfect(containerMain, baseRotationAngle, centralRotationCos, centralRotationSin);

            Timeline moveOutAnim = AnimationFactory.getMoveOutElementAnimation(containerBase, centralRotationCos, centralRotationSin, TRANSLATE);
            Timeline moveInAnim = AnimationFactory.getMoveInElementAnimation(containerBase);

            boolean isLocked = userType.getElementLockMap().get(wheelIndex).get(elementIndex);

//            WheelManager wheelManager = WheelManager.getInstance();
//            ClipboardData clipboardData = wheelManager.getClipboardData(wheelIndex, i);

            Element element = new Element(
                    elementIndex,
                    containerBase,
                    containerMain,
                    clipImage,
                    clipText,
                    iconView,
                    lockView,
                    pinView,
                    trashView,

                    moveOutAnim,
                    moveInAnim,

                    isLocked,
                    null
            );

            elements.add(element);
        }

        return elements;
    }

    // Element main
    private static Path createElementMain(double baseRotationAngle, double centralRotationCos, double centralRotationSin){
        Path elementMain = new Path();
        elementMain.setFillRule(FillRule.EVEN_ODD);
        elementMain.getStyleClass().add(ELEMENT_MAIN_CLASS);
        elementMain.getElements().addAll(getSegmentedCircle(0));
        elementMain.setTranslateX(centralRotationCos * TRANSLATE);
        elementMain.setTranslateY(centralRotationSin * TRANSLATE);
        elementMain.getTransforms().add(new Rotate(baseRotationAngle, CENTER, CENTER));
        return elementMain;
    }

    // Clip image
    private static Path createClipImage(double baseRotationAngle, double centralRotationCos, double centralRotationSin){
        Path clipImage = new Path();
        clipImage.setFillRule(FillRule.EVEN_ODD);
        clipImage.getStyleClass().add(ELEMENT_CLIP_IMAGE_CLASS);
        clipImage.getElements().addAll(getSegmentedCircle(12.5));
        clipImage.setTranslateX(centralRotationCos * TRANSLATE);
        clipImage.setTranslateY(centralRotationSin * TRANSLATE);
        clipImage.getTransforms().add(new Rotate(baseRotationAngle, CENTER, CENTER));
        return clipImage;
    }

    // Clip text
    private static Text createClipText(double centralRotationCos, double centralRotationSin){
        Text clipText = new Text();
        clipText.getStyleClass().add(ELEMENT_CLIP_TEXT_CLASS);
        clipText.setTranslateX(centralRotationCos * (CENTER_RADIUS + TRANSLATE));
        clipText.setTranslateY(centralRotationSin * (CENTER_RADIUS + TRANSLATE));
        return clipText;
    }

    // Inner line
    private static Path createInnerLine(double baseRotationAngle, double centralRotationCos, double centralRotationSin){
        Path innerLine = new Path();
        innerLine.getStyleClass().add(ELEMENT_INNER_LINE_CLASS);
        innerLine.getElements().addAll(getSegmentedCircle(5));
        innerLine.setTranslateX(centralRotationCos * TRANSLATE);
        innerLine.setTranslateY(centralRotationSin * TRANSLATE);
        innerLine.getTransforms().add(new Rotate(baseRotationAngle, CENTER, CENTER));
        return innerLine;
    }

    // Icon view
    private static ImageView createIconView(double centralRotationCos, double centralRotationSin){
        ImageView iconView = new ImageView();
        iconView.getStyleClass().add(ELEMENT_ICON_VIEW_CLASS);
        iconView.setTranslateX(centralRotationCos * (CENTER_RADIUS + TRANSLATE) - ELEMENT_ICON_SIZE / 2.0);
        iconView.setTranslateY(centralRotationSin * (CENTER_RADIUS + TRANSLATE) - ELEMENT_ICON_SIZE / 2.0);
        return iconView;
    }

    // Lock view
    private static ImageView createLockView(double centralRotationCos, double centralRotationSin){
        Image lockImage = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_LOCK, 20);
        ImageView lockView = new ImageView(lockImage);
        lockView.setTranslateX(centralRotationCos * (CENTER_RADIUS + TRANSLATE) - lockImage.getWidth() / 2.0);
        lockView.setTranslateY(centralRotationSin * (CENTER_RADIUS + TRANSLATE) - lockImage.getHeight() / 2.0);
        return lockView;
    }

    // Pin view
    private static ImageView createPinView(double rotation){
        double angle = rotation - 39 + WHEEL_ROTATION;
        double radian = -angle * Math.PI / 180.0;
        double cos = Math.cos(radian);
        double sin = -Math.sin(radian);
        double size = 16;

        Image image = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_PIN_BLACK, size);
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add(ELEMENT_OVERLAY_CLASS);
        imageView.setTranslateX(cos * (OUTER_RADIUS + TRANSLATE - size) - image.getWidth() / 2.0);
        imageView.setTranslateY(sin * (OUTER_RADIUS + TRANSLATE - size) - image.getHeight() / 2.0);
        return imageView;
    }

    // Delete view
    private static ImageView createTrashView(double rotation){
        double angle = rotation - 6 + WHEEL_ROTATION;
        double radian = -angle * Math.PI / 180.0;
        double cos = Math.cos(radian);
        double sin = -Math.sin(radian);
        double size = 16;

        Image image = ResourceManager.getInstance().loadImage(AppConstants.IMAGE_TRASH_BLACK, size);
        ImageView lockView = new ImageView(image);
        lockView.getStyleClass().add(ELEMENT_OVERLAY_CLASS);
        lockView.setTranslateX(cos * (OUTER_RADIUS + TRANSLATE - size) - image.getWidth() / 2.0);
        lockView.setTranslateY(sin * (OUTER_RADIUS + TRANSLATE - size) - image.getHeight() / 2.0);
        return lockView;
    }

    // Segmented circle
    private static List<PathElement> getSegmentedCircle(double offset){
        double innerRadiusWithOffset = INNER_RADIUS + offset;
        double outerRadiusWithOffset = OUTER_RADIUS - offset;
        double radian = ANGLE_PER_SEGMENT * Math.PI / 180.0;
        double angleCos = Math.cos(radian);
        double angleSin = Math.sin(radian);

        MoveTo bottomLeftPoint = new MoveTo();
        bottomLeftPoint.setX(CENTER + innerRadiusWithOffset);
        bottomLeftPoint.setY(CENTER - offset);

        HLineTo horizontalLine = new HLineTo();
        horizontalLine.setX(CENTER + outerRadiusWithOffset);

        ArcTo outerArc = new ArcTo();
        outerArc.setX(CENTER + angleCos * outerRadiusWithOffset + angleCos * offset);
        outerArc.setY(CENTER - angleSin * outerRadiusWithOffset + angleSin * offset);
        outerArc.setRadiusX(OUTER_RADIUS);
        outerArc.setRadiusY(OUTER_RADIUS);

        LineTo line = new LineTo();
        line.setX(CENTER + angleCos * innerRadiusWithOffset + angleCos * offset);
        line.setY(CENTER - angleSin * innerRadiusWithOffset + angleSin * offset);

        ArcTo innerArc = new ArcTo();
        innerArc.setSweepFlag(true);
        innerArc.setX(bottomLeftPoint.getX());
        innerArc.setY(bottomLeftPoint.getY());
        innerArc.setRadiusX(INNER_RADIUS);
        innerArc.setRadiusY(INNER_RADIUS);

        List<PathElement> pathElements = new ArrayList<>();
        pathElements.add(bottomLeftPoint);
        pathElements.add(horizontalLine);
        pathElements.add(outerArc);
        pathElements.add(line);
        pathElements.add(innerArc);
        return pathElements;
    }

    // Ripple effect
    private static void addRippleEfect(Group container, double baseRotationAngle, double centralRotationCos, double centralRotationSin){
        // Clip
        Path clip = createElementMain(baseRotationAngle, centralRotationCos, centralRotationSin);
        clip.setFill(Color.WHITE);

        // Param circle ripple
        Color rippleColor = new Color(1.0, 1.0, 1.0, 0.25);
        Duration rippleDuration = Duration.millis(500);

        // Circle ripple
        Circle circleRipple = new Circle(0, rippleColor);
        circleRipple.setOpacity(0.0);
        circleRipple.setClip(clip);

        // Fade anim
        FadeTransition fadeAnim = new FadeTransition(rippleDuration, circleRipple);
        fadeAnim.setInterpolator(Interpolator.EASE_OUT);
        fadeAnim.setFromValue(1.0);
        fadeAnim.setToValue(0.0);

        // Scale anim
        Timeline scaleAnim = new Timeline();
        KeyValue keyValue = new KeyValue(circleRipple.radiusProperty(), 200, Interpolator.EASE_OUT);
        KeyFrame keyFrame = new KeyFrame(rippleDuration, keyValue);
        scaleAnim.getKeyFrames().clear();
        scaleAnim.getKeyFrames().add(keyFrame);

        // Group anim
        SequentialTransition parallelTransition = new SequentialTransition();
        parallelTransition.getChildren().addAll(scaleAnim, fadeAnim);
        parallelTransition.setOnFinished(event -> {
            circleRipple.setOpacity(0.0);
            circleRipple.setRadius(0.0);
        });

        // Mouse click
        container.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                parallelTransition.stop();
                parallelTransition.getOnFinished().handle(null);

                circleRipple.setCenterX(event.getX());
                circleRipple.setCenterY(event.getY());

                parallelTransition.playFromStart();
            }
        });

        container.getChildren().add(circleRipple);
    }
}
