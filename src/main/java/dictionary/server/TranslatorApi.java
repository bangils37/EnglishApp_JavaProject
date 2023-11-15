package dictionary.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslatorApi {

    /**
     * Dịch văn bản tiếng Anh `text` sang tiếng Việt.
     *
     * @param text văn bản cần dịch
     * @return bản dịch tiếng Việt, hoặc "500" nếu có lỗi
     */
    public static String translateEnToVi(String text) {
        try {
            return translate("en", "vi", text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "500";
    }

    /**
     * Dịch văn bản tiếng Việt `text` sang tiếng Anh.
     *
     * @param text văn bản cần dịch
     * @return bản dịch tiếng Anh, hoặc "500" nếu có lỗi
     */
    public static String translateViToEn(String text) {
        try {
            return translate("vi", "en", text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "500";
    }

    /**
     * Dịch văn bản từ `langFrom` sang `langTo`.
     *
     * <p><a
     * href="https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application">Tham chiếu</a>
     *
     * @param langFrom ngôn ngữ đầu vào (2 ký tự (ví dụ: 'en'))
     * @param langTo ngôn ngữ đầu ra (2 ký tự (ví dụ: 'vi'))
     * @param text văn bản cần dịch
     * @return văn bản dịch trong `langTo`
     */
    private static String translate(String langFrom, String langTo, String text)
            throws IOException {
        String urlStr =
                "https://script.google.com/macros/s/AKfycby3AOWmhe32TgV9nW-Q0TyGOEyCHQeFiIn7hRgy5m8jHPaXDl2GdToyW_3Ys5MTbK6wjg/exec"
                        + "?q="
                        + URLEncoder.encode(text, StandardCharsets.UTF_8)
                        + "&target="
                        + langTo
                        + "&source="
                        + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
