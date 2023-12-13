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
        return chooseFileOrDir(stage, true);
    }

    /**
     * Mở cửa sổ chọn thư mục để chọn một thư mục.
     *
     * @param stage sân khấu để mở DirectoryChooser từ
     * @return đường dẫn của thư mục đã chọn, hoặc chuỗi rỗng nếu không chọn thư mục nào
     */
    public static String chooseDir(Stage stage) {
        return chooseFileOrDir(stage, false);
    }

    /**
     * Hiển thị hộp thoại chọn file hoặc thư mục.
     *
     * @param stage     sân khấu để mở FileChooser hoặc DirectoryChooser từ
     * @param chooseDir true nếu chọn thư mục, false nếu chọn file
     * @return đường dẫn của file hoặc thư mục đã chọn, hoặc chuỗi rỗng nếu không chọn gì
     */
    private static String chooseFileOrDir(Stage stage, boolean chooseDir) {
        File chosenFile = chooseDir ? showDirectoryDialog(stage) : showFileOpenDialog(stage);
        return chosenFile != null ? chosenFile.getAbsolutePath() : "";
    }

    private static File showFileOpenDialog(Stage stage) {
        return fileChooser.showOpenDialog(stage);
    }

    private static File showDirectoryDialog(Stage stage) {
        return dirChooser.showDialog(stage);
    }
}
