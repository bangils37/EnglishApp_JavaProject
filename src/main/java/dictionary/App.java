package dictionary;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import dictionary.core.Dictionary;
import dictionary.core.History;
import dictionary.core.SQLDictionary;
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

    public static void main(String[] args) {
        History.getInstance().loadHistory();
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        initializePrimaryStage(primaryStage);
        selectDictionaryType();
    }

    private void initializePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle("Dictionary");
        primaryStage.setResizable(false);
        try {
            loadFXML(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnCloseRequest(primaryStage);

        primaryStage.show();
    }

    private void loadFXML(Stage primaryStage) throws Exception {
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
    }

    private void setOnCloseRequest(Stage primaryStage) {
        primaryStage.setOnCloseRequest(
                arg0 -> {
                    dictionary.close();
                    History.getInstance().exportHistory();
                    Platform.exit();
                    System.exit(0);
                });
    }

    private void selectDictionaryType() {
        Alert alert = createDictionaryTypeAlert();
        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent()) {
            handleDictionaryOption(option.get());
        }
    }

    private Alert createDictionaryTypeAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Chọn loại từ điển sử dụng");
        alert.setHeaderText(
                "Bạn có muốn kết nối cơ sở dữ liệu MYSQL vào từ điển hay không?\n"
                        + "(Yêu cầu đã set up cơ sở dữ liệu MYSQL sẵn sàng)");
        return alert;
    }

    private void handleDictionaryOption(ButtonType selectedOption) {
        if (selectedOption == ButtonType.OK) {
            initializeSQLDictionary();
        }
    }

    private void initializeSQLDictionary() {
        dictionary = new SQLDictionary();
        try {
            dictionary.initialize();
            showSuccessAlert("Thành công kết nối với cơ sở dữ liệu MYSQL.");
        } catch (SQLException e) {
            handleSQLException();
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.show();
    }

    private void handleSQLException() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(
                "Không kết nối được vào cơ sở dữ liệu MYSQL!\nHãy đảm bảo đã set up cơ sở dữ liệu đúng cách.");
        alert.show();
    }
}
