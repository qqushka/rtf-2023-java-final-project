package ru.pantyukhin.fp2023;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import ru.pantyukhin.fp2023.dto.Earthquakes;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parse {
    public static void main(String[] args) throws IOException, CsvException {
        Parse parse = new Parse();
        System.out.println(parse.parseCsv("Earthquakes.csv"));
    }

    public List<Earthquakes> parseCsv(String filename) throws IOException, CsvException {
        Path path = Paths.get(filename);
        try {
            List<String[]> lines = readAllLines(path);

            return lines.stream()
                    .map(Earthquakes::new)
                    .collect(Collectors.toList());
        } catch (IOException | CsvException e) {
            System.out.println(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new CsvException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    public List<String[]> readAllLines(Path filePath) throws IOException, CsvException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] headers = csvReader.readNext();
                System.out.println("Skipping headers: " + Arrays.toString(headers));
                return csvReader.readAll();
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
