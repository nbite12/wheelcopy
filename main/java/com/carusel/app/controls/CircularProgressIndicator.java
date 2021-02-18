package com.carusel.app.controls;

import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class CircularProgressIndicator extends Region{
    private static final double PREFERRED_WIDTH = 18;
    private static final double PREFERRED_HEIGHT = 18;
    private static final double MINIMUM_WIDTH = 18;
    private static final double MINIMUM_HEIGHT = 18;
    private static final double MAXIMUM_WIDTH = 18;
//    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 18;
//    private static final double MAXIMUM_HEIGHT = 1024;

    private final DoubleProperty dashOffset = new SimpleDoubleProperty(0);
    private final DoubleProperty dashArray_0 = new SimpleDoubleProperty(1);
    private StackPane indeterminatePane;
    private Circle circle;
    private Arc arc;
    private final Timeline timeline;
    private RotateTransition indeterminatePaneRotation;
    private final InvalidationListener listener;
    private boolean isRunning;

    // Constructor
    public CircularProgressIndicator(){
        getStyleClass().add("circular-progress");

        isRunning = false;
        timeline = new Timeline();
        listener = observable -> {
            circle.setStrokeDashOffset(dashOffset.get());
            circle.getStrokeDashArray().setAll(dashArray_0.getValue(), 200d);
        };
        init();
        initGraphics();
        registerListeners();
        startIndeterminate();
    }

    // Initialization
    private void init(){
        if(Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
                Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0){
            if(getPrefWidth() > 0 && getPrefHeight() > 0){
                setPrefSize(getPrefWidth(), getPrefHeight());
            }else{
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if(Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0){
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if(Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0){
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics(){
        double center = PREFERRED_WIDTH * 0.5;
        // double radius = PREFERRED_WIDTH * 0.5;
        double radius = PREFERRED_WIDTH * 0.45;
        circle = new Circle();
        circle.setCenterX(center);
        circle.setCenterY(center);
        circle.setRadius(radius);
        circle.getStyleClass().add("indicator");
        circle.setStrokeLineCap(StrokeLineCap.ROUND);
        circle.setStrokeWidth(1);
        circle.setStrokeDashOffset(dashOffset.get());
        circle.getStrokeDashArray().setAll(dashArray_0.getValue(), 200d);

        arc = new Arc(center, center, radius, radius, 90, 360.0);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        arc.setStrokeWidth(PREFERRED_WIDTH * 0.1);
        arc.getStyleClass().add("indicator");

        indeterminatePane = new StackPane(circle);
        indeterminatePane.setVisible(false);

//        indeterminatePane.setStyle("-fx-background-color: red");

        getChildren().setAll(indeterminatePane);

        // Setup timeline animation
        KeyValue kvDashOffset_0 = new KeyValue(dashOffset, 0, Interpolator.EASE_BOTH);
        KeyValue kvDashOffset_50 = new KeyValue(dashOffset, -32, Interpolator.EASE_BOTH);
        KeyValue kvDashOffset_100 = new KeyValue(dashOffset, -64, Interpolator.EASE_BOTH);

        KeyValue kvDashArray_0_0 = new KeyValue(dashArray_0, 5, Interpolator.EASE_BOTH);
        KeyValue kvDashArray_0_50 = new KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH);
        KeyValue kvDashArray_0_100 = new KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH);

        KeyValue kvRotate_0 = new KeyValue(circle.rotateProperty(), -10, Interpolator.LINEAR);
        KeyValue kvRotate_100 = new KeyValue(circle.rotateProperty(), 370, Interpolator.LINEAR);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvDashOffset_0, kvDashArray_0_0, kvRotate_0);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), kvDashOffset_50, kvDashArray_0_50);
        KeyFrame kf2 = new KeyFrame(Duration.millis(1500), kvDashOffset_100, kvDashArray_0_100, kvRotate_100);

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().setAll(kf0, kf1, kf2);

        // Setup additional pane rotation
        indeterminatePaneRotation = new RotateTransition();
        indeterminatePaneRotation.setNode(indeterminatePane);
        indeterminatePaneRotation.setFromAngle(0);
        indeterminatePaneRotation.setToAngle(-360);
        indeterminatePaneRotation.setInterpolator(Interpolator.LINEAR);
        indeterminatePaneRotation.setCycleCount(Timeline.INDEFINITE);
        indeterminatePaneRotation.setDuration(new Duration(4500));
    }

    private void registerListeners(){
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        dashOffset.addListener(listener);
    }

    private void startIndeterminate(){
        if(isRunning) return;

        indeterminatePane.setManaged(true);
        indeterminatePane.setVisible(true);

        timeline.play();
        indeterminatePaneRotation.play();
        isRunning = true;
    }

    private void resize(){
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        double size = Math.min(width, height);

        if(width > 0 && height > 0){
            indeterminatePane.setMaxSize(size, size);
            indeterminatePane.setPrefSize(size, size);
            indeterminatePane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            double center = size * 0.5;
            double radius = size * 0.45;

            arc.setCenterX(center);
            arc.setCenterY(center);
            arc.setRadiusX(radius);
            arc.setRadiusY(radius);
            arc.setStrokeWidth(size * 0.10526316);

            double factor = size / 24;
            circle.setScaleX(factor);
            circle.setScaleY(factor);
        }
    }
}