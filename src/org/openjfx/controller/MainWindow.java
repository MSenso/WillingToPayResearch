package org.openjfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import org.openjfx.model.AnalysisModel;

import java.io.*;
import java.util.*;

public class MainWindow implements Window {

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @FXML
    private ChoiceBox<String> wavesBox;

    @FXML
    private TextField timeRangeField;

    @FXML
    private ChoiceBox<String> participantsBox;

    @FXML
    private TextArea eegField;

    @FXML
    private TextField delayField;

    @FXML
    private Button runButton;

    @FXML
    private BorderPane inputPane;

    private final AnalysisModel model = new AnalysisModel();

    @FXML
    private TextArea pValuesArea;

    @FXML
    private TextArea significantArea;

    @FXML
    private ChoiceBox<String> analysisTypeBox;

    public void setParticipants(List<String> participants) {
        this.model.setParticipants(new ArrayList<>(participants));
        this.participantsBox.getItems().addAll(participants);
        this.participantsBox.getItems().add("Все");
        this.participantsBox.getSelectionModel().select("Все");
        setRunModelButtonAbility(false);
    }

    public AnalysisModel getModel() {
        return this.model;
    }

    private void setWavesBox() {
        this.wavesBox.getItems().addAll(List.of("Alpha", "Beta", "Gamma", "Delta", "Tetha"));
    }

    private void setAnalysisTypeBox() {
        this.analysisTypeBox.getItems().addAll(List.of("Мощность", "Ассиметрия"));
    }

    private boolean isCorrectEegLine(String line) {
        System.out.println(line);
        if (line == null || line.isEmpty() || line.isBlank()) {
            System.out.println("Пустая строка");
            return false;
        }
        var regex = "^[\\d]+:[ ]*[\\d\\w]+$";
        if (!line.matches(regex)) {
            System.out.println("Не соответствует шаблону");
            return false;
        }
        var eegIndex = Integer.parseInt(line.split(":")[0]);
        return eegIndex >= 0 && eegIndex <= 20;
    }

    public void onRunButton() {
        var eegFlag = checkEegField();
        var timeRangeFlag = checkTimeRangeField();
        var timeDelayFlag = checkTimeDelayField();
        if (eegFlag && timeDelayFlag && timeRangeFlag) {
            getWaves();
            this.model.setAnalysisChoice(this.analysisTypeBox.getSelectionModel().getSelectedItem());
            calculateFeaturesSignificance();
        }
    }

    private void getWaves() {
        this.model.setWave(this.wavesBox.getSelectionModel().getSelectedItem().toLowerCase(Locale.ROOT));
    }

    private boolean checkTimeRangeField() {
        var line = this.timeRangeField.getText();
        if (line == null || line.isEmpty() || line.isBlank()) {
            System.out.println("Строка пуста");
            return false;
        }
        if (tryParseToDouble(line)) {
            var number = Double.parseDouble(line);
            if (number > 0 && number < 300) {
                this.model.setTimeRange(number);
                return true;
            } else {
                System.out.println("Вне диапазона");
                return false;
            }
        } else {
            System.out.println("Не число");
            return false;
        }
    }

    private boolean checkTimeDelayField() {
        var line = this.delayField.getText();
        if (line == null || line.isEmpty() || line.isBlank()) {
            System.out.println("Строка пуста");
            return false;
        }
        if (tryParseToDouble(line)) {
            var number = Double.parseDouble(line);
            if (number > 0 && number < 50) {
                this.model.setTimeDelay(number);
                return true;
            } else {
                System.out.println("Вне диапазона");
                return false;
            }
        } else {
            System.out.println("Не число");
            return false;
        }
    }

