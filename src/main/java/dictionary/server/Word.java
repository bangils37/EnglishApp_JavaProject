package dictionary.server;

public class Word {
    private final String wordTarget;
    private String wordExplain;

    /**
     * Constructor mới cho Word.
     *
     * @param wordTarget Từ tiếng Anh
     * @param wordExplain Định nghĩa tiếng Việt
     */
    public Word(String wordTarget, String wordExplain) {
        this.wordTarget = wordTarget;
        this.wordExplain = wordExplain;
    }

    /**
     * Lấy từ tiếng Anh.
     *
     * @return Từ tiếng Anh
     */
    public String getWordTarget() {
        return wordTarget;
    }

    /**
     * Lấy định nghĩa của từ.
     *
     * @return Định nghĩa tiếng Việt của từ
     */
    public String getWordDefinition() {
        return wordExplain;
    }

    /**
     * Thiết lập định nghĩa tiếng Việt cho từ.
     *
     * @param wordExplain Định nghĩa tiếng Việt
     */
    public void setWordExplain(String wordExplain) {
        this.wordExplain = wordExplain;
    }
}
