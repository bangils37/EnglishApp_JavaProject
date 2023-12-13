package dictionary.ui.controller;

import static dictionary.App.dictionary;

import dictionary.core.Trie;
import dictionary.ui.HelperUI;
import dictionary.ui.ImportWordService;
import java.nio.charset.StandardCharsets;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class AddWord {
    @FXML
    private Button browseButton;
    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private TextField inputText;
    @FXML
    private Label fileLabel;
    @FXML
    private AnchorPane anchorPane;
    private ImportWordService service;

    /** Focus on the `browseButton` when open the window. */
    @FXML
    private void initialize() {
        // Tự động chuyển focus vào `browseButton` khi cửa sổ mở.
        Platform.runLater(() -> browseButton.requestFocus());

        // Nếu không phải là chế độ sáng, thiết lập nền cho `htmlEditor`.
        if (!Application.isLightMode()) {
            htmlEditor.setHtmlText("<body style='background-color: #262837; color: #babccf'/>");
        }
    }

    /**
     * Lưu từ và định nghĩa của nó vào từ điển dưới dạng văn bản HTML.
     *
     * @param event sự kiện
     */
    @FXML
    public void saveWord(ActionEvent event) {
        String target = inputText.getText();
        byte[] pText = htmlEditor.getHtmlText().getBytes(StandardCharsets.ISO_8859_1);
        String definition = new String(pText, StandardCharsets.UTF_8);
        definition = definition
                .replaceAll("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">|</body></html>", "");
        if (dictionary.insertWord(target, definition)) {
            Trie.getInstance().insert(target);
            System.out.print(Trie.getInstance().search(target).size());
            showAlert("Thông báo", "Thêm từ `" + target + "` thành công!", AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Thêm từ `" + target + "` không thành công!", AlertType.ERROR);
        }
        closeWindow(event);
    }

    /**
     * Chọn file và hiển thị đường dẫn vào Label.
     *
     * @param event sự kiện
     */
    @FXML
    public void chooseFile(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String file = HelperUI.chooseFile(appStage);
        fileLabel.setText("  " + file);
    }

    /**
     * Nếu người dùng đóng cửa sổ trong khi đang nhập (chưa hoàn thành) thì công
     * việc cũng sẽ bị hủy.
     */
    public void closeWhileImporting() {
        if (service != null) {
            service.cancel();
        }
    }

    /**
     * Nhập từ từ file đã chọn vào từ điển. Tạo một nhiệm vụ nền để hiển thị thanh
     * tiến trình của công việc.
     */
    @FXML
    public void submitImport() {
        String filePath = fileLabel.getText().strip();
        if (!filePath.isEmpty()) {
            service = new ImportWordService(filePath);
            Region veil = createVeil();
            ProgressBar pBar = createProgressBar();
            anchorPane.getChildren().addAll(veil, pBar);
            service.start();
        }
    }

    /**
     * Đóng cửa sổ.
     *
     * @param event sự kiện
     */
    @FXML
    public void quitWindow(ActionEvent event) {
        closeWindow(event);
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

    /**
     * Đóng cửa sổ.
     *
     * @param event sự kiện
     */
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Tạo và trả về một Region cho màn đen.
     *
     * @return một Region cho màn đen
     */
    private Region createVeil() {
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        veil.setPrefSize(657, 707);
        veil.visibleProperty().bind(service.runningProperty());
        return veil;
    }

    /**
     * Tạo và trả về một thanh tiến trình cho nhiệm vụ nhập.
     *
     * @return thanh tiến trình cho nhiệm vụ nhập
     */
    private ProgressBar createProgressBar() {
        ProgressBar pBar = new ProgressBar();
        pBar.setPrefSize(200, 40);
        pBar.setStyle("-fx-progress-color: green;");
        pBar.setLayoutX(228.5);
        pBar.setLayoutY(333.5);
        pBar.progressProperty().bind(service.progressProperty());
        pBar.visibleProperty().bind(service.runningProperty());
        return pBar;
    }
}
