package chicken.invaders;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

public class Egg extends Item {
    private AnimationTimer eggTimer;

    public Egg(double x, double y, Pane pane, Plane plane) {
        super("file:images/egg.png", x, y);
        shape.setX(x);
        shape.setY(y);
        pane.getChildren().add(1, this.shape);

        eggTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                shape.setY(shape.getY() + 4);
                if (shape.getY() > pane.getHeight()) {
                    stop();
                    shape.setVisible(false);
                }
                if (shape.getY() >= plane.getShape().getY() - 15 &&
                    shape.getY() <= plane.getShape().getY() + 80 &&
                    shape.getX() >= plane.getShape().getX() &&
                    shape.getX() <= plane.getShape().getX() + 75) {
                    stop();
                    plane.die();
                    shape.setVisible(false);
                }
            }
        };
        eggTimer.start();
    }

    public void stop() {
        if (eggTimer != null) {
            eggTimer.stop();
        }
    }
}