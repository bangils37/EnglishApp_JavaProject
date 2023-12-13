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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExportToFile {

    @FXML
    private Button browseButton;

    @FXML
    private Label dirLabel;

    @FXML
    private TextField fileName;

    @FXML
    private void initialize() {
        Platform.runLater(() -> browseButton.requestFocus());
    }

    @FXML
    public void chooseDir(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String dir = HelperUI.chooseDir(appStage);
        dirLabel.setText("  " + dir);
    }

    @FXML
    public void submitExport() {
        String file = fileName.getText().strip();
        String dirPath = dirLabel.getText().strip();
        if (!dirPath.isEmpty() && !file.isEmpty()) {
            exportToFile(dirPath, file);
        }
    }

    private void exportToFile(String dirPath, String file) {
        try {
            String filePath = dirPath + "\\" + file;
            dictionary.exportToFile(filePath);
            showAlert("Thông báo", "Thành công xuất dữ liệu ra file `" + filePath + "`", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không tìm thấy đường dẫn của file!", Alert.AlertType.ERROR);
        }
    }

    private void setAlertCss(Alert alert) {
        if (!Application.isLightMode()) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/Alert-dark.css")).toExternalForm());
            dialogPane.getStyleClass().add("alert");
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        setAlertCss(alert);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
