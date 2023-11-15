package dictionary.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Helper {

    /**
     * Tạo một chuỗi khoảng trắng có độ dài 'spaces'.
     *
     * @param numSpaces Số lượng khoảng trắng để thêm vào chuỗi.
     * @return chuỗi của `numSpaces` khoảng trắng
     */
    public static String createSpacesString(int numSpaces) {
        if (numSpaces < 0) {
            numSpaces = 0;
        }
        return CharBuffer.allocate(numSpaces).toString().replace('\0', ' ');
    }

    /**
     * Tạo một chuỗi gồm các ký tự `-` có độ dài 'length'.
     *
     * @param length Số lượng ký tự `-` để thêm vào dòng phân cách dọc.
     * @return chuỗi của `length` ký tự `-`
     */
    public static String createLineSeparator(int length) {
        if (length < 0) {
            length = 0;
        }
        return CharBuffer.allocate(length).toString().replace('\0', '-');
    }

    /**
     * Chuyển đổi HTML thành văn bản thuần túy giữ nguyên các dòng mới, đoạn văn...
     *
     * <p>Tham khảo:
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
     * Xây dựng văn bản thuần túy từ các nút Jsoup.
     *
     * <p>Tham khảo: <a
     * href="https://stackoverflow.com/questions/2513707/how-to-convert-html-to-text-keeping-linebreaks">...</a>
     *
     * @param node Các nút Jsoup
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
     * Đếm số dòng trong tệp `file`.
     * @param file đường dẫn của tệp
     * @return số dòng của tệp đã cho
     * @throws IOException tệp không tìm thấy hoặc không thể đọc
     */
    public static int countNumLinesOfFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }
}
