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

    /** Thiết lập văn bản nhãn và đặt định nghĩa hiện tại của từ đang chỉnh sửa. */
    @FXML
    private void initialize() {
        editLabel.setText("Chỉnh sửa giải nghĩa của từ `" + editingWord + "`");
        if (!Application.isLightMode()) {
            htmlEditor.setHtmlText(
                    "<body style='background-color: #262837; color: #babccf'>"
                            + dictionary.lookUpWord(editingWord)
                            + "</body>");
        } else {
            htmlEditor.setHtmlText(dictionary.lookUpWord(editingWord));
        }
    }

    /**
     * Lưu định nghĩa mới cho từ đang chỉnh sửa ở định dạng HTML.
     *
     * @param event sự kiện hành động
     */
    @FXML
    public void saveDefinition(ActionEvent event) {
        byte[] ptext = htmlEditor.getHtmlText().getBytes(StandardCharsets.ISO_8859_1);
        String definition = new String(ptext, StandardCharsets.UTF_8);
        definition =
                definition.replace(
                        "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">", "");
        definition = definition.replace("</body></html>", "");
        definition = definition.replace("\"", "'");
        if (dictionary.updateWordDefinition(editingWord, definition)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            setAlertCss(alert);
            alert.setTitle("Thông báo");
            alert.setContentText("Cập nhật giải nghĩa của từ `" + editingWord + "` thành công!");
            alert.show();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            setAlertCss(alert);
            alert.setTitle("Lỗi");
            alert.setContentText(
                    "Cập nhật giải nghĩa của từ `" + editingWord + "` không thành công!");
            alert.show();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Thoát khỏi cửa sổ chỉnh sửa.
     *
     * @param event sự kiện hành động
     */
    @FXML
    public void quitWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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
