package chicken.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class BossEgg extends Item {
    private Timeline animation;

    public BossEgg(double x, double y, Pane pane, Plane plane, double angle) {
        super("file:images/eggboss.png", x, y);
        pane.getChildren().add(1, this.shape);

        double speed = 4;
        double dx = speed * Math.cos(angle);
        double dy = speed * Math.sin(angle);

        animation = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            // Mișcă oul în direcția calculată
            shape.setX(shape.getX() + dx);
            shape.setY(shape.getY() + dy);

            // Detectează coliziunea cu avionul
            if (shape.getBoundsInParent().intersects(plane.getShape().getBoundsInParent())) {
                plane.die();
                shape.setVisible(false);
                animation.stop();
            }

            // Verifică dacă oul a ieșit din ecran
            if (shape.getY() > pane.getHeight() || shape.getX() < 0 || shape.getX() > pane.getWidth()) {
                shape.setVisible(false);
                animation.stop();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }
    public void stop() {
    if (animation != null) {
        animation.stop();
    }
}
}
