package dictionary.core;

import java.io.File;
import java.util.List;
import dictionary.util.FileUtil;

public class History {
    private List<String> historySearch = null;

    private static History instance = null;

    private History() {
        loadHistory();
    }

    public static History getInstance() {
        if (instance == null) {
            instance = new History();
        }
        return instance;
    }

    public List<String> getHistorySearch() {
        return historySearch;
    }

    /**
     * Tải lịch sử tìm kiếm vào danh sách `historySearch`.
     */
    public void loadHistory() {
        createHistoryDirectoryAndFile();
        loadHistoryFromFile(Config.HISTORY_FILE_PATH);
        refactorHistory();
    }

    /**
     * Tạo thư mục và tệp lịch sử nếu chưa tồn tại.
     */
    private void createHistoryDirectoryAndFile() {
        File directory = new File(Config.HISTORY_DIRECTORY_PATH);
        FileUtil.createDirectoryIfNotExists(directory);

        File historyFile = new File(Config.HISTORY_FILE_PATH);
        FileUtil.createFileIfNotExists(historyFile);
    }

    public void removeWordFromHistory(String target) {
        historySearch.removeIf(e -> e.equals(target));
    }

    /**
     * Tải lịch sử tìm kiếm từ tệp đã lưu.
     */
    private void loadHistoryFromFile(String filePath) {
        historySearch = FileUtil.loadLinesFromFile(filePath);
    }

    /**
     * Thêm một từ vào lịch sử tìm kiếm History.
     *
     * @param target từ cần thêm
     */
    public void addWordToHistory(String target) {
        historySearch.removeIf(e -> e.equals(target));
        historySearch.add(target);
        refactorHistory();
    }

    /**
     * Xuất lịch sử tìm kiếm ra tệp đã lưu.
     */
    public void exportHistory() {
        String content = buildHistoryContent();
        FileUtil.writeToFile(Config.HISTORY_FILE_PATH, content);
    }

    /**
     * Tạo nội dung của lịch sử tìm kiếm.
     */
    private String buildHistoryContent() {
        StringBuilder content = new StringBuilder();
        for (String target : historySearch) {
            content.append(target).append("\n");
        }
        return content.toString();
    }

    /**
     * Tái cấu trúc lịch sử tìm kiếm để giới hạn số lượng từ.
     */
    private void refactorHistory() {
        if (historySearch.size() > Config.MAX_WORDS_HISTORY) {
            historySearch.subList(0, historySearch.size() - Config.MAX_WORDS_HISTORY).clear();
        }
    }
}
