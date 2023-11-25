package dictionary;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import dictionary.core.DatabaseDictionary;
import dictionary.core.Dictionary;
import dictionary.core.History;
import dictionary.core.LocalDictionary;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class App extends Application {
    public static Dictionary dictionary;

    /**
     * Load the MYSQL Database and History words data.
     *
     * @param args cmd arguments
     */
    public static void main(String[] args) {
        History.getInstance().loadHistory();
        launch();
    }

    /** Start Application. */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dictionary");
        primaryStage.setResizable(false);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource("fxml/Application.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass().getResource("/css/Application-light.css"))
                                    .toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(
                    arg0 -> {
                        dictionary.close();
                        History.getInstance().exportHistory();
                        Platform.exit();
                        System.exit(0);
                    });
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectDictionaryType();
    }

    /**
     * Open (popup) a confirmation box to choose whether to use MYSQL database with
     * the dictionary
     * or not. Then initialize the dictionary type.
     */
    private void selectDictionaryType() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Chọn loại từ điển sử dụng");
        alert.setHeaderText(
                "Bạn có muốn kết nối cơ sở dữ liệu MYSQL vào từ điển hay không?\n"
                        + "(Yêu cầu đã set up cơ sở dữ liệu MYSQL sẵn sàng)");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                dictionary = new DatabaseDictionary();
                try {
                    dictionary.initialize();
                    Alert alert1 = new Alert(AlertType.INFORMATION);
                    alert1.setTitle("Thông báo");
                    alert1.setContentText("Thành công kết nối với cơ sở dữ liệu MYSQL.");
                    alert1.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert1 = new Alert(AlertType.ERROR);
                    alert1.setTitle("Lỗi");
                    alert1.setContentText(
                            "Không kết nối được vào cơ sở dữ liệu MYSQL!\nHãy đảm bảo đã set up cơ sở dữ liệu đúng cách.");
                    alert1.show();
                    dictionary = new LocalDictionary();
                }
            } else if (option.get() == ButtonType.CANCEL) {
                dictionary = new LocalDictionary();
            }
        }
    }
}
