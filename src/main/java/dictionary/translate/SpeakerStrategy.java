package dictionary.translate;

public interface SpeakerStrategy {

    /**
     * Speak text `text` with a voice.
     * 
     * @param text the text to be spoken
     */
    void speak(String text);
}
