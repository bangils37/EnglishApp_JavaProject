package dictionary.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import dictionary.util.FileUtil;

public class History {
    private static final int MAX_WORDS_HISTORY = 30;
    private static final String HISTORY_DIRECTORY_PATH = "dictionary-user-data/";
    private static final String HISTORY_FILE_PATH = HISTORY_DIRECTORY_PATH + "words-search-history.txt";
    private final ArrayList<String> historySearch = new ArrayList<>();

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

    public ArrayList<String> getHistorySearch() {
        return historySearch;
    }

    /**
     * Load search history into `historySearch` ArrayList.
     * 
     * @see #historySearch
     */
    public void loadHistory() {
        File directory = new File(HISTORY_DIRECTORY_PATH);
        FileUtil.createDirectoryIfNotExists(directory);

        File historyFile = new File(HISTORY_FILE_PATH);
        FileUtil.createFileIfNotExists(historyFile);

        loadHistoryFromFile(HISTORY_FILE_PATH);
        refactorHistory();
    }

    /**
     * Load search history from saved file.
     * 
     * @param filePath path to the file
     */
    private void loadHistoryFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historySearch.add(line.strip());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read file: " + filePath);
        }
    }

    /**
     * Add a word to the search History.getInstance().
     * 
     * @param target the word to be added
     */
    public void addWordToHistory(String target) {
        historySearch.removeIf(e -> e.equals(target));
        historySearch.add(target);
        refactorHistory();
    }

    /**
     * Export serach history to saved file.
     */
    public void exportHistory() {
        StringBuilder content = new StringBuilder();

        for (String target : historySearch) {
            content.append(target).append("\n");
        }

        FileUtil.writeToFile(HISTORY_FILE_PATH, content.toString());
    }

    /**
     * Refactor search history to limit the number of words.
     */
    private void refactorHistory() {
        if (historySearch.size() > MAX_WORDS_HISTORY) {
            historySearch.subList(0, historySearch.size() - MAX_WORDS_HISTORY).clear();
        }
    }
}