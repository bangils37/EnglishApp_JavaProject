package dictionary.core;

public class Config {
    public static final String HOST_NAME = "localhost";
    public static final String DB_NAME = "dictionary";
    public static final String USER_NAME = "en-vi-dictionary";
    public static final String PASSWORD = "n1-02-dictionary";
    public static final String PORT = "3306";
    public static final String MYSQL_URL = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DB_NAME;
    public static String TRANSLATE_API_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=";
    public static String TEXT_TO_SPEECH_API_URL = "https://translate.google.com/translate_tts?ie=UTF-8&tl=%s&client=tw-ob&q=%s";
    public static final int MAX_WORDS_HISTORY = 30;
    public static final String HISTORY_DIRECTORY_PATH = "dictionary-user-data/";
    public static final String HISTORY_FILE_PATH = HISTORY_DIRECTORY_PATH + "words-search-history.txt";
}
