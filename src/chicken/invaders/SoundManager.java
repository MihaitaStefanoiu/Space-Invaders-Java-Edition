package chicken.invaders;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static Map<String, MediaPlayer> sounds = new HashMap<>();

    // Încarcă un sunet o singură dată din resurse
    public static void loadSound(String key, String resourcePath) {
        try {
            URL resource = SoundManager.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("Sunetul nu a fost găsit: " + resourcePath);
                return;
            }
            Media media = new Media(resource.toExternalForm());
            MediaPlayer player = new MediaPlayer(media);
            sounds.put(key, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Redă un sunet (de exemplu trage sau moarte gaina)
    public static void playSound(String key) {
        MediaPlayer player = sounds.get(key);
        if (player != null) {
            player.stop();   // oprește dacă e în redare
            player.play();   // pornește de la început
        }
    }

    // Funcție de inițializare (apelează la startul jocului)
    public static void initialize() {
        loadSound("chickenDie", "/Images/chicken_die.wav");
        loadSound("win", "/Images/win.mp3");
        loadSound("lose", "/Images/lose.wav");
        loadSound("shot", "/Images/shot.wav");
        loadSound("player_die", "/Images/player_die.wav");
        loadSound("powerup", "/Images/powerup.wav");
        loadSound("start_game", "/Images/start_game.wav");
        loadSound("boss_die", "/Images/boss_die.mp3");
        loadSound("boss_hit", "/Images/hit_boss.mp3");
    }
}
