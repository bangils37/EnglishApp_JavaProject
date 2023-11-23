package dictionary.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    /**
     * Create directory if not exists.
     * 
     * @param directory
     */
    public static void createDirectoryIfNotExists(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Failed to create directory: " + directory.getPath());
        }
    }

    /**
     * Create file if not exists.
     * 
     * @param file
     */
    public static void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Failed to create file: " + file.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create file: " + file.getPath());
            }
        }
    }

    /**
     * Write content to file.
     * 
     * @param filePath path to the file
     * @param content  content to write
     */
    public static void writeToFile(String filePath, String content) {
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath),
                        StandardCharsets.UTF_8))) {
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while writing to file: " + filePath);
        }
    }
}
