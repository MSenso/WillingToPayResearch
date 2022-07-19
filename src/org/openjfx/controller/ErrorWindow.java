package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ErrorWindow implements Window {

    @FXML
    private Button okButton;
    @FXML
    private ListView<String> testError;
    @FXML
    private Label errorTitle;

    @FXML
    private BorderPane inputPane;

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @FXML
    void handleOkButtonAction(ActionEvent event) {
        ((Stage) okButton.getScene().getWindow()).close();
    }

    public void setNameError(List<String> errors) {
        if (errors.size() > 1) errorTitle.setText("Допущены ошибки:");
        else errorTitle.setText("Допущена ошибка:");
        testError.setItems(FXCollections.observableList(errors));
    }

    private void setBackgroundImage() {
        try {
            Image image = new Image(new FileInputStream("src/org/openjfx/resources/images/addBackground.png"));
            inputPane.setBackground(new Background(new BackgroundImage(image, null, null, null, null)));
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла mainBackground в папке src/org/openjfx/resources/images"));
        }
    }

    @FXML
    public void initialize() {
        setBackgroundImage();
    }

}