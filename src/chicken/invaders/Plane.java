package chicken.invaders;

import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Plane extends Item {
    private long lastShotTime = 0;
    private boolean tripleShot = false;
    private int score;
    private GamePane pane;
    private boolean winner;
    private boolean alive;

    public Plane(double x, double y, GamePane pane) {
        super("file:images/plane.png", x, y);
        this.score = 0;
        this.pane = pane;
        this.winner = false;
        this.alive = true;
    }
    
    public void reset() {
    setX(900);      // poziția inițială 
    setY(900);
    shape.setVisible(true);
    alive = true;
    winner = false;
    tripleShot = false;
}


    public void activateTripleShot() {
        tripleShot = true;

        // Power-up durează 5 secunde
        PauseTransition powerUpDuration = new PauseTransition(Duration.seconds(5));
        powerUpDuration.setOnFinished(e -> tripleShot = false);
        powerUpDuration.play();
    }

    public void moveLeft() {
        setX(getX() - 10);
    }

    public void moveRight() {
        setX(getX() + 10);
    }

    public void moveUp() {
        setY(getY() - 10);
    }

    public void moveDown() {
        setY(getY() + 10);
    }

    public int getScore() {
        return this.score;
    }

   public void shot() {
    // Blochează tragerea dacă avionul nu e viu sau jocul e terminat
    if (!alive || winner || pane.isGameOver()) return;

    long now = System.currentTimeMillis();
    if (now - lastShotTime >= 300) {
        lastShotTime = now;

        double x = this.getX() + 45;
        double y = this.getY() - 20;

        if (tripleShot) {
            new Shot(x, y, pane, 0);
            new Shot(x, y, pane, -0.3);
            new Shot(x, y, pane, 0.3);
        } else {
            new Shot(x, y, pane, 0);
        }

        SoundManager.playSound("shot");
    }
       
}
   
   
   
   public void moveTowardsTarget() {

}

   
public void die() {
    if (alive && !winner) {
        shape.setVisible(false);
        alive = false;
        SoundManager.playSound("player_die");
        pane.notifyDeath(); // notifică GamePane
    }
}

    public void win() {
    if (!winner && alive) {
        winner = true;
        pane.notifyWin(); // notifică GamePane
    }
}
        public boolean isAlive() {
    return alive;
}

public boolean isWinner() {
    return winner;
}
}
