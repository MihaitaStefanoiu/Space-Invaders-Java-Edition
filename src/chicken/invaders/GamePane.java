package chicken.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GamePane extends Pane {
    

    // ----------------- Variabile membre -----------------
    private List<Egg> eggs = new ArrayList<>();
    private Plane plane;
    private BossChicken bossChicken;
    private Pane overlayPane = new Pane();  // Pane pentru UI-ul peste toate
    private ImageView endImageView;
    private int currentLevel = 0;
    private static final int MAX_LEVEL = 5;
    private List<BossEgg> bossEggs = new ArrayList<>();
    private Timeline eggTimeline;
    private List<Chicken> chickens = new ArrayList<>();
    private List<Shot> activeShots = new ArrayList<>();
    private Label levelLabel = new Label();
    private Label scoreLabel = new Label();
    private Label highScoreLabel = new Label();
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private Timeline gameLoop;

    // ----------------- Constructor -----------------
    public GamePane() {
        // Adauga fundalul jocului
        ImageView bg = new ImageView("file:images/space.png");
        this.getChildren().add(bg);
        

        setupLabels();

        // Creaza player ul 
        this.plane = new Plane(900, 900, this);
        this.getChildren().add(this.plane.getShape());

        startGame();
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> {
        plane.moveTowardsTarget();
        checkCollisions(); 
}));
gameLoop.setCycleCount(Timeline.INDEFINITE);
gameLoop.play();
    }
    private boolean checkCollision(ImageView a, ImageView b) {
    return a.getBoundsInParent().intersects(b.getBoundsInParent());
}
    

    public List<Egg> getEggs() {
        return eggs;
    }
    

    // ----------------- Setup UI Labels -----------------
    private void setupLabels() {
        // Label scor
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 24px;");
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(20);
        this.getChildren().add(scoreLabel);

        // Label highscore
        highScoreLabel.setTextFill(Color.WHITE);
        highScoreLabel.setStyle("-fx-font-size: 24px;");
        highScoreLabel.setLayoutX(20);
        highScoreLabel.setLayoutY(60);
        this.getChildren().add(highScoreLabel);

        // Label nivel 
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        levelLabel.setLayoutY(100);
        this.getChildren().add(levelLabel);
    }

    private void centerLevelLabel() {
        double centerX = (this.getWidth() - levelLabel.getWidth()) / 2;
        levelLabel.setLayoutX(centerX);
    }

    // ----------------- Metode pentru încărcare și salvare highscore -----------------
    private int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            return 0; // dacă fișierul nu există sau nu conține un număr valid
        }
    }

    private void saveHighScore(int highScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Metode de start și resetare joc -----------------
    public void startGame() {
        if (endImageView != null) {
            this.getChildren().remove(endImageView);
            endImageView = null;
        }

        // Încarcă highscore din fișier la startul jocului
        highScore = loadHighScore();

        plane.reset();  // resetează poziția și starea avionului

        killAllEggs();  // elimină toate ouăle

        clearChickens();
        removeBoss();
        removeAllShots();

        gameOver = false;
        score = 0;
        updateScore();
        currentLevel = 0;

        levelLabel.setVisible(true);

        if (eggTimeline != null) {
            eggTimeline.stop();
            eggTimeline = null;
        }

        startNextLevel();
    }
    
    

    public List<Shot> getActiveShots() {
        return activeShots;
    }

    public void removeAllShots() {
        for (Shot s : activeShots) {
            s.stop(); // oprește animația
            this.getChildren().remove(s.getShape()); // scoate din ecran
        }
        activeShots.clear(); // golește lista
    }

    private void clearChickens() {
        for (Chicken c : chickens) {
            this.getChildren().remove(c.getShape());
        }
        chickens.clear();
    }

    private void removeBoss() {
        if (bossChicken != null) {
            this.getChildren().remove(bossChicken.getShape());
            bossChicken = null;
        }
    }

    // ----------------- Nivele și spawn -----------------
    public void startNextLevel() {
        currentLevel++;

        if (currentLevel <= MAX_LEVEL) {
            levelLabel.setText("Nivel " + currentLevel);
        } else {
            levelLabel.setText("BOSS");
        }

        levelLabel.applyCss();
        levelLabel.layout();
        centerLevelLabel();

        removeBoss();

        if (currentLevel <= MAX_LEVEL) {
            spawnWave(currentLevel);
        } else {
            spawnBoss();
        }
    }
    
    
    private void checkCollisions() {
    ImageView planeShape = plane.getShape();

    // Coliziune cu găini
    for (Chicken c : chickens) {
        if (c.isAlive() && checkCollision(planeShape, c.getShape())) {
            // Aici tratezi coliziunea (ex: notifică moarte, reduce viață etc)
            System.out.println("Coliziune cu gaina!");
            notifyDeath();
            break;  // oprește după prima coliziune găsită
        }
    }

    // Coliziune cu boss (dacă există)
    if (bossChicken != null) {
        if (checkCollision(planeShape, bossChicken.getShape())) {
            System.out.println("Coliziune cu BOSS-ul!");
            notifyDeath();
        }
    }
}

    public void spawnWave(int level) {
        if (eggTimeline != null) eggTimeline.stop();

        Chicken.resetCounters();
        chickens.clear();

        int rows = 2 + level / 2;
        int cols = 6 + level / 2;

        double screenWidth = 1200;
        double screenHeight = 1000;

        double spacingX = 80;
        double spacingY = 80;

        double startX = screenWidth / 2 - (cols - 1) * spacingX / 2;
        double startY = screenHeight / 4 - (rows - 1) * spacingY / 2;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                double x = startX + i * spacingX;
                double y = startY + j * spacingY;
                Chicken c = new Chicken(x, y, this, plane);
                chickens.add(c);
                this.getChildren().add(c.getShape());
            }
        }

        eggTimeline = new Timeline(new KeyFrame(Duration.millis(Math.max(200, 600 - level * 30)), e -> {
            if (!chickens.isEmpty()) {
                Chicken randomChicken = chickens.get((int) (Math.random() * chickens.size()));
                if (randomChicken.isAlive()) {
                    randomChicken.egg();
                }
            }
        }));
        eggTimeline.setCycleCount(Timeline.INDEFINITE);
        eggTimeline.play();
    }

    public void spawnBoss() {
        if (bossChicken == null) {
            bossChicken = new BossChicken(this, plane);
        }
        if (!this.getChildren().contains(bossChicken.getShape())) {
            this.getChildren().add(bossChicken.getShape());
        }
    }

    public List<Chicken> getChickens() {
        return chickens;
    }

    public BossChicken getBossChicken() {
        return bossChicken;
    }

    public Plane getPlane() {
        return this.plane;
    }

    public List<BossEgg> getBossEggs() {
        return bossEggs;
    }

    // ----------------- Scor -----------------
    public void updateScore() {
        scoreLabel.setText("Score: " + score);
        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore); // salvează în fișier când apare highscore nou
        }
        highScoreLabel.setText("Highscore: " + highScore);
    }
    

    public void addScore(int points) {
        System.out.println("Adding score: " + points);
        score += points;
        updateScore();
    }

    // ----------------- Final joc -----------------
    public void gameOver() {
        System.out.println("Game Over");
        if (eggTimeline != null) {
            eggTimeline.stop();
        }
        levelLabel.setVisible(false);
        gameOver = true;
    }

    public void notifyDeath() {
        if (!gameOver) {
            gameOver = true;
            if (eggTimeline != null) eggTimeline.stop();
            levelLabel.setVisible(false);

            endImageView = new ImageView("file:images/game-over.png");
            endImageView.setFitWidth(getWidth());
            endImageView.setFitHeight(getHeight());
            endImageView.setMouseTransparent(true);

            this.getChildren().add(endImageView);

            ChickenInvaders.playLoseMusic();
        }
    }

    public void notifyWin() {
        if (!gameOver) {
            gameOver = true;
            if (eggTimeline != null) eggTimeline.stop();
            levelLabel.setVisible(false);

            endImageView = new ImageView("file:images/win.png");
            endImageView.setFitWidth(getWidth());
            endImageView.setFitHeight(getHeight());
            endImageView.setMouseTransparent(true);

            this.getChildren().add(endImageView);

            ChickenInvaders.playWinMusic();
        }
    }

    public void killAllEggs() {
        for (Egg egg : eggs) {
            egg.stop();
            egg.getShape().setVisible(false);
            this.getChildren().remove(egg.getShape());
        }
        eggs.clear();

        for (BossEgg bossEgg : bossEggs) {
            bossEgg.stop();
            bossEgg.getShape().setVisible(false);
            this.getChildren().remove(bossEgg.getShape());
        }
        bossEggs.clear();
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
