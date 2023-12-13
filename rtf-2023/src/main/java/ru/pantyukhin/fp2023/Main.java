package ru.pantyukhin.fp2023;

import com.opencsv.exceptions.CsvException;
import ru.pantyukhin.fp2023.dto.Earthquakes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, CsvException, SQLException {
        Parse parse = new Parse();
        List<Earthquakes> earthquakes = parse.parseCsv("Earthquakes.csv");

        Database db = new Database();
        db.openConnection("is");
        // db.saveEarthquakes(earthquakes); // Заполнить базу данных

        Map<Integer, Integer> earthquakeData = db.analyzeEarthquakeData(); // Информация для построения графика
        EarthquakeChart chart = new EarthquakeChart(earthquakeData); // График
        chart.setVisible(true); // Отображаем график

        // Cредняя магнитуда для штата
        String State = "West Virginia";
        float AverageMagnitudeForState = db.printAverageMagnitudeForState(State);
        System.out.println(State + " -> " + AverageMagnitudeForState);

        // Штат с самым глубоким землятресением по году
        int Year = 2013;
        String EarthquakeState = db.deepestEarthquakeStateIn2013(Year);
        System.out.println(Year + " -> " + EarthquakeState);

        db.close();
    }
}

