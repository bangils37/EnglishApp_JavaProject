package dictionary.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class.getName());

    /**
     * Tạo thư mục nếu chưa tồn tại.
     *
     * @param directory thư mục
     */
    public static void createDirectoryIfNotExists(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            logger.log(Level.WARNING, "Không thể tạo thư mục: {0}", directory.getPath());
        }
    }

    /**
     * Tạo file nếu chưa tồn tại.
     *
     * @param file file
     */
    public static void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    logger.log(Level.WARNING, "Không thể tạo file: {0}", file.getPath());
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Không thể tạo file: " + file.getPath(), e);
            }
        }
    }

    /**
     * Ghi nội dung vào file.
     *
     * @param filePath đường dẫn đến file
     * @param content  nội dung cần ghi
     */
    public static void writeToFile(String filePath, String content) {
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath),
                        StandardCharsets.UTF_8))) {
            out.write(content);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Có lỗi xảy ra khi ghi vào file: " + filePath, e);
        }
    }

    /**
     * Load các dòng trong file vào List.
     * 
     * @param filePath đường dẫn đến file
     * @return List các dòng trong file
     */
    public static List<String> loadLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Có lỗi xảy ra khi đọc file: " + filePath, e);
        }
        return lines;
    }
}
