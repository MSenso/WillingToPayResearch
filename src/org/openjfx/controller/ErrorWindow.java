package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class ErrorWindow implements Window {

    @FXML
    private Button okButton;
    @FXML
    private ListView<String> testError;
    @FXML
    private Label errorTitle;

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
}