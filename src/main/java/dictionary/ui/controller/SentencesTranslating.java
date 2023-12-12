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
    @FXML private TextArea sourceText;
    @FXML private TextArea sinkText;
    private boolean enToVi = true;
    @FXML private Label upButton;
    @FXML private Label downButton;
    @FXML private Button translateButton;
    @FXML private Button helpButton;
    @FXML private Button dictionaryButton;
    @FXML private Button voiceButton;
    @FXML private Button alterButton;

    private TranslationStrategy viToEnStrategy = new ViToEnTranslationStrategy();
    private TranslationStrategy enToViStrategy = new EnToViTranslationStrategy();
    private SpeakerStrategy viSpeakerStrategy = new ViSpeakerStrategy();
    private SpeakerStrategy enSpeakerStrategy = new EnSpeakerStrategy();

    public SentencesTranslating() {}

    @FXML
    private void initialize() {
        prepareButtonIcon(Application.isLightMode());
    }

    /**
     * Chuẩn bị biểu tượng cho tất cả các nút dựa trên `mode` được chỉ định (dark mode là
     * 0 và light mode
     * là 1).
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
     * @param button     nút để đặt biểu tượng
     * @param iconName   tên của tệp biểu tượng
     * @param fitHeight  chiều cao để vừa với biểu tượng
     * @param fitWidth   chiều rộng để vừa với biểu tượng
     */
    private void setButtonIcon(Button button, String iconName, double fitHeight, double fitWidth) {
        ImageView icon = new ImageView(new Image("/icon/" + iconName));
        icon.setFitHeight(fitHeight);
        icon.setFitWidth(fitWidth);
        button.setGraphic(icon);
    }

    /**
     * Dịch văn bản từ tiếng Anh sang tiếng Việt (hoặc ngược lại, tùy thuộc vào trạng thái
     * hiện tại `enToVi`)
     * và đưa ra nội dung vào sinkText.
     */
    @FXML
    public void translateEnToVi() {
        String source = sourceText.getText();
        sinkText.setText(
                (enToVi ? enToViStrategy : viToEnStrategy)
                        .translate(source));
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
     * trạng thái hiện tại
     * `enToVi`).
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
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass()
                                    .getClassLoader()
                                    .getResource("fxml/Application.fxml")));
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                            getClass()
                                                    .getResource(
                                                            (Application.isLightMode()
                                                                    ? "/css/Application-light.css"
                                                                    : "/css/Application-dark.css")))
                                    .toExternalForm());
            appStage.setTitle("Dictionary");
            appStage.setScene(scene);
            appStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị cửa sổ hướng dẫn popup.
     */
    private void showInstructionPopup() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass()
                                    .getClassLoader()
                                    .getResource(
                                            "fxml/SentencesTranslatingInstructionPopup.fxml")));
            Stage senInsStage = new Stage();
            Stage appStage = (Stage) translateButton.getScene().getWindow();
            senInsStage.initOwner(appStage);
            Scene scene = new Scene(root);
            if (!Application.isLightMode()) {
                scene.getStylesheets()
                        .add(
                                Objects.requireNonNull(
                                                getClass().getResource("/css/General-dark.css"))
                                        .toExternalForm());
            }
            senInsStage.setTitle("Hướng dẫn sử dụng");
            senInsStage.setResizable(false);
            senInsStage.setScene(scene);
            senInsStage.initModality(Modality.APPLICATION_MODAL);
            senInsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật nhãn ngôn ngữ dựa trên cài đặt ngôn ngữ hiện tại.
     */
    private void updateLanguageLabels() {
        if (enToVi) {
            upButton.setText("English");
            downButton.setText("Vietnamese");
        } else {
            upButton.setText("Vietnamese");
            downButton.setText("English");
        }
    }
}
