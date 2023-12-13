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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

public class ImportWordTask extends Task<Void> {
    private final String file;
    private int numWordsInserted = 0;
    private int numWords;

    public ImportWordTask(String file) {
        this.file = file;
    }

    /**
     * Nhập các từ vào từ điển từ `file`. Cập nhật thanh tiến trình trong quá trình
     * thực hiện.
     *
     * @return không có gì
     */
    @Override
    protected Void call() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), StandardCharsets.UTF_8));
            numWords = StringUtil.countNumLinesOfFile(file);
            importWords(in);
            in.close();
        } catch (FileNotFoundException e) {
            handleFileNotFoundError();
        } catch (IOException e) {
            handleFileReadError();
        }
        return null;
    }

    private void importWords(BufferedReader in) throws IOException {
        String inputLine;
        int counter = 0;
        while ((inputLine = in.readLine()) != null) {
            if (isCancelled()) {
                return;
            }
            processInputLine(inputLine);
            counter++;
            System.out.println(counter);
            if (counter % 5 == 0) {
                updateProgress(counter, numWords);
            }
        }
    }

    private void processInputLine(String inputLine) {
        int pos = inputLine.indexOf(":");
        if (pos != -1) {
            String target = inputLine.substring(0, pos).strip();
            String definition = inputLine.substring(pos + 1).strip();
            tryInsertWord(target, definition);
        }
    }

    private void tryInsertWord(String target, String definition) {
        if (App.dictionary.insertWord(target, definition)) {
            Trie.getInstance().insert(target);
            System.out.println("Inserted: " + target);
            numWordsInserted++;
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

    /**
     * Hiển thị hộp thoại thông báo thành công khi công việc được thực hiện thành
     * công.
     */
    @Override
    protected void succeeded() {
        showAlert(AlertType.INFORMATION, "Thành công thêm ", numWordsInserted, numWords);
    }

    /** Hiển thị hộp thoại cảnh báo khi đóng cửa sổ trong khi đang nhập từ. */
    @Override
    protected void cancelled() {
        showAlert(AlertType.WARNING, "Quá trình nhập từ file bị gián đoạn.\n", numWordsInserted, numWords);
    }

    /** Hiển thị hộp thoại lỗi khi có lỗi trong quá trình nhập từ. */
    @Override
    protected void failed() {
        showAlert(AlertType.ERROR, "Quá trình nhập từ file gặp lỗi.\n", numWordsInserted, numWords);
    }

    private void showAlert(AlertType alertType, String prefix, int numInserted, int total) {
        Alert alert = new Alert(alertType);
        setAlertCss(alert);
        alert.setTitle("Thông báo");
        String content = prefix
                + numInserted
                + "/"
                + total
                + " từ vào từ điển.\nCó "
                + (total - numInserted)
                + " từ không được thêm vào từ điển\n(bị gián đoạn, lỗi format hoặc từ đã tồn tại).";
        alert.setContentText(content);
        alert.show();
    }

    /**
     * Set CSS cho hộp thoại thông báo trong trường hợp chế độ tối.
     *
     * @param alert hộp thoại thông báo
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
