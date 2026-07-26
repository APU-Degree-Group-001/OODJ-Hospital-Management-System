/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

/**
 *
 * @author sheaw
 */

import java.io.FileWriter;
import java.io.IOException;

// Import Internal Package
import oodj_hospital_management_system.config.SystemConfiguration;

public class Logger {
    private static final String logFilePath = SystemConfiguration.logFilePath;
    private static final String errorLogFilePath = SystemConfiguration.errorLogFilePath;
    private static final FileHandler logHandler = new FileHandler(logFilePath);
    private static final FileHandler errorLogHandler = new FileHandler(errorLogFilePath);

    public static void log(String activity) {
        String timeStamp = DateTime.formattedLocalDateTime(DateTime.currentDateTime());

        try {
            if (!logHandler.checkFileExistence()) {
                System.out.println(logHandler.createFile());
            }

            try (FileWriter writer = new FileWriter(logFilePath, true)) {
                writer.write(String.format("[%s] %s%n", timeStamp, activity));
                writer.flush();
            }
        } catch (IOException e) {
            errorLog("Error writing to log file -> " + e.getMessage());
        }
    }

    public static void errorLog(String activity) {
        String timeStamp = DateTime.formattedLocalDateTime(DateTime.currentDateTime());

        try {
            if (!errorLogHandler.checkFileExistence()) {
                System.out.println(errorLogHandler.createFile());
            }

            try (FileWriter writer = new FileWriter(errorLogFilePath, true)) {
                writer.write(String.format("[%s] %s%n", timeStamp, activity));
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Error writing to error log file -> " + e.getMessage());
        }
    }

    static void log() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
