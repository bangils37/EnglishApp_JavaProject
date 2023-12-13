package dictionary.ui.controller;

import static dictionary.App.dictionary;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class EditDefinition {
    private static String editingWord;

    @FXML
    private Label editLabel;

    @FXML
    private HTMLEditor htmlEditor;

    public static void setEditingWord(String editingWord) {
        EditDefinition.editingWord = editingWord;
    }

    @FXML
    private void initialize() {
        setLabelAndHtmlEditorText();
    }

    private void setLabelAndHtmlEditorText() {
        editLabel.setText("Chỉnh sửa giải nghĩa của từ `" + editingWord + "`");
        setHtmlEditorText();
    }

    private void setHtmlEditorText() {
        String currentDefinition = dictionary.lookUpWord(editingWord);
        String backgroundColor = Application.isLightMode() ? "" : "background-color: #262837; color: #babccf";
        htmlEditor.setHtmlText(String.format("<body style='%s'>%s</body>", backgroundColor, currentDefinition));
    }

    @FXML
    public void saveDefinition(ActionEvent event) {
        String definition = extractDefinitionFromHtmlEditor();
        if (dictionary.updateWordDefinition(editingWord, definition)) {
            showAlert("Thông báo", "Cập nhật giải nghĩa của từ `" + editingWord + "` thành công!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Cập nhật giải nghĩa của từ `" + editingWord + "` không thành công!", Alert.AlertType.ERROR);
        }
        closeWindow(event);
    }

    private String extractDefinitionFromHtmlEditor() {
        String htmlText = htmlEditor.getHtmlText();
        String definition = htmlText.replaceAll("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">|</body></html>", "");
        return definition.replace("\"", "'");
    }

    @FXML
    public void quitWindow(ActionEvent event) {
        closeWindow(event);
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

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
