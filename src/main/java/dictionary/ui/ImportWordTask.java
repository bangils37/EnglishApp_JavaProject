package dictionary.ui;

import dictionary.App;
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

public class ImportWordTask extends Task<Void> {

    private final String file;
    private int numWordsInserted = 0;
    private int numWords;

    public ImportWordTask(String file) {
        this.file = file;
    }

    @Override
    protected Void call() {
        try {
            initializeImport();
            importWordsFromFile();
        } catch (FileNotFoundException e) {
            handleFileNotFoundError();
        } catch (IOException e) {
            handleFileReadError();
        }
        return null;
    }

    private void initializeImport() {
        try (BufferedReader in = createBufferedReader()) {
            numWords = StringUtil.countNumLinesOfFile(file);
        } catch (IOException e) {
            handleFileReadError();
        }
    }

    private BufferedReader createBufferedReader() throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8));
    }

    private void importWordsFromFile() throws IOException {
        try (BufferedReader in = createBufferedReader()) {
            processInputLines(in);
        }
    }

    private void processInputLines(BufferedReader in) throws IOException {
        int counter = 0;
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (isCancelled()) {
                return;
            }
            processInputLine(inputLine);
            counter++;
            updateProgressIfNeeded(counter);
        }
    }

    private void processInputLine(String inputLine) {
        int pos = findTabPosition(inputLine);
        if (pos != -1) {
            String target = extractTarget(inputLine, pos);
            String definition = extractDefinition(inputLine, pos);
            tryInsertWord(target, definition);
        }
    }

    private int findTabPosition(String inputLine) {
        return inputLine.indexOf("\t");
    }

    private String extractTarget(String inputLine, int pos) {
        return inputLine.substring(0, pos).strip();
    }

    private String extractDefinition(String inputLine, int pos) {
        return inputLine.substring(pos + 1).strip();
    }

    private void tryInsertWord(String target, String definition) {
        if (App.dictionary.insertWord(target, definition)) {
            logInsertedWord(target);
            numWordsInserted++;
        }
    }

    private void logInsertedWord(String target) {
        System.out.println("Inserted: " + target);
    }

    private void updateProgressIfNeeded(int counter) {
        if (counter % 5 == 0) {
            updateProgress(counter, numWords);
        }
    }

    private void handleFileNotFoundError() {
        showErrorAlert("Không tìm thấy đường dẫn của file `" + file + "`!");
    }

    private void handleFileReadError() {
        showErrorAlert("Không đọc được file `" + file + "`!");
    }

    private void showErrorAlert(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        setAlertCss(alert);
        alert.setTitle("Lỗi");
        alert.setContentText(content);
        alert.show();
    }

    private void showAlert(AlertType alertType, String prefix, int numInserted, int total) {
        Alert alert = new Alert(alertType);
        setAlertCss(alert);
        alert.setTitle("Thông báo");
        String content = getContentForAlert(prefix, numInserted, total);
        alert.setContentText(content);
        alert.show();
    }

    private String getContentForAlert(String prefix, int numInserted, int total) {
        return prefix
                + numInserted
                + "/"
                + total
                + " từ vào từ điển.\nCó "
                + (total - numInserted)
                + " từ không được thêm vào từ điển\n(bị gián đoạn, lỗi format hoặc từ đã tồn tại).";
    }

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

    @Override
    protected void succeeded() {
        showAlert(AlertType.INFORMATION, "Thành công thêm ", numWordsInserted, numWords);
    }

    @Override
    protected void cancelled() {
        showAlert(AlertType.WARNING, "Quá trình nhập từ file bị gián đoạn.\n", numWordsInserted, numWords);
    }

    @Override
    protected void failed() {
        showAlert(AlertType.ERROR, "Quá trình nhập từ file gặp lỗi.\n", numWordsInserted, numWords);
    }
}
