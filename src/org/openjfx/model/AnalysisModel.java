package org.openjfx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisModel {

    private List<String> participants = new ArrayList<>();

    private static final int rowsPerParticipant = 10;

    public static int getRowsPerParticipant() {
        return rowsPerParticipant;
    }

    private static final int eegCount = 21;

    public static int getEegCount() {
        return eegCount;
    }

    private Map<Integer, String> eegDict = new HashMap<>();

    private double timeRange;

    private double timeDelay = 0.3;

    private String analysisChoice;

    public String getAnalysisChoice() {
        return analysisChoice;
    }

    public void setAnalysisChoice(String analysisChoice) {
        this.analysisChoice = analysisChoice;
    }

    private String wave;

    private String DBPath;

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void setEegDict(Map<Integer, String> eegDict) {
        this.eegDict = eegDict;
    }

    public void setTimeRange(double timeRange) {
        this.timeRange = timeRange;
    }

    public void setTimeDelay(double timeDelay) {
        this.timeDelay = timeDelay;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }

    public void setDBPath(String DBPath) {
        this.DBPath = DBPath;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public Map<Integer, String> getEegDict() {
        return eegDict;
    }

    public double getTimeRange() {
        return timeRange;
    }

    public double getTimeDelay() {
        return timeDelay;
    }

    public String getWave() {
        return wave;
    }

    public String getDBPath() {
        return DBPath;
    }
}
