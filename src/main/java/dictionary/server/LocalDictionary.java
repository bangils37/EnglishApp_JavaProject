package dictionary.server;

import java.util.ArrayList;

public class LocalDictionary extends Dictionary {

    private static final ArrayList<Word> words = new ArrayList<>();

    /**
     * Lấy tất cả các từ trong từ điển.
     *
     * @return ArrayList của Word
     */
    @Override
    public ArrayList<Word> getAllWords() {
        return words;
    }

    /**
     * Lấy tất cả các từ tiếng Anh trong từ điển vào một ArrayList của String.
     *
     * @return ArrayList của String chứa tất cả các từ
     */
    @Override
    public ArrayList<String> getAllWordTargets() {
        ArrayList<String> result = new ArrayList<>();
        for (Word w : words) {
            String target = w.getWordTarget();
            result.add(target);
        }
        return result;
    }

    /**
     * Tra cứu từ `target` và trả về định nghĩa tương ứng.
     *
     * @param target từ cần tra cứu
     * @return định nghĩa, nếu không tìm thấy sẽ trả về "404" dưới dạng String.
     */
    @Override
    public String lookUpWord(final String target) {

        for (Word w : words) {
            if (w.getWordTarget().equals(target)) {
                return w.getWordDefinition();
            }
        }
        return "404";
    }

    /**
     * Thêm một từ mới vào từ điển.
     *
     * @param target từ cần thêm
     * @param definition định nghĩa
     * @return true nếu `target` chưa được thêm, ngược lại là false
     */
    @Override
    public boolean insertWord(final String target, final String definition) {
        for (Word w : words) {
            if (w.getWordTarget().equals(target)) {
                return false;
            }
        }
        Word w = new Word(target, definition);
        words.add(w);
        Trie.insert(target);
        return true;
    }

    /**
     * Xóa từ `target`.
     *
     * @param target từ cần xóa
     * @return true nếu xóa thành công, ngược lại là false
     */
    @Override
    public boolean deleteWord(final String target) {
        for (int i = 0; i < words.size(); ++i) {
            if (words.get(i).getWordTarget().equals(target)) {
                words.remove(i);
                Trie.delete(target);
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật định nghĩa tiếng Việt của `target` thành `definition`.
     *
     * @param target từ cần cập nhật
     * @param definition định nghĩa mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    @Override
    public boolean updateWordDefinition(final String target, final String definition) {
        for (Word w : words) {
            if (w.getWordTarget().equals(target)) {
                w.setWordExplain(definition);
                return true;
            }
        }
        return false;
    }
}
