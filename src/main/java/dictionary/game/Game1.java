package dictionary.game;

import dictionary.ui.controller.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static dictionary.App.dictionary;


public class Game1 {
    @FXML
    private WebView webView;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button AButton, BButton, CButton, DButton;

    @FXML
    private TextField AtextField, BtextField, CtextField, DtextField;

    @FXML
    ImageView animationView;

    @FXML
    ImageView animationNariRun;

    @FXML
    ImageView attackView;

    @FXML
    Button playAnimationButton, stopAnimationButton;

    Button correctButton;
    int score = 0;
    final int numberOfQuestion = 25;
    int currentNumberQuestion = 0;
    int correctAnswerID;
    int correctAnswerCharID;
    AnimationPanel animationPanel;

    public void resetInfo() {
        currentNumberQuestion = 0;
        score = 0;
    }

    @FXML
    void playAnim(ActionEvent event) {
        animationPanel = new AnimationPanel(animationView, animationNariRun,attackView);
        animationPanel.startAnimationThread();
        playAnimationButton.setVisible(false);
        stopAnimationButton.setVisible(true);
    }

    @FXML
    void stopAnim(ActionEvent event) {
        animationPanel.stopAnimationThread();
        playAnimationButton.setVisible(true);
        stopAnimationButton.setVisible(false);
    }

    @FXML
    void close(ActionEvent event) {
        animationPanel.stopAnimationThread();
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    @FXML
    public void clickChange(ActionEvent event) {
        currentNumberQuestion++;
        scoreLabel.setText("Score: " + score);

        if (currentNumberQuestion > numberOfQuestion) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(
                    "Điểm của bạn là:" + Integer.toString(score) + " " + Integer.toString(currentNumberQuestion - 1) + "\n" +
                            "Bạn có muốn làm bài kiểm tra lại hay không?\n");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent()) {
                if (option.get() == ButtonType.OK) {
                    resetInfo();
                    clickChange(new ActionEvent());
                } else if (option.get() == ButtonType.CANCEL) {
                    close(new ActionEvent());
                }
            }
        }
        Random rand = new Random();
        correctAnswerID = rand.nextInt(138481) + 1;

        String definition = dictionary.lookUpWordByIDGetDefinition(correctAnswerID);

        StringBuffer bffDefinition = new StringBuffer(definition);

        int firstDeleteID = -1;
        int lastDeleteID = -1;

        for (int i = 0; i < definition.length(); i++) {
            if (definition.charAt(i) == '@') {
                firstDeleteID = i;
            }

            if (definition.charAt(i) == '<' && firstDeleteID != -1) {
                lastDeleteID = i;
                bffDefinition.replace(firstDeleteID, lastDeleteID, " ");
                firstDeleteID = -1;
            }
        }

        definition = bffDefinition.toString();

        String htmlContent = String.format("<html><body style='background: #152A5D; color: #ffffff'>" +
                "<div style='border-radius: 20px; padding: 10px;'>%s</div>" + "</body></html>", definition);

        webView.getEngine().loadContent(definition, "text/html");

        String target = dictionary.lookUpWordByIDGetTarget(rand.nextInt(138481) + 1);
        AtextField.setText(target);

        target = dictionary.lookUpWordByIDGetTarget(rand.nextInt(138481) + 1);
        BtextField.setText(target);

        target = dictionary.lookUpWordByIDGetTarget(rand.nextInt(138481) + 1);
        CtextField.setText(target);

        target = dictionary.lookUpWordByIDGetTarget(rand.nextInt(138481) + 1);
        DtextField.setText(target);

        randomAnswer();
    }

    @FXML
    void checkCorrectButton(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Button) {
            if ((Button) source == correctButton) {
                score++;
                scoreLabel.setText("Score: " + score);
//                System.out.println("dung");
            } else {
//                System.out.println("sai");
            }
            clickChange(new ActionEvent());
        }
    }

    void randomAnswer() {
        Random rand = new Random();
        correctAnswerCharID = rand.nextInt(4);

        String target = dictionary.lookUpWordByIDGetTarget(correctAnswerID);
        switch (correctAnswerCharID) {
            case 0:
                AtextField.setText(target);
                correctButton = AButton;
                break;
            case 1:
                BtextField.setText(target);
                correctButton = BButton;
                break;
            case 2:
                CtextField.setText(target);
                correctButton = CButton;
                break;
            case 3:
                DtextField.setText(target);
                correctButton = DButton;
                break;
        }
    }

    @FXML
    public void changeToApplication(ActionEvent event) {
        loadMainApplicationScene(event);
    }

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
}