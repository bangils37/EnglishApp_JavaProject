package dictionary.ui;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HelperUI {
    static final FileChooser fileChooser = new FileChooser();
    static final DirectoryChooser dirChooser = new DirectoryChooser();

    /**
     * Sử dụng FileChooser để mở một cửa sổ popup để chọn file.
     *
     * @param stage sân khấu để mở FileChooser
     * @return đường dẫn của file đã chọn
     */
    public static String chooseFile(Stage stage) {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }

    /**
     * Sử dụng DirectoryChooser để mở một cửa sổ popup để chọn thư mục.
     *
     * @param stage sân khấu để mở DirectoryChooser
     * @return đường dẫn của thư mục đã chọn
     */
    public static String chooseDir(Stage stage) {
        File dir = dirChooser.showDialog(stage);
        if (dir != null) {
            return dir.getAbsolutePath();
        }
        return "";
    }
}
