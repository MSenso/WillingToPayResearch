package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
public interface Window {

    void setParentWindow(Window window);
    default void showStage(int width, int height, String iconPath, String title, Parent root) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root, width, height));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        try {
            stage.getIcons().add(new Image(getClass().getResource(iconPath).toURI().toString()));
        }
        catch (NullPointerException e)
        {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        stage.setTitle(title);
        stage.show();
    }


    @FXML
    default void showWindow(int width, int height, String resourcePath, String iconPath, String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при загрузке окна");
        }
        Window window = loader.getController();
        window.setParentWindow(this);
        showStage(width, height, iconPath, title, root);
    }

    @FXML
    default void showWindow(int width, int height, String resourcePath, String iconPath, String title,
                            List<String> errors) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при загрузке окна");
        }
        ErrorWindow window = loader.getController();
        window.setNameError(errors);
        showStage(width, height, iconPath, title, root);
    }
}
