package org.openjfx.run;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException, URISyntaxException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/org/openjfx/view/MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Анализ эксперимента с шоколадом");
        try {
            primaryStage.getIcons().add(new
                    Image(getClass().getResource("/org/openjfx/resources/images/MainIcon.png").toURI(
            ).toString()));
        } catch (NullPointerException e) {
            System.out.println("Ошибка при попытке запустить приложение. Проверьте наличие файла MainIcon.png в папке src");
        }
        primaryStage.show();
        loader.getController();
    }
}