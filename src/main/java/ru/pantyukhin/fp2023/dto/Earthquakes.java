package ru.pantyukhin.fp2023.dto;

import lombok.Data;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Data
public class Earthquakes {
    private String id;
    private Integer depth_in_meters;
    private String magnitude_type;
    private Float magnitude;
    private String state;
    private String time;

    public Earthquakes(String[] csvLine) {
        this.id = csvLine[0];
        this.depth_in_meters = Integer.parseInt(csvLine[1]);
        this.magnitude_type = csvLine[2];
        this.magnitude = Float.parseFloat(csvLine[3]);
        this.state = csvLine[4];

        String dateTimeString = csvLine[5];
        DateTimeFormatter formatter;

        if (dateTimeString.contains("T")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        } else if (dateTimeString.split(" ")[1].split(":")[0].length() == 1) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        this.time = dateTime.format(formatter);
    }
}
