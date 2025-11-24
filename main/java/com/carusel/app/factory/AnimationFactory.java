package com.carusel.app.factory;

import de.schlegel11.jfxanimation.JFXAnimationTemplate;
import de.schlegel11.jfxanimation.JFXAnimationTemplateAction;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Function;

public class AnimationFactory{
    // Element - Out
    public static Timeline getMoveOutElementAnimation(Node node, double centralRotationCos, double centralRotationSin, double translate){
        Timeline timeline = JFXAnimationTemplate.create()
                .from()
                .to()
                .action(new Function<JFXAnimationTemplateAction.InitBuilder<Node>, JFXAnimationTemplateAction.Builder<?, ?>>(){
                    @Override
                    public JFXAnimationTemplateAction.Builder<?, ?> apply(JFXAnimationTemplateAction.InitBuilder<Node> nodeInitBuilder){
                        return nodeInitBuilder
                                .target(node.translateXProperty())
                                .endValue(node.getTranslateX() + centralRotationCos * translate);
                    }
                })
                .action(new Function<JFXAnimationTemplateAction.InitBuilder<Node>, JFXAnimationTemplateAction.Builder<?, ?>>(){
                    @Override
                    public JFXAnimationTemplateAction.Builder<?, ?> apply(JFXAnimationTemplateAction.InitBuilder<Node> nodeInitBuilder){
                        return nodeInitBuilder
                                .target(node.translateYProperty())
                                .endValue(node.getTranslateY() + centralRotationSin * translate);
                    }
                })
                .config(builder -> builder.duration(Duration.seconds(0.2)))
                .build();

        return timeline;
    }

    // Element - In
    public static Timeline getMoveInElementAnimation(Node node){
        Timeline timeline = JFXAnimationTemplate.create()
                .from()
                .to()
                .action(builder -> builder.target(node.translateXProperty()).endValue(node.getTranslateX()))
                .action(builder -> builder.target(node.translateYProperty()).endValue(node.getTranslateY()))
                .config(builder -> builder.duration(Duration.seconds(0.2)))
                .build();

        return timeline;
    }
}
