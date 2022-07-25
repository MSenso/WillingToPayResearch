package org.openjfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.openjfx.model.AnalysisModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        this.wavesBox.getItems().addAll(Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Tetha"));
    }

    private void setAnalysisTypeBox() {
        this.analysisTypeBox.getItems().addAll(Arrays.asList("Мощность", "Ассиметрия"));
        this.analysisTypeBox.getSelectionModel().select("Мощность");
    }

    private boolean isCorrectEegLine(String line) {
        if (line == null || line.isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("В списке электродов содержится пустая строка"));
            return false;
        }
        String regex = "^[\\d]+:[ ]*[\\d\\w]+$";
        if (!line.matches(regex)) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("Строка электрода не соответствует шаблону \"Номер электрода: название электрода\". " +
                            "Убедитесь, что номер является натуральным числом, а название состоит из букв и следующими за ними цифр"));
            return false;
        }
        int eegIndex = Integer.parseInt(line.split(":")[0]);
        return eegIndex >= 0 && eegIndex <= 20;
    }

    public void onRunButton() {
        boolean eegFlag = checkEegField();
        boolean timeRangeFlag = checkTimeRangeField();
        boolean timeDelayFlag = checkTimeDelayField();
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
        String line = this.timeRangeField.getText();
        if (line == null || line.isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("Строка временного окна пуста"));
            return false;
        }
        if (tryParseToDouble(line)) {
            line = line.replace(",", ".");
            double number = Double.parseDouble(line);
            if (number > 0 && number < 300) {
                this.model.setTimeRange(number);
                return true;
            } else {
                showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                        "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                        Arrays.asList("Значение временного окна должно быть в диапазоне от 0 до 300 невключительно"));
                return false;
            }
        } else {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("Значение временного окна должно быть действительным числом"));
            return false;
        }
    }

    private boolean checkTimeDelayField() {
        String line = this.delayField.getText();
        if (line == null || line.isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("Строка временного интервала пуста"));
            return false;
        }
        if (tryParseToDouble(line)) {
            line = line.replace(",", ".");
            double number = Double.parseDouble(line);
            if (number > 0 && number < 50) {
                this.model.setTimeDelay(number);
                return true;
            } else {
                showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                        "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                        Arrays.asList("Значение временного интервала должно быть в диапазоне от 0 до 50 невключительно"));
                return false;
            }
        } else {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("Значение временного интервала должно быть действительным числом"));
            return false;
        }
    }

    private boolean checkEegField() {
        this.model.setEegDict(new HashMap<>());
        String line = this.eegField.getText();
        if (line.isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    Arrays.asList("В поле электродов содержится пустая строка"));
            return false;
        } else {
            String[] lines = line.split("\\r?\\n|\\r");
            int participantsCount = this.participantsBox.getSelectionModel().getSelectedItem().equals("Все")
                    ? this.model.getParticipants().size() : 1;
            int maxLimit = participantsCount < 3 ? participantsCount * AnalysisModel.getRowsPerParticipant() - 3 : AnalysisModel.getEegCount();
            if (lines.length == 0 || lines.length > maxLimit) {
                showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                        "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                        Arrays.asList("Для возможности вычисления p значений при выбранном количестве участников максимальное количество электродов равно " + maxLimit));
                return false;
            } else {
                for (String curLine : lines) {
                    if (isCorrectEegLine(curLine)) {
                        String[] parts = curLine.split(":[\\s]*");
                        int index = Integer.parseInt(parts[0]);
                        if (this.model.getEegDict().containsKey(index)) {
                            this.model.setEegDict(new HashMap<>());
                            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                                    Arrays.asList("В списке электродов содержатся одинаковые номера"));
                            return false;
                        }
                        this.model.getEegDict().put(index, parts[1]);
                    } else {
                        this.model.setEegDict(new HashMap<>());
                        showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                                "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                                Arrays.asList("Значение номера электрода должно быть в диапазоне от 0 до 20 включительно"));
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
        String result = "{";
        List<String> entryLines = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
            entryLines.add(key + ": " + "'" + value + "'");
        }
        result += String.join(", ", entryLines) + "}";
        return result;
    }

    private String listToString(List<String> list) {
        List<String> lines = new ArrayList<>();
        for (String line : list) {
            lines.add("'" + line + "'");
        }
        return "[" + String.join(", ", lines) + "]";
    }

    private void parseOutput(String output) {
        String[] lines = output.replace("{", "").replace("}", "")
                .split(", ");
        List<String> formattedLines = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.replace("'", "").split(": ");
            String formattedLine = parts[0] + ": " + parts[1];
            formattedLines.add(formattedLine);
        }
        this.pValuesArea.setText(String.join(System.lineSeparator(), formattedLines));
    }

    private void parseSignificantFeatures(String output) {
        String formattedLine = output.replace("[", "").replace("]", "").replace("'", "");
        if (formattedLine.isEmpty()) {
            this.significantArea.setText("Нет значимых критериев");
        } else {
            String[] formattedLines = formattedLine.split(", ");
            this.significantArea.setText(String.join(System.lineSeparator(), formattedLines));
        }
    }

    private String mapAnalysisType() {
        String item = this.analysisTypeBox.getSelectionModel().getSelectedItem();
        if (item.equals("Мощность")) return "power";
        else return "asymmetry";
    }

    private void calculateFeaturesSignificance() {
        String participantsString = listToString(model.getParticipants());
        String channelsString = mapToString(model.getEegDict());
        String wavesString = model.getWave();
        String timeRangeString = String.valueOf(model.getTimeRange());
        String timeDelayString = String.valueOf(model.getTimeDelay());
        String DBPath = model.getDBPath().replace("\\", "/");
        String analysisType = mapAnalysisType();
        ProcessBuilder pb = new ProcessBuilder("python3",
                System.getProperty("user.dir") + "/scripts/InputToAnalysis.py",
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
                        Arrays.asList("Произошла ошибка при работе с файлами эксперимента. Пожалуйста, убедитесь, что в папке с jar-файлом есть scripts-папка, " +
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
                    Arrays.asList("Пожалуйста, проверьте наличие файла InputToAnalysis.py в папке src/org/openjfx/scripts"));
        }
    }

    private boolean tryParseToDouble(String val) {
        String doubleRegex = "^[\\d]+[.,][\\d]+$";
        String intRegex = "^[\\d]+$";
        return val.matches(doubleRegex) || val.matches(intRegex);
    }

    @FXML
    protected void handleExitButtonAction() {
        Platform.exit();
        System.exit(0);
    }


    @FXML
    void handleFormatMenuItemAction() {
        showWindow(800, 600, "/org/openjfx/view/FormatWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "Формат ввода");
    }

    @FXML
    void handleAboutMenuItemAction() {
        showWindow(600, 400, "/org/openjfx/view/AboutWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "О программе");
    }

    @FXML
    public void initialize() {
        runButton.setDisable(true);
        setWavesBox();
        setAnalysisTypeBox();
    }
}

