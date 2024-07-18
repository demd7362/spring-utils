package com.example.common.utils;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    public static String currentTimeAsString() {
        return timeToString(LocalTime.now());
    }

    public static String timeToString(LocalTime time) {
        return TIME_FORMATTER.format(time);
    }


    public static String currentDateAsString() {
        return dateToString(LocalDate.now());
    }

    public static String currentDateTimeAsString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate currentDate = localDateTime.toLocalDate();
        LocalTime currentTime = localDateTime.toLocalTime();
        return dateToString(currentDate) + timeToString(currentTime);
    }

    public static String dateToString(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }
    public static boolean isPast(String date){
        LocalDate parsedDate = toLocalDate(date);
        return parsedDate.isBefore(LocalDate.now());
    }
    public static boolean isPast(LocalDate date){
        return date.isBefore(LocalDate.now());
    }
    public static boolean isFuture(String date){
        LocalDate parsedDate = toLocalDate(date);
        return parsedDate.isAfter(LocalDate.now());
    }
    public static boolean isFuture(LocalDate date){
        return date.isAfter(LocalDate.now());
    }
    public static LocalDate toLocalDate(String date){
        return LocalDate.parse(date, DATE_FORMATTER);
    }
    public static LocalTime toLocalTime(String time){
        return LocalTime.parse(time, TIME_FORMATTER);
    }


}
