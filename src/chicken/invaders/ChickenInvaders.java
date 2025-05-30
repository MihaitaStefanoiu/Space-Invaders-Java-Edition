package chicken.invaders;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ChickenInvaders extends Application {

    private MediaPlayer menuMusic;
    public static MediaPlayer winMusic;
    public static MediaPlayer loseMusic;

    public static void playWinMusic() {
        if (winMusic != null) {
            winMusic.stop();
            winMusic.play();
        }
    }

    public static void playLoseMusic() {
        if (loseMusic != null) {
            loseMusic.stop();
            loseMusic.play();
        }
    }

   public void start(Stage stage) {
    SoundManager.initialize();

    menuMusic = new MediaPlayer(new Media(new File("images/menu.mp3").toURI().toString()));
    menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
    menuMusic.setVolume(0.5);
    menuMusic.play();

    winMusic = new MediaPlayer(new Media(new File("images/win.mp3").toURI().toString()));
    loseMusic = new MediaPlayer(new Media(new File("images/lose.wav").toURI().toString()));

    Pane introPane = new Pane();
    Scene scene = new Scene(introPane);
    ImageView bg = new ImageView("file:images/space.png");
    ImageView title = new ImageView("file:images/title.png");
    title.setX(80);
    title.setY(50);

    ImageView exit = new ImageView("file:images/exit.png");
    exit.setX(50);
    exit.setY(80);
    exit.setCursor(Cursor.HAND);

    ImageView start = new ImageView("file:images/start.png");
    start.setX(50);
    start.setY(100);
    start.setCursor(Cursor.HAND);

    introPane.getChildren().addAll(bg, title, exit, start);

   Runnable startGame = () -> {
    SoundManager.playSound("start_game");
    if (menuMusic != null) {
        menuMusic.stop();
    }

    GamePane view = new GamePane();
    Set<KeyCode> pressedKeys = new HashSet<>();

    scene.setCursor(Cursor.NONE);

    scene.setOnKeyPressed(e -> {
        pressedKeys.add(e.getCode());

        if (e.getCode() == KeyCode.ESCAPE) {
            stage.close();
        }

        if (view.isGameOver() && e.getCode() == KeyCode.R) {
            view.startGame();
        }
    });

    scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

    AnimationTimer timer = new AnimationTimer() {
        public void handle(long now) {
            if (pressedKeys.contains(KeyCode.A) && view.getPlane().getX() > 0) {
                view.getPlane().moveLeft();
            }
            if (pressedKeys.contains(KeyCode.D) && view.getPlane().getX() < view.getWidth() - 100) {
                view.getPlane().moveRight();
            }
            if (pressedKeys.contains(KeyCode.W) && view.getPlane().getY() > 450) {
                view.getPlane().moveUp();
            }
            if (pressedKeys.contains(KeyCode.S) && view.getPlane().getY() < view.getHeight() - 110) {
                view.getPlane().moveDown();
            }
            if (pressedKeys.contains(KeyCode.SPACE)) {
                view.getPlane().shot();
            }
        }
    };
    timer.start();

    scene.setRoot(view);

    view.requestFocus();
};
    start.setOnMouseReleased(e -> startGame.run());
    scene.setOnKeyPressed(e -> {
        if (e.getCode() == KeyCode.ENTER) {
            startGame.run();
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            stage.close();
        }
    });

    exit.setOnMouseReleased(e -> stage.close());

    stage.setScene(scene);
    stage.setTitle("Chicken Invaders");
    stage.setFullScreen(true);
    stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    stage.show();
}
}