package dictionary.ui.controller;

import static dictionary.App.dictionary;

import dictionary.core.Trie;
import dictionary.ui.HelperUI;
import dictionary.ui.WordImporterTask;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.util.Objects;

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

    private Service<Void> service;

    @FXML
    private void initialize() {
        Platform.runLater(() -> initializeBrowseButtonFocus());

        if (!Application.isLightMode()) {
            setDarkModeHtmlEditorStyle();
        }
    }

    private void initializeBrowseButtonFocus() {
        browseButton.requestFocus();
    }

    private void setDarkModeHtmlEditorStyle() {
        htmlEditor.setHtmlText("<body style='background-color: #262837; color: #babccf'/>");
    }

    /**
     * Xử lý sự kiện lưu từ vào từ điển.
     *
     * @param event sự kiện lưu từ
     */
    @FXML
    public void saveWord(ActionEvent event) {
        String target = inputText.getText();
        String definition = extractDefinitionFromHtmlEditor();
        processSaveWord(target, definition, event);
    }

    private String extractDefinitionFromHtmlEditor() {
        String htmlText = htmlEditor.getHtmlText();
        return htmlText.replaceAll("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">|</body></html>", "")
                .replace("\"", "'");
    }

    /**
     * Xử lý lưu từ vào từ điển.
     *
     * @param target     từ cần lưu
     * @param definition định nghĩa của từ
     * @param event      sự kiện
     */
    private void processSaveWord(String target, String definition, ActionEvent event) {
        if (dictionary.insertWord(target, definition)) {
            Trie.getInstance().insert(target);
            System.out.print(Trie.getInstance().search(target).size());
            showAlert("Thông báo", "Thêm từ `" + target + "` thành công!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Thêm từ `" + target + "` không thành công!", Alert.AlertType.ERROR);
        }
        closeWindow(event);
    }

    @FXML
    public void chooseFile(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String file = HelperUI.chooseFile(appStage);
        updateFileLabel(file);
    }

    private void updateFileLabel(String file) {
        fileLabel.setText("  " + file);
    }

    public void closeWhileImporting() {
        if (service != null) {
            service.cancel();
        }
    }

    @FXML
    public void submitImport() {
        String filePath = fileLabel.getText().strip();
        if (!filePath.isEmpty()) {
            startImportService(filePath);
        }
    }

    private void startImportService(String filePath) {
        service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new WordImporterTask(filePath);
            }
        };

        Region veil = createVeil();
        ProgressBar pBar = createProgressBar();
        anchorPane.getChildren().addAll(veil, pBar);
        service.start();
        System.out.println(filePath);
    }

    @FXML
    public void quitWindow(ActionEvent event) {
        closeWindow(event);
    }

    private void setAlertCss(Alert alert) {
        if (!Application.isLightMode()) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets()
                    .add(Objects.requireNonNull(getClass().getResource("/css/Alert-dark.css")).toExternalForm());
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

    private Region createVeil() {
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        veil.setPrefSize(657, 707);
        veil.visibleProperty().bind(service.runningProperty());
        return veil;
    }

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
