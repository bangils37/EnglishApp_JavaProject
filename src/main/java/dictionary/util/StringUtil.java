package dictionary.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class StringUtil {

    /**
     * Loại bỏ các thẻ HTML từ một chuỗi.
     *
     * @param html chuỗi chứa các thẻ HTML
     * @return chuỗi không có thẻ HTML
     */
    public static String removeHtmlTags(String html) {
        return html.replaceAll("<(?:I|Q)>|</(?:I|Q)>|<br />", "\n");
    }

    /**
     * Tạo một chuỗi khoảng trắng có chiều dài 'spaces'.
     *
     * @param numSpaces Số lượng khoảng trắng để thêm vào chuỗi.
     * @return chuỗi chứa `numSpaces` khoảng trắng
     */
    public static String createSpacesString(int numSpaces) {
        return numSpaces < 0 ? "" : " ".repeat(numSpaces);
    }

    /**
     * Tạo một chuỗi `-` có chiều dài 'length'.
     *
     * @param length Số lượng ký tự `-` để thêm vào dòng phân cách dọc.
     * @return chuỗi chứa `length` ký tự `-`
     */
    public static String createLineSeparator(int length) {
        return length < 0 ? "" : "-".repeat(length);
    }

    /**
     * Chuyển đổi HTML sang văn bản thuần túy giữ nguyên các dòng, đoạn văn...
     *
     * <p>
     * Tham chiếu:
     * https://stackoverflow.com/questions/2513707/how-to-convert-html-to-text-keeping-linebreaks
     *
     * @param html Chuỗi HTML
     * @return văn bản thuần túy
     */
    public static String htmlToText(String html) {
        Document document = Jsoup.parse(html);
        Element body = document.body();

        return buildStringFromNode(body).toString();
    }

    /**
     * Xây dựng văn bản thuần túy từ các nút của Jsoup.
     *
     * <p>
     * Tham chiếu: <a
     * href=
     * "https://stackoverflow.com/questions/2513707/how-to-convert-html-to-text-keeping-linebreaks">...</a>
     *
     * @param node Các nút của Jsoup
     * @return StringBuffer
     */
    private static StringBuffer buildStringFromNode(Node node) {
        StringBuffer buffer = new StringBuffer();

        if (node instanceof TextNode textNode) {
            buffer.append(textNode.text().trim());
        }

        for (Node childNode : node.childNodes()) {
            buffer.append(buildStringFromNode(childNode));
        }

        if (node instanceof Element element) {
            String tagName = element.tagName();
            if ("p".equals(tagName) || "br".equals(tagName)) {
                buffer.append("\n");
            }
        }

        return buffer;
    }

    /**
     * Đếm số dòng trong file `file`.
     *
     * @param file đường dẫn của file
     * @return số dòng của file được chỉ định
     * @throws IOException file không tìm thấy hoặc không đọc được
     */
    public static int countNumLinesOfFile(String file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return Math.toIntExact(reader.lines().count());
        }
    }
}
