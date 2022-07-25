package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImportWindow implements Window {

    @FXML
    public TextField pathText;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private List<String> participants = new ArrayList<>();

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @FXML
    void handleCancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private String openFile() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = fileChooser.showDialog(stage);
        if (file != null) return file.getAbsolutePath();
        else return "";
    }

    @FXML
    void handleMouseClickAction() {
        String path = openFile();
        pathText.setText(path);
    }

    public ObservableList<String> importData(String path) {
        ObservableList<String> errors = FXCollections.observableArrayList();
        File directoryPath = new File(path);
        File[] content = directoryPath.listFiles();
        if (Optional.ofNullable(content).isPresent()) {
            this.participants = Arrays.stream(content)
                    .filter(File::isDirectory)
                    .map(File::getName)
                    .collect(Collectors.toList());
        } else errors.add("В директории должна быть папка хотя бы с одним участником");
        return errors;
    }

    @FXML
    void handleOkButtonAction() {
        if (pathText.getText().isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/ErrorIcon.png", "Ошибка", Arrays.asList("Файл не выбран"));
        } else {
            List<String> errors = importData(pathText.getText());
            if (errors.size() == 0) {
                ((MainWindow) this.parentWindow).setParticipants(this.participants);
                ((MainWindow) this.parentWindow).getModel().setDBPath(pathText.getText());
                ((Stage) okButton.getScene().getWindow()).close();
            } else showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/ErrorIcon.png", "Ошибка", errors);
        }
    }
}
