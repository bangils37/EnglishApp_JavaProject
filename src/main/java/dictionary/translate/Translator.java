package dictionary.translate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javazoom.jl.player.Player;

/**
 * Design pattern: Singleton
 */
public class Translator {

    public static String TRANSLATE_API_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=";
    public static String TEXT_TO_SPEECH_API_URL = "https://translate.google.com/translate_tts?ie=UTF-8&tl=%s&client=tw-ob&q=%s";

    private static Translator instance = null;

    private Translator() {
    }

    /**
     * Get the instance of Translator.
     *
     * @return the instance of Translator
     */
    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    /**
     * Translate text from `langFrom` to `langTo`.
     * 
     * @param langFrom the input language (2 letters (ex: 'en'))
     * @param langTo   the output language (2 letters (ex: 'vi'))
     * @param text     the text to be translated
     * @return the translation text in `langTo`
     * @throws IOException if got errors
     */
    public String translate(String langFrom, String langTo, String text)
            throws IOException {
        String url = String.format(TRANSLATE_API_URL, langFrom, langTo)
                + URLEncoder.encode(text, StandardCharsets.UTF_8);
        URL obj = new URL(url);

        try (BufferedInputStream in = new BufferedInputStream(obj.openStream())) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString().split("\"")[1];
        }
    }

    /**
     * Convert text to speech.
     * 
     * @param lang the language (2 letters (ex: 'en'))
     * @param text the text to be converted to speech
     */
    public void speak(String lang, String text) {
        try {
            String apiUrl = String.format(TEXT_TO_SPEECH_API_URL, lang,
                    URLEncoder.encode(text, StandardCharsets.UTF_8));
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream audio = con.getInputStream();
            new Player(audio).play();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getting voices");
        }
    }
}
