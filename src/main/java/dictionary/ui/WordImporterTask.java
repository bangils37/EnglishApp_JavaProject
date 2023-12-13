package dictionary.ui;

import dictionary.App;
import dictionary.core.Trie;
import dictionary.ui.controller.Application;
import dictionary.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

public class WordImporterTask extends Task<Void> {
    private final String file;
    private int numWordsInserted = 0;
    private int numWords;

    public WordImporterTask(String file) {
        this.file = file;
    }

    @Override
    protected Void call() {
        try {
            BufferedReader in = createBufferedReader();
            numWords = StringUtil.countNumLinesOfFile(file);
            importWords(in);
            in.close();
        } catch (FileNotFoundException e) {
            handleFileError("Không tìm thấy đường dẫn của file `" + file + "`!");
        } catch (IOException e) {
            handleFileError("Không đọc được file `" + file + "`!");
        }
        return null;
    }

    private BufferedReader createBufferedReader() throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    private void importWords(BufferedReader in) throws IOException {
        int counter = 0;
        while (readAndProcessInputLine(in, counter)) {
            counter++;
            handleCounterUpdate(counter);
        }
    }

    private boolean readAndProcessInputLine(BufferedReader in, int counter) throws IOException {
        String inputLine = in.readLine();
        return inputLine != null && !isCancelled() && processInputLine(inputLine);
    }

    private boolean processInputLine(String inputLine) {
        int pos = inputLine.indexOf(":");
        if (pos != -1) {
            String target = extractTarget(inputLine, pos);
            String definition = extractDefinition(inputLine, pos);
            return tryInsertWord(target, definition);
        }
        return true;
    }

    private String extractTarget(String inputLine, int pos) {
        return inputLine.substring(0, pos).strip();
    }

    private String extractDefinition(String inputLine, int pos) {
        return inputLine.substring(pos + 1).strip();
    }

    private boolean tryInsertWord(String target, String definition) {
        if (App.dictionary.insertWord(target, definition)) {
            Trie.getInstance().insert(target);
            handleWordInsertionSuccess(target);
            return true;
        }
        return false;
    }

    private void handleWordInsertionSuccess(String target) {
        System.out.println("Inserted: " + target);
        numWordsInserted++;
    }

    private void handleCounterUpdate(int counter) {
        System.out.println(counter);
        if (counter % 5 == 0) {
            updateProgress(counter, numWords);
        }
    }

    private void handleFileError(String content) {
        showErrorAlert("Lỗi", content);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = createErrorAlert(title, content);
        alert.show();
    }

    private Alert createErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        setAlertCss(alert);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert;
    }

    @Override
    protected void succeeded() {
        showSuccessAlert();
    }

    private void showSuccessAlert() {
        showAlert(AlertType.INFORMATION, "Thông báo", "Thành công thêm ", numWordsInserted, numWords);
    }

    @Override
    protected void cancelled() {
        showAlert(AlertType.WARNING, "Thông báo", "Quá trình nhập từ file bị gián đoạn.\n", numWordsInserted, numWords);
    }

    @Override
    protected void failed() {
        showAlert(AlertType.ERROR, "Thông báo", "Quá trình nhập từ file gặp lỗi.\n", numWordsInserted, numWords);
    }

    private void showAlert(AlertType alertType, String title, String prefix, int numInserted, int total) {
        Alert alert = createInfoAlert(alertType, title, prefix, numInserted, total);
        alert.show();
    }

    private Alert createInfoAlert(AlertType alertType, String title, String prefix, int numInserted, int total) {
        Alert alert = new Alert(alertType);
        setAlertCss(alert);
        alert.setTitle(title);
        String content = prefix + numInserted + "/" + total + " từ vào từ điển.\nCó "
                + (total - numInserted)
                + " từ không được thêm vào từ điển\n(bị gián đoạn, lỗi format hoặc từ đã tồn tại).";
        alert.setContentText(content);
        return alert;
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
