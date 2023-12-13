package dictionary.ui.controller;

import dictionary.translate.SpeakerStrategy;
import dictionary.translate.TranslationStrategy;
import dictionary.translate.strategy.EnSpeakerStrategy;
import dictionary.translate.strategy.EnToViTranslationStrategy;
import dictionary.translate.strategy.ViSpeakerStrategy;
import dictionary.translate.strategy.ViToEnTranslationStrategy;

import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SentencesTranslating {
    @FXML
    private TextArea sourceText;
    @FXML
    private TextArea sinkText;
    private boolean enToVi = true;
    @FXML
    private Label upButton;
    @FXML
    private Label downButton;
    @FXML
    private Button translateButton;
    @FXML
    private Button helpButton;
    @FXML
    private Button dictionaryButton;
    @FXML
    private Button voiceButton;
    @FXML
    private Button alterButton;

    private TranslationStrategy viToEnStrategy = new ViToEnTranslationStrategy();
    private TranslationStrategy enToViStrategy = new EnToViTranslationStrategy();
    private SpeakerStrategy viSpeakerStrategy = new ViSpeakerStrategy();
    private SpeakerStrategy enSpeakerStrategy = new EnSpeakerStrategy();

    public SentencesTranslating() {
    }

    @FXML
    private void initialize() {
        prepareButtonIcon(Application.isLightMode());
    }

    /**
     * Chuẩn bị biểu tượng cho tất cả các nút dựa trên `mode` được chỉ định (dark mode là
     * 0 và light mode là 1).
     *
     * @param mode biểu tượng light mode hoặc dark mode
     */
    public void prepareButtonIcon(boolean mode) {
        String suffix = (mode ? "light" : "dark");
        setButtonIcon(translateButton, "translate-icon-" + suffix + ".png", 36, 36);
        // Đặt biểu tượng cho các nút khác nếu cần
    }

    /**
     * Đặt biểu tượng cho một nút.
     *
     * @param button    nút để đặt biểu tượng
     * @param iconName  tên của tệp biểu tượng
     * @param fitHeight chiều cao để vừa với biểu tượng
     * @param fitWidth  chiều rộng để vừa với biểu tượng
     */
    private void setButtonIcon(Button button, String iconName, double fitHeight, double fitWidth) {
        ImageView icon = new ImageView(new Image("/icon/" + iconName));
        icon.setFitHeight(fitHeight);
        icon.setFitWidth(fitWidth);
        button.setGraphic(icon);
    }

    /**
     * Dịch văn bản từ tiếng Anh sang tiếng Việt (hoặc ngược lại, tùy thuộc vào trạng thái
     * hiện tại `enToVi`) và đưa ra nội dung vào sinkText.
     */
    @FXML
    public void translateEnToVi() {
        String source = sourceText.getText();
        sinkText.setText((enToVi ? enToViStrategy : viToEnStrategy).translate(source));
    }

    /**
     * Chuyển đổi từ cảnh Sentences Translator về ứng dụng chính.
     *
     * @param event sự kiện hành động
     */
    @FXML
    public void changeToApplication(ActionEvent event) {
        loadMainApplicationScene(event);
    }

    /**
     * Mở (popup) một cửa sổ hiển thị hướng dẫn cách sử dụng dịch câu.
     *
     * @param event sự kiện hành động
     */
    @FXML
    public void showSentencesInstruction(ActionEvent event) {
        showInstructionPopup();
    }

    /**
     * Phát âm thanh TTS của văn bản nguồn (từ tiếng Anh sang tiếng Việt hoặc ngược lại tùy thuộc vào
     * trạng thái hiện tại `enToVi`).
     */
    @FXML
    public void textToSpeech() {
        String source = sourceText.getText();
        (enToVi ? enSpeakerStrategy : viSpeakerStrategy).speak(source);
    }

    /**
     * Thay đổi trạng thái hiện tại `enToVi` để chuyển đổi giữa tiếng Anh và tiếng Việt hoặc
     * ngược lại.
     */
    @FXML
    public void swapLanguage() {
        enToVi = !enToVi;
        updateLanguageLabels();
    }

    /**
     * Tải cảnh chính của ứng dụng.
     *
     * @param event sự kiện hành động
     */
    private void loadMainApplicationScene(ActionEvent event) {
        try {
            Parent root = loadFXML("fxml/Application.fxml");
            setSceneAndShow(event, root, "Dictionary", "/css/Application-light.css", "/css/Application-dark.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị cửa sổ hướng dẫn popup.
     */
    private void showInstructionPopup() {
        try {
            Parent root = loadFXML("fxml/SentencesTranslatingInstructionPopup.fxml");
            Stage senInsStage = new Stage();
            Stage appStage = (Stage) translateButton.getScene().getWindow();
            senInsStage.initOwner(appStage);
            Scene scene = new Scene(root);
            setSceneAndShowPopup(senInsStage, scene, "Hướng dẫn sử dụng", "/css/General-dark.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật nhãn ngôn ngữ dựa trên cài đặt ngôn ngữ hiện tại.
     */
    private void updateLanguageLabels() {
        if (enToVi) {
            setLabelsText("English", "Vietnamese");
        } else {
            setLabelsText("Vietnamese", "English");
        }
    }

    /**
     * Set text for language labels.
     *
     * @param upLabelText   text for up label
     * @param downLabelText text for down label
     */
    private void setLabelsText(String upLabelText, String downLabelText) {
        upButton.setText(upLabelText);
        downButton.setText(downLabelText);
    }

    /**
     * Load FXML file and return its root.
     *
     * @param fxmlFileName name of the FXML file
     * @return root of the loaded FXML file
     * @throws IOException if an I/O error occurs during loading
     */
    private Parent loadFXML(String fxmlFileName) throws IOException {
        return FXMLLoader.load(getClass().getClassLoader().getResource(fxmlFileName));
    }

    /**
     * Set scene for the given stage and show it.
     *
     * @param event    action event
     * @param root     root of the scene
     * @param title    title of the stage
     * @param lightCSS light mode CSS file
     * @param darkCSS  dark mode CSS file
     */
    private void setSceneAndShow(ActionEvent event, Parent root, String title, String lightCSS, String darkCSS) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Application.isLightMode() ? lightCSS : darkCSS);
        appStage.setTitle(title);
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * Set scene for the given stage, show it as a modal popup and make it non-resizable.
     *
     * @param stage   the stage to be shown
     * @param scene   the scene to be set
     * @param title   title of the stage
     * @param darkCSS dark mode CSS file
     */
    private void setSceneAndShowPopup(Stage stage, Scene scene, String title, String darkCSS) {
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}