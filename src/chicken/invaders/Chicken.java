package chicken.invaders;

import javafx.animation.AnimationTimer;

public class Chicken extends Item {
    private static int killedChickens = 0;   // Contor global pentru găini ucise
    private static int dead = 0;              // Găini moarte în nivelul curent
    private static int total = 0;             // Total găini generate în nivelul curent

    private boolean alive;
    private double x, y;
    private double move = 1.5;                // Viteza de mișcare 
    private double direction = 1;             // Direcția de mișcare 
    private double startX, endX;              

    private GamePane pane;
    private Plane plane;
    private AnimationTimer animation;

    public Chicken(double x, double y, GamePane pane, Plane plane) {
        super("file:images/chicken.png", x, y);
        this.alive = true;
        this.x = x;
        this.y = y;
        this.pane = pane;
        this.plane = plane;

        if (total == 0) dead = 0;  // Reset dead dacă e primul spawn din nivel
        total++;

        startX = x - 100;
        endX = x + 500;

        // Timer pentru mișcarea orizontală a găinii
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!alive) {
                    this.stop();
                    return;
                }

                shape.setX(shape.getX() + move * direction);

                // Schimbă direcția când atinge limitele startX sau endX
                if (shape.getX() >= endX || shape.getX() <= startX) {
                    direction *= -1;
                }
            }
        };

        animation.start();
    }

    public boolean isAlive() {
        return alive;
    }

    public static void resetCounters() {
        dead = 0;
        total = 0;
    }

    public void die() {
        alive = false;
        shape.setVisible(false);

        SoundManager.playSound("chickenDie");

        dead++;
        pane.addScore(10);       // Adaugă 10 puncte la scor
        pane.updateScore();      // Actualizează afișarea scorului

        killedChickens++;
        // Spawn power-up la fiecare 40 de găini ucise
        if (killedChickens % 40 == 0) {
            new PowerUp(shape.getX(), shape.getY(), pane, plane);
        }

        // Dacă toate găinile sunt moarte, începe nivelul următor
        if (dead == total) {
            pane.startNextLevel();
        }
    }

    public void egg() {
    Egg newEgg = new Egg(getX(), getY(), pane, plane);
    pane.getEggs().add(newEgg);
}
}
