package ru.pantyukhin.fp2023;

import com.opencsv.exceptions.CsvException;
import ru.pantyukhin.fp2023.dto.Earthquakes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String filename = "Earthquakes.csv";
        if (args.length > 0) {
            filename = args[0];
        }
        try {
            List<Earthquakes> earthquakes = new Parse().parseCsv(filename);
            processEarthquakes(earthquakes);
        } catch (IOException | CsvException | SQLException e) {
            e.printStackTrace();
        }
    }
    private static void processEarthquakes(List<Earthquakes> earthquakes) throws SQLException {
        Database db = new Database();
        try {
            db.openConnection("is");
            fillDatabaseWithEarthquakes(db, earthquakes);
            generateAndDisplayEarthquakeChart(db);
            printAverageMagnitudeForState(db, "West Virginia");
            printDeepestEarthquakeStateByYear(db, 2013);
        } finally {
            db.close();
        }
    }
    private static void fillDatabaseWithEarthquakes(Database db, List<Earthquakes> earthquakes) throws SQLException {
        db.saveEarthquakes(earthquakes);
    }
    private static void generateAndDisplayEarthquakeChart(Database db) throws SQLException {
        Map<Integer, Integer> earthquakeData = db.analyzeEarthquakeData();
        EarthquakeChart chart = new EarthquakeChart(earthquakeData);
        chart.setVisible(true);
    }
    private static void printAverageMagnitudeForState(Database db, String state) throws SQLException {
        float averageMagnitude = db.printAverageMagnitudeForState(state);
        System.out.println(state + " -> " + averageMagnitude);
    }
    private static void printDeepestEarthquakeStateByYear(Database db, int year) throws SQLException {
        String earthquakeState = db.findDeepestEarthquakeStateByYear(year);
        System.out.println(year + " -> " + earthquakeState);
    }
}