package dictionary.translate;

/**
 * Design pattern: Strategy
 */
public interface TranslationStrategy {

    /**
     * Translate text `text` into another language.
     * 
     * @param text the text to be translated
     * @return the translation text. If got errors, return null
     */
    String translate(String text);
}
