package dictionary.ui.controller;

import static dictionary.App.dictionary;

import dictionary.ui.HelperUI;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExportToFile {
    @FXML private Button browseButton;
    @FXML private Label dirLabel;
    @FXML private TextField fileName;

    /** Tập trung vào browseButton khi cửa sổ được mở. */
    @FXML
    private void initialize() {
        Platform.runLater(() -> browseButton.requestFocus());
    }

    /**
     * Chọn thư mục để lưu file xuất.
     *
     * @param event sự kiện
     */
    @FXML
    public void chooseDir(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String dir = HelperUI.chooseDir(appStage);
        dirLabel.setText("  " + dir);
    }

    /**
     * Xuất tất cả các từ vào thư mục và tên file được chọn. Mỗi dòng của file xuất
     * là một từ và định nghĩa của nó, cách nhau bởi ký tự TAB.
     */
    @FXML
    public void submitExport() {
        String file = fileName.getText().strip();
        String dirPath = dirLabel.getText().strip();
        if (!dirPath.isEmpty() && !file.isEmpty()) {
            try {
                String filePath = dirPath + "\\" + file;
                dictionary.exportToFile(filePath);
                showAlert("Thông báo", "Thành công xuất dữ liệu ra file `" + filePath + "`", AlertType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không tìm thấy đường dẫn của file!", AlertType.ERROR);
            }
        }
    }

    /**
     * Thiết lập CSS cho hộp thoại cảnh báo trong trường hợp chế độ tối.
     *
     * @param alert hộp thoại cảnh báo
     */
    private void setAlertCss(Alert alert) {
        if (!Application.isLightMode()) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(getClass().getResource("/css/Alert-dark.css"))
                                    .toExternalForm());
            dialogPane.getStyleClass().add("alert");
        }
    }

    /**
     * Hiển thị một cảnh báo với tiêu đề, nội dung và loại cảnh báo cụ thể.
     *
     * @param title   tiêu đề của cảnh báo
     * @param content nội dung của cảnh báo
     * @param type    loại của cảnh báo
     */
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        setAlertCss(alert);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
