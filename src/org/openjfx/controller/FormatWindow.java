package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.util.List;

public class FormatWindow implements Window {
    @FXML
    private Label matrLabel;
    @FXML
    private BorderPane inputPane;

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
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
        matrLabel.setText(matrLabel.getText() + System.lineSeparator() + "Номер итерации 1:" + System.lineSeparator() +
                "число 1, число 2" + System.lineSeparator() + "число 3, число 4");
        setBackgroundImage();
    }
}
