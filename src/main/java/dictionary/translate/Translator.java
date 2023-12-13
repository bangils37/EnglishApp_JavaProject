package dictionary.translate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import dictionary.core.Config;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * Design pattern: Singleton
 */
public class Translator {

    private static Translator instance = null;

    private Translator() {
    }

    /**
     * Lấy instance của Translator.
     *
     * @return instance của Translator
     */
    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    /**
     * Dịch văn bản từ `langFrom` sang `langTo`.
     *
     * @param langFrom ngôn ngữ đầu vào (2 ký tự (ví dụ: 'en'))
     * @param langTo   ngôn ngữ đầu ra (2 ký tự (ví dụ: 'vi'))
     * @param text     văn bản cần dịch
     * @return văn bản dịch ở ngôn ngữ `langTo`
     * @throws IOException nếu có lỗi
     */
    public String translate(String langFrom, String langTo, String text) throws IOException {
        String url = buildTranslateUrl(langFrom, langTo, text);
        return fetchTranslation(url);
    }

    /**
     * Xây dựng URL của API dịch.
     *
     * @param langFrom ngôn ngữ đầu vào (2 ký tự (ví dụ: 'en'))
     * @param langTo   ngôn ngữ đầu ra (2 ký tự (ví dụ: 'vi'))
     * @param text     văn bản cần dịch
     * @return URL đã xây dựng
     * @throws UnsupportedEncodingException nếu encoding không được hỗ trợ
     */
    private String buildTranslateUrl(String langFrom, String langTo, String text) throws UnsupportedEncodingException {
        return String.format(Config.TRANSLATE_API_URL, langFrom, langTo)
                + URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    /**
     * Lấy văn bản dịch từ URL đã cho.
     *
     * @param url URL để lấy văn bản dịch từ
     * @return văn bản dịch
     * @throws IOException nếu có lỗi trong quá trình yêu cầu HTTP
     */
    private String fetchTranslation(String url) throws IOException {
        URL obj = new URL(url);

        try (BufferedInputStream in = new BufferedInputStream(obj.openStream())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString().split("\"")[1];
        }
    }

    /**
     * Chuyển đổi văn bản thành tiếng nói.
     *
     * @param lang ngôn ngữ (2 ký tự (ví dụ: 'en'))
     * @param text văn bản cần chuyển đổi thành tiếng nói
     */
    public void speak(String lang, String text) {
        try {
            String apiUrl = buildTextToSpeechUrl(lang, text);
            playAudio(apiUrl);
        } catch (Exception e) {
            handleSpeechError(e);
        }
    }

    /**
     * Xây dựng URL của API chuyển đổi văn bản thành tiếng nói.
     *
     * @param lang ngôn ngữ (2 ký tự (ví dụ: 'en'))
     * @param text văn bản cần chuyển đổi thành tiếng nói
     * @return URL đã xây dựng
     * @throws UnsupportedEncodingException nếu encoding không được hỗ trợ
     */
    private String buildTextToSpeechUrl(String lang, String text) throws UnsupportedEncodingException {
        return String.format(Config.TEXT_TO_SPEECH_API_URL, lang, URLEncoder.encode(text, StandardCharsets.UTF_8));
    }

    /**
     * Phát âm thanh từ URL đã cho.
     *
     * @param apiUrl URL của tệp âm thanh
     * @throws IOException nếu có lỗi trong quá trình yêu cầu HTTP
     */
    private void playAudio(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        InputStream audio = con.getInputStream();

        try {
            new Player(audio).play();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }

        con.disconnect();
    }

    /**
     * Xử lý lỗi trong quá trình chuyển đổi văn bản thành tiếng nói.
     *
     * @param e ngoại lệ được ném ra
     */
    private void handleSpeechError(Exception e) {
        e.printStackTrace();
        System.err.println("Lỗi khi lấy giọng đọc");
    }
}
