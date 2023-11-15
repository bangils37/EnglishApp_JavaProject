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
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExportToFile {
    @FXML private Button browseButton;
    @FXML private Label dirLabel;
    @FXML private TextField fileName;

    /** Tập trung vào nút browseButton khi mở cửa sổ. */
    @FXML
    private void initialize() {
        Platform.runLater(() -> browseButton.requestFocus());
    }

    /**
     * Chọn thư mục để lưu file đã xuất.
     *
     * @param event sự kiện hành động
     */
    @FXML
    public void chooseDir(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String dir = HelperUI.chooseDir(appStage);
        dirLabel.setText("  " + dir);
    }

    /**
     * Xuất tất cả các từ vào thư mục và tên file đã chọn. Mỗi dòng của file xuất ra là một từ với
     * định nghĩa của nó, cách nhau bởi ký tự TAB.
     */
    @FXML
    public void submitExport() {
        String file = fileName.getText().strip();
        String dirPath = dirLabel.getText().strip();
        if (!dirPath.isEmpty() && !file.isEmpty()) {
            try {
                dictionary.exportToFile(dirPath + "\\" + file);
                Alert alert = new Alert(AlertType.INFORMATION);
                setAlertCss(alert);
                alert.setTitle("Thông báo");
                alert.setContentText(
                        "Thành công xuất dữ liệu ra file `" + dirPath + "\\" + file + "`");
                alert.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                setAlertCss(alert);
                alert.setTitle("Lỗi");
                alert.setContentText("Không tìm thấy đường dẫn của file!");
                alert.show();
            }
        }
    }

    /**
     * Đặt CSS cho hộp thoại cảnh báo trong trường hợp chế độ tối.
     *
     * @param alert cảnh báo
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
}
