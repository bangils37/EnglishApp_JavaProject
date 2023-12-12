package dictionary.ui.controller;

import static dictionary.App.dictionary;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class EditDefinition {
    private static String editingWord;
    @FXML private Label editLabel;
    @FXML private HTMLEditor htmlEditor;

    public static void setEditingWord(String editingWord) {
        EditDefinition.editingWord = editingWord;
    }

    /** Đặt văn bản cho nhãn và đặt định nghĩa hiện tại của từ đang chỉnh sửa. */
    @FXML
    private void initialize() {
        editLabel.setText("Chỉnh sửa giải nghĩa của từ `" + editingWord + "`");
        setHtmlEditorText();
    }

    /**
     * Đặt văn bản HTML của trình soạn thảo dựa trên định nghĩa hiện tại của từ đang chỉnh sửa và chế độ ứng dụng.
     */
    private void setHtmlEditorText() {
        String currentDefinition = dictionary.lookUpWord(editingWord);
        if (!Application.isLightMode()) {
            htmlEditor.setHtmlText("<body style='background-color: #262837; color: #babccf'>" + currentDefinition + "</body>");
        } else {
            htmlEditor.setHtmlText(currentDefinition);
        }
    }

    /**
     * Lưu định nghĩa mới cho từ đang chỉnh sửa dưới dạng HTML.
     *
     * @param event sự kiện
     */
    @FXML
    public void saveDefinition(ActionEvent event) {
        byte[] ptext = htmlEditor.getHtmlText().getBytes(StandardCharsets.ISO_8859_1);
        String definition = new String(ptext, StandardCharsets.UTF_8);
        definition = definition.replaceAll("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">|</body></html>", "");
        definition = definition.replace("\"", "'");
        if (dictionary.updateWordDefinition(editingWord, definition)) {
            showAlert("Thông báo", "Cập nhật giải nghĩa của từ `" + editingWord + "` thành công!", AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Cập nhật giải nghĩa của từ `" + editingWord + "` không thành công!", AlertType.ERROR);
        }
        closeWindow(event);
    }

    /**
     * Đóng cửa sổ chỉnh sửa.
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
}
