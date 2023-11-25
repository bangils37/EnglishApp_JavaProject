package dictionary.translate.strategy;

import dictionary.translate.SpeakerStrategy;
import dictionary.translate.Translator;

public class EnSpeakerStrategy implements SpeakerStrategy {

    @Override
    public void speak(String text) {
        Translator.getInstance().speak("en", text);
    }
}
