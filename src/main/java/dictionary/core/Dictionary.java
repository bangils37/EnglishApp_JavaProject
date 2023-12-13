package dictionary.core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public abstract class Dictionary {

    /**
     * Khởi tạo từ điển khi bắt đầu ứng dụng.
     */
    public void initialize() throws SQLException {
    }

    /**
     * Đóng từ điển khi thoát ứng dụng.
     */
    public void close() {
    }

    /**
     * Lấy tất cả các từ trong từ điển.
     *
     * @return Danh sách từ
     */
    public abstract List<Word> getAllWords();

    /**
     * Lấy tất cả các từ tiếng Anh trong từ điển thành danh sách các chuỗi.
     *
     * @return Danh sách chuỗi của tất cả các từ
     */
    public abstract List<String> getAllWordTargets();

    /**
     * Tra cứu từ `target` và trả về định nghĩa tương ứng.
     *
     * @param target từ tra cứu
     * @return định nghĩa, nếu không tìm thấy thì trả về "404" dưới dạng chuỗi.
     */
    public abstract String lookUpWord(final String target);

    /**
     * Chèn một từ mới vào từ điển.
     *
     * @param target     từ
     * @param definition định nghĩa
     * @return true nếu `target` chưa được thêm, ngược lại là false
     */
    public abstract boolean insertWord(final String target, final String definition);

    /**
     * Xóa từ `target`.
     *
     * @param target từ đã xóa
     * @return true nếu xóa thành công, ngược lại là false
     */
    public abstract boolean deleteWord(final String target);

    /**
     * Cập nhật định nghĩa tiếng Việt của `target` thành `definition`.
     *
     * @param target     từ
     * @param definition định nghĩa mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public abstract boolean updateWordDefinition(final String target, final String definition);

    /**
     * Lấy định nghĩa của từ có ID là `iD`.
     * 
     * @param iD ID của từ
     * @return định nghĩa của từ
     */
    public abstract String lookUpWordByIDGetDefinition(int iD);

    /**
     * Lấy từ có ID là `iD`.
     * 
     * @param iD ID của từ
     * @return từ
     */
    public abstract String lookUpWordByIDGetTarget(int iD);

    /**
     * Xuất tất cả các từ với mỗi từ và định nghĩa trên 1 dòng, được phân tách bằng
     * ký tự tab.
     *
     * @return một chuỗi các từ đã xuất
     */
    public String exportAllWords() {
        List<Word> allWords = getAllWords();
        StringBuilder result = new StringBuilder();
        for (Word word : allWords) {
            result.append(word.getName())
                    .append('\t')
                    .append(word.getDefinition())
                    .append('\n');
        }
        return result.toString();
    }

    /**
     * Xuất tất cả các từ và định nghĩa của chúng vào tệp `exportPath`.
     *
     * @param exportPath đường dẫn của tệp xuất
     * @throws IOException đường dẫn không tìm thấy
     */
    public void exportToFile(String exportPath) throws IOException {
        Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(exportPath), StandardCharsets.UTF_8));
        String export = exportAllWords();
        out.write(export);
        out.close();
    }
}
