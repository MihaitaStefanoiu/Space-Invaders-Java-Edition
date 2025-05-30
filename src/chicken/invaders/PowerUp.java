package chicken.invaders;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp {
    private ImageView shape;
    private GamePane pane;
    private Plane plane;
    private AnimationTimer timer;

    public PowerUp(double x, double y, GamePane pane, Plane plane) {
        this.pane = pane;
        this.plane = plane;

        shape = new ImageView(new Image("file:images/powerup.png"));
        shape.setX(x);
        shape.setY(y);
        pane.getChildren().add(shape);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                shape.setY(shape.getY() + 4); // power-up cade în jos cu viteză constantă

                // Coliziune cu avionul
                if (shape.getBoundsInParent().intersects(plane.getShape().getBoundsInParent())) {
                    collect();
                    stop();
                }

                // Dacă iese din ecran, elimină power-up-ul
                if (shape.getY() > pane.getHeight()) {
                    pane.getChildren().remove(shape);
                    stop();
                }
            }
        };
        timer.start();
    }

    private void collect() {
        // Elimină power-up-ul din panou și activează efectul la avion
        pane.getChildren().remove(shape);
        plane.activateTripleShot();
        SoundManager.playSound("powerup");
    }
}