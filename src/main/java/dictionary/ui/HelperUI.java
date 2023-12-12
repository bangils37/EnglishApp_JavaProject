package dictionary.ui;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelperUI {
    private static final FileChooser fileChooser = new FileChooser();
    private static final DirectoryChooser dirChooser = new DirectoryChooser();

    /**
     * Mở cửa sổ chọn file để chọn một tập tin.
     *
     * @param stage sân khấu để mở FileChooser từ
     * @return đường dẫn của tập tin đã chọn, hoặc chuỗi rỗng nếu không chọn tập tin nào
     */
    public static String chooseFile(Stage stage) {
        File file = fileChooser.showOpenDialog(stage);
        return file != null ? file.getAbsolutePath() : "";
    }

    /**
     * Mở cửa sổ chọn thư mục để chọn một thư mục.
     *
     * @param stage sân khấu để mở DirectoryChooser từ
     * @return đường dẫn của thư mục đã chọn, hoặc chuỗi rỗng nếu không chọn thư mục nào
     */
    public static String chooseDir(Stage stage) {
        File dir = dirChooser.showDialog(stage);
        return dir != null ? dir.getAbsolutePath() : "";
    }
}
