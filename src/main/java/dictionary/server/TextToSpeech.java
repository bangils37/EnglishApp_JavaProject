package dictionary.server;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javazoom.jl.player.Player;

public class TextToSpeech {
    /**
     * Chuyển đổi văn bản tiếng Anh {@code text} thành giọng nói và phát nó với Google Translator TTS API
     *
     * @param text Văn bản cần chuyển đổi thành giọng nói tiếng Anh
     */
    public static void playSoundGoogleTranslateEnToVi(String text) {
        try {
            String api =
                    "https://translate.google.com/translate_tts?ie=UTF-8&tl="
                            + "en"
                            + "&client=tw-ob&q="
                            + URLEncoder.encode(text, StandardCharsets.UTF_8);
            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream audio = con.getInputStream();
            new Player(audio).play();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy giọng");
        }
    }

    /**
     * Chuyển đổi văn bản tiếng Việt {@code text} thành giọng nói và phát nó với Google Translator TTS API
     *
     * @param text Văn bản cần chuyển đổi thành giọng nói tiếng Việt
     */
    public static void playSoundGoogleTranslateViToEn(String text) {
        try {
            String api =
                    "https://translate.google.com/translate_tts?ie=UTF-8&tl="
                            + "vi"
                            + "&client=tw-ob&q="
                            + URLEncoder.encode(text, StandardCharsets.UTF_8);
            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream audio = con.getInputStream();
            new Player(audio).play();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy giọng");
        }
    }
}
