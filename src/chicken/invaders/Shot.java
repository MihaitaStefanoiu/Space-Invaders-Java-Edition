package chicken.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.util.Duration;

public class Shot extends Item {
    private Timeline animation;
    private GamePane pane;
    private double dx;
    private double dy;

    public Shot(double x, double y, GamePane pane, double angleOffset) {
        super("file:images/shot.png", x, y);
        this.pane = pane;
        pane.getChildren().add(this.shape);
        pane.getActiveShots().add(this); // ✅ Adaugă glonțul în lista activă

        // Calculează traiectoria
        double speed = 7;
        dx = Math.sin(angleOffset) * speed;
        dy = -Math.cos(angleOffset) * speed;

        animation = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            shape.setX(shape.getX() + dx);
            shape.setY(shape.getY() + dy);

            Bounds shotBounds = shape.getBoundsInParent();

            // Coliziune cu găini
            for (Chicken c : pane.getChickens()) {
                if (c != null && c.isAlive()) {
                    Bounds chickenBounds = c.getShape().getBoundsInParent();
                    if (shotBounds.intersects(chickenBounds)) {
                        c.die();
                        destroy();
                        return;
                    }
                }
            }

            // Coliziune cu boss
            BossChicken boss = pane.getBossChicken();
            if (boss != null && boss.getShape().isVisible()) {
                if (shotBounds.intersects(boss.getShape().getBoundsInParent())) {
                    boss.takeDamage();
                    destroy();
                    return;
                }
            }

            // Dacă iese din ecran
            if (shape.getY() < 0 || shape.getX() < 0 || shape.getX() > pane.getWidth()) {
                destroy();
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    public Shot(double x, double y, GamePane pane) {
        this(x, y, pane, 0);
    }

   
    public void stop() {
        if (animation != null) {
            animation.stop();
        }
        shape.setVisible(false);
    }

    
    private void destroy() {
        stop();
        pane.getChildren().remove(shape);
        pane.getActiveShots().remove(this);
    }
}