    private boolean checkEegField() {
        this.model.setEegDict(new HashMap<>());
        var line = this.eegField.getText();
        if (line.isEmpty() || line.isBlank()) {
            System.out.println("Пустой ввод");
            return false;
        } else {
            var lines = line.split("\\r?\\n|\\r");
            var participantsCount = this.participantsBox.getSelectionModel().getSelectedItem().equals("Все")
                    ? this.model.getParticipants().size() : 1;
            var maxLimit = participantsCount < 3 ? participantsCount * AnalysisModel.getRowsPerParticipant() - 3 : AnalysisModel.getEegCount();
            if (lines.length == 0 || lines.length > maxLimit) {
                System.out.println("Длина массива неверна");
                return false;
            } else {
                for (var curLine : lines) {
                    if (isCorrectEegLine(curLine)) {
                        var parts = curLine.split(":[\\s]*");
                        var index = Integer.parseInt(parts[0]);
                        if (this.model.getEegDict().containsKey(index)) {
                            this.model.setEegDict(new HashMap<>());
                            System.out.println("Таклй ключ есть");
                            return false;
                        }
                        this.model.getEegDict().put(index, parts[1]);
                    } else {
                        this.model.setEegDict(new HashMap<>());
                        System.out.println("Некорректная строка");
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public void setRunModelButtonAbility(boolean isDisable) {
        runButton.setDisable(isDisable);
    }

    public void onImportButton() {
        showWindow(600, 150, "/org/openjfx/view/ImportWindow.fxml", "/org/openjfx/resources/images/ImportIcon.png", "Импорт данных");
    }

    private String mapToString(Map<Integer, String> map) {
        var result = "{";
        var entryLines = new ArrayList<String>();
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            entryLines.add(key + ": " + "'" + value + "'");
        }
        result += String.join(", ", entryLines) + "}";
        return result;
    }

    private String listToString(List<String> list) {
        var lines = new ArrayList<String>();
        for (var line : list) {
            lines.add("'" + line + "'");
        }
        return "[" + String.join(", ", lines) + "]";
    }

    private void parseOutput(String output) {
        var lines = output.replace("{", "").replace("}", "")
                .split(", ");
        var formattedLines = new ArrayList<String>();
        for (var line : lines) {
            var parts = line.replace("'", "").split(": ");
            var formattedLine = parts[0] + ": " + parts[1];
            formattedLines.add(formattedLine);
        }
        this.pValuesArea.setText(String.join(System.lineSeparator(), formattedLines));
    }

    private void parseSignificantFeatures(String output) {
        var formattedLine = output.replace("[", "").replace("]", "").replace("'", "");
        if (formattedLine.isEmpty() || formattedLine.isBlank()) {
            this.significantArea.setText("Нет значимых критериев");
        } else {
            var formattedLines = formattedLine.split(", ");
            this.significantArea.setText(String.join(System.lineSeparator(), formattedLines));
        }
    }

    private String mapAnalysisType() {
        var item = this.analysisTypeBox.getSelectionModel().getSelectedItem();
        if (item.equals("Мощность")) return "power";
        else return "asymmetry";
    }

    private void calculateFeaturesSignificance() {
        var participantsString = listToString(model.getParticipants());
        var channelsString = mapToString(model.getEegDict());
        var wavesString = model.getWave();
        var timeRangeString = String.valueOf(model.getTimeRange());
        var timeDelayString = String.valueOf(model.getTimeDelay());
        var DBPath = model.getDBPath().replace("\\", "/");
        var analysisType = mapAnalysisType();

        ProcessBuilder pb = new ProcessBuilder("python3",
                "src\\org\\openjfx\\scripts\\InputToAnalysis.py",
                DBPath, participantsString, channelsString, wavesString, timeRangeString, timeDelayString, analysisType);
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            List<String> outputs = new ArrayList<>();
            String output = "";
            while (output != null) {
                output = stdInput.readLine();
                System.out.println(output);
                outputs.add(output);
            }
            if (outputs.size() == 1) {
                showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                        "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                        List.of("Произошла ошибка при работе с файлами эксперимента. Пожалуйста, убедитесь, " +
                                "что по указанному пути есть папки с данными участников и что EEG-файлы лежат внутри папки CSV_export. Проверьте наличие csv файлов price, " +
                                "WTP и taste и txt файлов first, choco_order и times. Проверьте, не открыты ли файлы внутри папки участника эксперимента"));
            }
            else {
                parseOutput(outputs.get(outputs.size() - 3));
                parseSignificantFeatures(outputs.get(outputs.size() - 2));
            }
        } catch (IOException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла InputToAnalysis.py в папке src/org/openjfx/scripts"));
        }
    }

    private boolean tryParseToDouble(String val) {
        var doubleRegex = "^[\\d]+[.,][\\d]+$";
        var intRegex = "^[\\d]+$";
        return val.matches(doubleRegex) || val.matches(intRegex);
    }

    @FXML
    protected void handleExitButtonAction() {
        Platform.exit();
        System.exit(0);
    }


    @FXML
    void handleFormatMenuItemAction() {
        showWindow(1050, 700, "/org/openjfx/view/FormatWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "Формат ввода");
    }

    @FXML
    void handleAboutMenuItemAction() {
        showWindow(600, 400, "/org/openjfx/view/AboutWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "О программе");
    }

    private void setBackgroundImage() {
        try {
            Image image = new Image(new FileInputStream("src/org/openjfx/resources/images/mainBackground.png"));
            inputPane.setBackground(new Background(new BackgroundImage(image, null, null, null, null)));
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла mainBackground в папке src/org/openjfx/resources/images"));
        }
    }

    @FXML
    public void initialize() {
        runButton.setDisable(true);
        setWavesBox();
        setAnalysisTypeBox();
        setBackgroundImage();
    }
}

