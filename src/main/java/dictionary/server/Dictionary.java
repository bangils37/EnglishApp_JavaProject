package dictionary.server;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Dictionary {

    /**
     * Khởi tạo từ điển khi khởi động ứng dụng. (Chỉ được ghi đè bởi DatabaseDictionary để thực hiện kết nối MYSQL)
     */
    public void initialize() throws SQLException {}

    /**
     * Đóng từ điển khi thoát khỏi ứng dụng. (Chỉ được ghi đè bởi DatabaseDictionary để đóng kết nối MYSQL)
     */
    public void close() {}

    /**
     * Lấy tất cả các từ trong từ điển.
     *
     * @return ArrayList của Word
     */
    public abstract ArrayList<Word> getAllWords();

    /**
     * Lấy tất cả các từ tiếng Anh trong từ điển thành một ArrayList của String.
     *
     * @return ArrayList của String chứa tất cả các từ
     */
    public abstract ArrayList<String> getAllWordTargets();

    /**
     * Tra cứu từ `target` và trả về định nghĩa tương ứng.
     *
     * @param target từ cần tra cứu
     * @return định nghĩa, nếu không tìm thấy trả về "404" dưới dạng String.
     */
    public abstract String lookUpWord(final String target);

    /**
     * Chèn một từ mới vào từ điển.
     *
     * @param target từ
     * @param definition định nghĩa
     * @return true nếu `target` chưa được thêm, ngược lại trả về false
     */
    public abstract boolean insertWord(final String target, final String definition);

    /**
     * Xóa từ `target`.
     *
     * @param target từ cần xóa
     * @return true nếu xóa thành công, ngược lại trả về false
     */
    public abstract boolean deleteWord(final String target);

    /**
     * Cập nhật định nghĩa tiếng Việt của `target` thành `definition`.
     *
     * @param target từ
     * @param definition định nghĩa mới
     * @return true nếu cập nhật thành công, ngược lại trả về false
     */
    public abstract boolean updateWordDefinition(final String target, final String definition);

    /**
     * Xuất tất cả các từ với mỗi từ và định nghĩa nằm trên 1 dòng, được ngăn cách bằng ký tự tab.
     *
     * @return chuỗi các từ được xuất
     */
    public String exportAllWords() {
        ArrayList<Word> allWords = getAllWords();
        StringBuilder result = new StringBuilder();
        for (Word word : allWords) {
            result.append(word.getWordTarget())
                    .append('\t')
                    .append(word.getWordDefinition())
                    .append('\n');
        }
        return result.toString();
    }

    /**
     * Xuất tất cả các từ và định nghĩa của chúng vào tệp `exportPath`.
     *
     * @param exportPath đường dẫn của tệp xuất khẩu
     * @throws IOException đường dẫn không tìm thấy
     */
    public void exportToFile(String exportPath) throws IOException {
        Writer out =
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(exportPath), StandardCharsets.UTF_8));
        String export = exportAllWords();
        out.write(export);
        out.close();
    }
}
