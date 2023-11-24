package dictionary.util;

public class StringUtil {

    /**
     * Remove HTML tags from a string.
     * 
     * @param html string containing HTML tags
     * @return string without HTML tags
     */
    public static String removeHtmlTags(String html) {
        html = html.replace("<I>", "");
        html = html.replace("</I>", "");
        html = html.replace("<Q>", "");
        html = html.replace("</Q>", "");
        html = html.replace("<br />", "\n");

        return html;
    }
}
