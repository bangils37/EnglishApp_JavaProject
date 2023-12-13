package dictionary.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StringUtil {

    public static String removeHtmlTags(String html) {
        return html.replaceAll("<(?:I|Q)>|</(?:I|Q)>|<br />", "\n");
    }

    public static String createSpacesString(int numSpaces) {
        return numSpaces < 0 ? "" : " ".repeat(numSpaces);
    }

    public static String createLineSeparator(int length) {
        return length < 0 ? "" : "-".repeat(length);
    }

    public static String htmlToText(String html) {
        Document document = Jsoup.parse(html);
        Element body = document.body();

        return buildStringFromNode(body).toString();
    }

    private static StringBuffer buildStringFromNode(Node node) {
        StringBuffer buffer = new StringBuffer();

        if (node instanceof TextNode textNode) {
            buffer.append(textNode.text().trim());
        }

        for (Node childNode : node.childNodes()) {
            buffer.append(buildStringFromNode(childNode));
        }

        if (node instanceof Element element) {
            handleElement(buffer, element);
        }

        return buffer;
    }

    private static void handleElement(StringBuffer buffer, Element element) {
        String tagName = element.tagName();
        if ("p".equals(tagName) || "br".equals(tagName)) {
            buffer.append("\n");
        }
    }

    public static int countNumLinesOfFile(String file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return Math.toIntExact(reader.lines().count());
        }
    }
}
