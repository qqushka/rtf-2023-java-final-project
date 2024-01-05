package ru.pantyukhin.fp2023.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class Earthquakes {
    private String id;
    private Integer depth_in_meters;
    private String magnitude_type;
    private Float magnitude;
    private String state;
    private LocalDateTime time;

    public Earthquakes(String[] csvLine) {
        this.id = csvLine[0];
        this.depth_in_meters = Integer.parseInt(csvLine[1]);
        this.magnitude_type = csvLine[2];
        this.magnitude = Float.parseFloat(csvLine[3]);
        this.state = csvLine[4];
        this.time = parseDateTime(csvLine[5]);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        // Определяем форматы даты
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        };

        // Пытаемся распарсить дату с помощью каждого форматтера
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            } catch (DateTimeParseException ignored) { }
        }
        throw new DateTimeParseException("Could not parse date: " + dateTimeString, dateTimeString, 0);
    }
}