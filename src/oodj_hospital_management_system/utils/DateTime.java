/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

/**
 *
 * @author sheaw
 * Description: Format Date and Time
 */

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private final static DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public static LocalDateTime currentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime;
    }

    public static String formattedLocalDateTime(LocalDateTime dateTime) {
        String formattedDateTime = dateTime.format(dateTimeFormat);
        return formattedDateTime;
    }

    public static LocalDateTime toDateTimeObject(String datetime) {
        LocalDateTime localDateTime = LocalDateTime.parse(datetime, dateTimeFormat);
        return localDateTime;
    }

    public static LocalDate getDate(LocalDateTime datetime) {
        return datetime.toLocalDate();
    }

    public static LocalTime getTime(LocalDateTime datetime) {
        return datetime.toLocalTime();
    }

    public static String toDateStringWords(LocalDate date) {
        return date.format(dateFormatter1);
    }

    public static String toDateString(LocalDate date) {
        return date.format(dateFormatter2);
    }

    public static String toTimeString(LocalTime time) {
        return time.format(timeFormatter);
    }
    
    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static LocalTime currentTime() {
        return LocalTime.now();
    }
    
    public static boolean isBefore(LocalDateTime first, LocalDateTime second) {
        return first.isBefore(second);
    }

    public static boolean isAfter(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second);
    }
    
    public static long calculateDurationInHours(
            LocalDateTime start,
            LocalDateTime end
    ) {
        return Duration.between(start, end).toHours();
    }
    
    public static long calculateDurationInMinutes(
            LocalDateTime start,
            LocalDateTime end
    ) {
        return Duration.between(start, end).toMinutes();
    }
    
    public static String formatHourMinuteDuration(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        return String.format("%dh %02dm", hours, minutes);
    }
}
