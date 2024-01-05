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

    public List<Earthquakes> parseCsv(String filename) throws IOException, CsvException {
        Path path = Paths.get(filename);
        try (Reader reader = Files.newBufferedReader(path);
             CSVReader csvReader = new CSVReader(reader)) {
            String[] headers = csvReader.readNext();
            System.out.println("Skipping headers: " + Arrays.toString(headers));
            return csvReader.readAll().stream()
                    .map(Earthquakes::new)
                    .collect(Collectors.toList());
        } catch (IOException | CsvException e) {
            throw e;
        } catch (Exception e) {
            CsvException newCsvException = new CsvException("Failed to parse CSV file: " + e.getMessage());
            newCsvException.initCause(e);
            throw newCsvException;
        }
    }
}