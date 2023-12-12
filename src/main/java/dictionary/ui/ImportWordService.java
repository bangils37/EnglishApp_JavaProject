package dictionary.ui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ImportWordService extends Service<Void> {
    public String file;

    public ImportWordService(String file) {
        this.file = file;
    }

    /**
     * Tạo và trả về nhiệm vụ để lấy dữ liệu. Lưu ý rằng phương thức này được gọi trên
     * luồng nền (tất cả các mã khác trong ứng dụng này đều chạy trên Luồng Ứng dụng JavaFX!).
     */
    @Override
    protected Task<Void> createTask() {
        return new ImportWordTask(file);
    }
}
