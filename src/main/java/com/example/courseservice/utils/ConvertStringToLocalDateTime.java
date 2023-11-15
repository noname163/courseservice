package com.example.courseservice.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class ConvertStringToLocalDateTime {
    public LocalTime convertStringToLocalTime(String format, String time) {
        if (time == null || time.equals("")) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalTime.parse(time, formatter);
    }

    public LocalDateTime convertStringToLocalDateTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String hour = date.substring(8, 10);
        String minues = date.substring(10, 12);
        String second = date.substring(12, 14);
        return LocalDateTime.of(
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(day),
                Integer.parseInt(hour),
                Integer.parseInt(minues),
                Integer.parseInt(second));

    }
    public LocalDate convertStringToLocalDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return LocalDate.of(
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(day));

    }
}
