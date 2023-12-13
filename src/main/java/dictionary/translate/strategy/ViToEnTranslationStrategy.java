package dictionary.translate.strategy;

import java.io.IOException;

import dictionary.translate.TranslationStrategy;
import dictionary.translate.Translator;

public class ViToEnTranslationStrategy implements TranslationStrategy {

    @Override
    public String translate(String text) {
        try {
            return Translator.getInstance().translate("vi", "en", text);
        } catch (IOException e) {
            return null;
        }
    }
}
