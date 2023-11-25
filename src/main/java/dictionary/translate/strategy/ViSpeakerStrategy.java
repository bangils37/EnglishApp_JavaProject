package dictionary.translate.strategy;

import dictionary.translate.SpeakerStrategy;
import dictionary.translate.Translator;

public class ViSpeakerStrategy implements SpeakerStrategy {

    @Override
    public void speak(String text) {
        Translator.getInstance().speak("vi", text);
    }
}
