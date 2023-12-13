package dictionary.ui.controller;

import static dictionary.App.dictionary;

import dictionary.core.History;
import dictionary.core.Trie;
import dictionary.translate.SpeakerStrategy;
import dictionary.translate.strategy.EnSpeakerStrategy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Application {
    public static boolean lightMode = true;
    private String lastLookUpWord = "";
    @FXML
    private TextField inputText;
    @FXML
    private ListView<String> searchList;
    @FXML
    private WebView webView;
    private int lastIndex = 0;
    private Image historyIcon;
    @FXML
    private Button addWordButton;
    @FXML
    private Button showInformationButton;
    @FXML
    private Button showInstructionButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button pronounceButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button googleButton;
    @FXML
    private Button modeToggle;

    private SpeakerStrategy speakerStrategy;

    public Application() {
        speakerStrategy = new EnSpeakerStrategy();
    }

    public static boolean isLightMode() {
        return lightMode;
    }

    public static void toggleMode() {
        lightMode = !lightMode;
    }

    /**
     * Focus on the inputText TextField when first open. Prepare the search list
     * after that.
     */
    @FXML
    private void initialize() {
        Platform.runLater(() -> inputText.requestFocus());
        prepareWebView();
        prepareHistoryIcon(isLightMode());
        prepareButtonIcon(isLightMode());
        prepareSearchList();
    }

    @FXML
    public void toggleModeButton() {
        toggleMode();
        updateSceneStylesheet();
        inputText.requestFocus();
        prepareHistoryIcon(!isLightMode());
        prepareButtonIcon(!isLightMode());
        if (!lastLookUpWord.isEmpty()) {
            lookUpWord();
        } else {
            updateWebViewContent();
        }
        prepareSearchList();
    }

    private void updateSceneStylesheet() {
        modeToggle.getScene().getStylesheets().clear();
        modeToggle.getScene().getStylesheets().add(
                isLightMode() ? "/css/Application-light.css" : "/css/Application-dark.css");
    }

    private void updateWebViewContent() {
        String backgroundColor = isLightMode() ? "#D2F6F7" : "#1D3C69";
        String textColor = isLightMode() ? "#000000" : "#babccf";
        webView.getEngine().loadContent(
                String.format("<html><body bgcolor='%s' style='color:%s'></body></html>",
                        backgroundColor, textColor),
                "text/html");
    }

    public void prepareWebView() {
        updateWebViewContent();
    }

    /**
     * Prepare the icons of all the buttons based on the given `mode` (dark mode is
     * 0 and light mode
     * is 1).
     *
     * @param mode light mode or dark mode icons
     */
    public void prepareButtonIcon(boolean mode) {
    }

    /**
     * Move to the search list by pressing DOWN arrow key when at the `inputText`
     * TextField.
     *
     * @param event action event
     */
    @FXML
    public void changeFocusDown(KeyEvent event) {
        if (event.getCode() == KeyCode.DOWN) {
            searchList.requestFocus();
            if (!searchList.getItems().isEmpty()) {
                searchList.getSelectionModel().select(0);
            }
        }
    }

    /** Load the history icon into its corresponding icon image. */
    private void prepareHistoryIcon(boolean isLightMode) {
        String iconType = isLightMode ? "light" : "dark";
        try {
            historyIcon = new Image(
                    new FileInputStream(
                            "src/main/resources/icon/history-icon-" + iconType + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prepare the search lists having the text in `inputText` as prefix. Words in
     * the history base
     * appears first in the list, and they begin with a "history" icon.
     */
    public void prepareSearchList() {
        searchList.getItems().clear();
        String target = inputText.getText();
        ArrayList<String> searchedWords = Trie.getInstance().search(target);
        List<String> allHistory = History.getInstance().getHistorySearch();

        populateSearchListWithHistory(allHistory, target);
        populateSearchListWithSearchedWords(searchedWords);
        configureSearchListCellFactory();
    }

    private void populateSearchListWithHistory(List<String> allHistory, String target) {
        for (int i = allHistory.size() - 1; i >= 0; i--) {
            if (target.isEmpty() || allHistory.get(i).startsWith(target)) {
                searchList.getItems().add("#" + allHistory.get(i));
            }
        }
    }

    private void populateSearchListWithSearchedWords(ArrayList<String> searchedWords) {
        for (String w : searchedWords) {
            searchList.getItems().add(w);
        }
    }

    private void configureSearchListCellFactory() {
        searchList.setCellFactory(
                new Callback<>() {
                    @Override
                    public ListCell<String> call(ListView<String> list) {
                        return new ListCell<>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setGraphic(null);
                                    setText(null);
                                } else if (item.charAt(0) != '#') {
                                    setGraphic(null);
                                    setText(item);
                                    setFont(Font.font(15));
                                } else {
                                    ImageView imageView = new ImageView(historyIcon);
                                    imageView.setFitHeight(15);
                                    imageView.setFitWidth(15);
                                    setGraphic(imageView);
                                    setText("  " + item.substring(1));
                                    setFont(Font.font(15));
                                }
                            }
                        };
                    }
                });
    }

    /** Look up the word in the dictionary and show its definition in `webView`. */
    @FXML
    public void lookUpWord() {
        String target = inputText.getText();
        if (target.startsWith("#")) {
            target = target.substring(1);
        }
        if (!target.isEmpty()) {
            History.getInstance().addWordToHistory(target);
        }

        String definition = dictionary.lookUpWord(target);
        handleDefinitionResult(definition);
    }

    private void handleDefinitionResult(String definition) {
        if (definition.equals("404")) {
            showNotFoundErrorAlert();
            resetWebViewContent();
        } else {
            updateWebViewContent(definition);
        }
    }

    private void showNotFoundErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setAlertCss(alert);
        alert.setTitle("Thông báo");
        alert.setContentText("Từ này không tồn tại!");
        alert.show();
    }

    private void resetWebViewContent() {
        String backgroundColor = Application.isLightMode() ? "#D2F6F7" : "#1D3C69";
        String textColor = Application.isLightMode() ? "#000000" : "#babccf";
        String content = String.format("<html><body bgcolor='%s' style='color:%s'></body></html>",
                backgroundColor, textColor);
        webView.getEngine().loadContent(content, "text/html");
    }

    private void updateWebViewContent(String definition) {
        String backgroundColor = Application.isLightMode() ? "#D2F6F7" : "#1D3C69";
        String textColor = Application.isLightMode() ? "#000000" : "#babccf";
        String formattedDefinition = String.format("<html><body bgcolor='%s' style='color:%s'>%s</body></html>",
                backgroundColor, textColor, definition);
        webView.getEngine().loadContent(formattedDefinition, "text/html");
    }

    /**
     * Look up word when pressing Enter at the selected word from the search list
     *
     * @param e key event
     */
    @FXML
    public void selectWord(KeyEvent e) {
        if (searchList.getSelectionModel().getSelectedIndices().isEmpty()) {
            return;
        }
        if (e.getCode() == KeyCode.ENTER) {
            handleEnterKeyPress();
        } else if (e.getCode() == KeyCode.UP) {
            handleUpKeyPress();
        }
        lastIndex = searchList.getSelectionModel().getSelectedIndex();
    }

    private void handleEnterKeyPress() {
        String target = searchList.getSelectionModel().getSelectedItem();
        if (target != null) {
            inputText.setText(target.startsWith("#") ? target.substring(1) : target);
            lookUpWord();
        }
    }

    private void handleUpKeyPress() {
        if (searchList.getSelectionModel().getSelectedIndex() == 0 && lastIndex == 0) {
            inputText.requestFocus();
        }
    }

    /**
     * Double-click a word in the search list to look up its definition.
     *
     * <p>
     * The double-clicked word will be added to the history though.
     *
     * @param mouseEvent mouse event
     */
    @FXML
    public void selectWordDoubleClick(MouseEvent mouseEvent) {
        if (isDoubleClick(mouseEvent)) {
            handleDoubleClick();
        }
    }

    private boolean isDoubleClick(MouseEvent mouseEvent) {
        return mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2;
    }

    private void handleDoubleClick() {
        String target = searchList.getSelectionModel().getSelectedItem();
        if (target != null) {
            inputText.setText(target.startsWith("#") ? target.substring(1) : target);
            lookUpWord();
        }
    }

    @FXML
    public void changeToGame(ActionEvent event) {
        // Thành code chuyển scene game ở đây nhá
    }

    /**
     * Change scene to sentences translating (Google Translate).
     *
     * @param event action event
     */
    @FXML
    public void changeToSentencesTranslating(ActionEvent event) {
        try {
            loadSentencesTranslatingScene(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSentencesTranslatingScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/SentencesTranslating.fxml"));
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String cssPath = Application.isLightMode() ? "/css/SentencesTranslating-light.css"
                : "/css/SentencesTranslating-dark.css";
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm());
        appStage.setTitle("Sentences Translator");
        appStage.setScene(scene);
        appStage.show();
    }

    /** Pronounce the English word that is currently shown in the `webView`. */
    @FXML
    public void playSound() {
        if (isLastLookUpWordNotEmpty()) {
            speakerStrategy.speak(lastLookUpWord);
        }
    }

    private boolean isLastLookUpWordNotEmpty() {
        return !lastLookUpWord.isEmpty();
    }

    /**
     * Open (pop up) export to file window for words export to file utility.
     *
     * @param event action event
     */
    @FXML
    public void exportToFile(ActionEvent event) {
        try {
            openExportToFileWindow(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openExportToFileWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/ExportToFile.fxml"));
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage newStage = createPopUpStage(root, appStage, "Xuất dữ liệu từ điển");
        newStage.show();
    }

    private Stage createPopUpStage(Parent root, Stage ownerStage, String title) {
        Stage newStage = new Stage();
        Scene scene = new Scene(root);
        setGeneralCss(scene);
        newStage.setScene(scene);
        newStage.initOwner(ownerStage);
        newStage.setTitle(title);
        newStage.initModality(Modality.APPLICATION_MODAL);
        return newStage;
    }

    /**
     * Open (pop up) information details of the application.
     *
     * @param event action event
     */
    public void showInformation(ActionEvent event) {
        try {
            openInformationPopup(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openInformationPopup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/InformationPopup.fxml"));
        Stage infStage = createPopUpStage(root, (Stage) ((Node) event.getSource()).getScene().getWindow(),
                "Về ứng dụng");
        infStage.show();
    }

    /**
     * Open (pop up) the application instruction.
     *
     * @param event action event
     */
    @FXML
    public void showInstruction(ActionEvent event) {
        try {
            openInstructionPopup(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openInstructionPopup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/InstructionPopup.fxml"));
        Stage insStage = createPopUpStage(root, (Stage) ((Node) event.getSource()).getScene().getWindow(),
                "Hướng dẫn sử dụng");
        insStage.show();
    }

    /**
     * Open (pop up) the edit word window for the currently looked up word (in the
     * `webView`).
     *
     * @param event action event
     */
    @FXML
    public void editWordDefinition(ActionEvent event) {
        if (isLastLookUpWordEmpty()) {
            showSelectWordErrorAlert();
            return;
        }
        if (isWordNotFound()) {
            showWordNotFoundErrorAlert();
            return;
        }

        EditDefinition.setEditingWord(lastLookUpWord);
        try {
            openEditDefinitionWindow(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLastLookUpWordEmpty() {
        return lastLookUpWord.isEmpty();
    }

    private void showSelectWordErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        setAlertCss(alert);
        alert.setTitle("Thông báo");
        alert.setContentText("Chưa chọn từ để chỉnh sửa!");
        alert.show();
    }

    private boolean isWordNotFound() {
        return dictionary.lookUpWord(lastLookUpWord).equals("404");
    }

    private void showWordNotFoundErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        setAlertCss(alert);
        alert.setTitle("Lỗi");
        alert.setContentText("Không tồn tại từ `" + lastLookUpWord + "` trong từ điển để chỉnh sửa!");
        alert.show();
    }

    private void openEditDefinitionWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/EditDefinition.fxml"));
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage newStage = createPopUpStage(root, appStage, "Chỉnh sửa giải nghĩa của từ");
        newStage.show();
    }

    @FXML
    public void deleteWord() {
        if (lastLookUpWord.isEmpty()) {
            showAlert("Thông báo", "Chưa chọn từ để xóa!", AlertType.ERROR);
        } else {
            if (showConfirmationAlert("Xóa từ",
                    "Bạn có chắc chắn muốn xóa từ `" + lastLookUpWord + "` khỏi từ điển hay không?")) {
                performDeleteWord();
            }
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = createAlert(title, content, type);
        alert.show();
    }

    private boolean showConfirmationAlert(String title, String headerText) {
        Alert confirmationAlert = createAlert(title, headerText, AlertType.CONFIRMATION);
        Optional<ButtonType> option = confirmationAlert.showAndWait();
        return option.map(buttonType -> buttonType == ButtonType.OK).orElse(false);
    }

    private Alert createAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        setAlertCss(alert);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert;
    }

    private void performDeleteWord() {
        if (dictionary.deleteWord(lastLookUpWord)) {
            showAlert("Thông báo", "Xóa từ `" + lastLookUpWord + "` thành công!", AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không tồn tại từ `" + lastLookUpWord + "` trong từ điển để xóa!", AlertType.ERROR);
        }
        lastLookUpWord = "";
    }

    @FXML
    public void addingWord(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/AddWord.fxml"));
            Parent root = loader.load();
            AddWord controller = loader.getController();
            Stage addStage = createAndShowStage("Thêm từ", root, event);
            addStage.setOnCloseRequest(e -> controller.closeWhileImporting());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage createAndShowStage(String title, Parent root, ActionEvent event) {
        Stage newStage = new Stage();
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(appStage);
        Scene scene = new Scene(root);
        setGeneralCss(scene);
        newStage.setTitle(title);
        newStage.setResizable(false);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();
        return newStage;
    }

    private void setGeneralCss(Scene scene) {
        String cssPath = Application.isLightMode() ? "/css/General-light.css" : "/css/General-dark.css";
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm());
    }

    private void setAlertCss(Alert alert) {
        if (!Application.isLightMode()) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets()
                    .add(Objects.requireNonNull(getClass().getResource("/css/Alert-dark.css")).toExternalForm());
            dialogPane.getStyleClass().add("alert");
        }
    }
}
