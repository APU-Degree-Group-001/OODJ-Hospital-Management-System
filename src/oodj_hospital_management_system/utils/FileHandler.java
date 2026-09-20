/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

/**
 *
 * @author sheaw
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Import Internal Package
import oodj_hospital_management_system.config.SystemConfiguration;

public class FileHandler {
    private File file;
    private String filePath;
    private String txtDelimiter = SystemConfiguration.txtDelimiter;

    public FileHandler(String filePath) {
        this.file = new File(filePath);
        this.filePath = filePath;
        // System.out.println("\n\nCurrent Working Directory: " +
        // System.getProperty("user.dir"));
        // System.out.println(file.getAbsolutePath());
        // if (!checkFileExistence()) {
        // try {
        // if (file.createNewFile()) {
        // System.out.println("File '" + file.getName() + "' is created!");
        // }
        // } catch (IOException e) {
        // System.out.println("Error creating file: " + filePath);
        // e.printStackTrace(); // print error details
        // }
        // }
    }

    public boolean checkFileExistence() {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean createFile() {
        try {
            if (file.createNewFile()) {
                System.out.println("File '" + file.getName() + "' is created!");
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + filePath);
            e.printStackTrace(); // print error details
        }
        return false;
    }

    public boolean deleteFile() {
        try {
            if (checkFileExistence()) {
                if (file.delete()) {
                    System.out.println("File '" + filePath + "' deleted successfully");
                    return true;
                } else {
                    System.out.println("File '" + filePath + "' failed to be deleted");
                }
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("Error deleting file: " + filePath);
            e.printStackTrace(); // print error details
        }
        return false;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * Escapes characters that would otherwise corrupt the row-based txt
     * format: commas (column separator) and newlines (line separator).
     * Must be reversed exactly by the corresponding unescape step in
     * readFile().
     */
    private String escapeValue(String value) {
        if (value == null) return "";
        return value
                .replace("\r\n", "\\n")
                .replace("\n", "\\n")
                .replace("\r", "\\n")
                .replace(", ", txtDelimiter)
                .replace(",", txtDelimiter)
                .replace(",  ", txtDelimiter);
    }

    /**
     * Reverses escapeValue(): restores real commas and real newlines from
     * their escaped placeholders.
     */
    private String unescapeValue(String value) {
        if (value == null) return "";
        return value
                .replace(txtDelimiter, ", ")
                .replace("\\n", "\n");
    }

    public ArrayList<LinkedHashMap<String, String>> readFile() {
        ArrayList<LinkedHashMap<String, String>> maps = new ArrayList<>();
        if (!file.exists()) {
            return maps;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return maps;
            }
            String[] header = headerLine.split(",", -1);
            for (int i = 0; i < header.length; i++) {
                header[i] = header[i].trim();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",", -1);
                if (row.length == header.length) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    for (int i = 0; i < row.length; i++) {
                        map.put(header[i], unescapeValue(row[i].trim()));
                    }
                    maps.add(map);
                } else {
                    System.out.println("Skipped malformed row in '" + filePath
                            + "' (expected " + header.length + " columns, got "
                            + row.length + "): " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
        return maps;
    }

    public void writeFile(ArrayList<LinkedHashMap<String, String>> maps) {
        if (maps.isEmpty()) {
            System.out.println("No data to write to file: " + filePath);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(String.join(", ", maps.get(0).keySet()) + "\n");
            for (LinkedHashMap<String, String> map : maps) {
                writeRow(writer, map);
            }
        } catch (IOException e) {
            System.out.println("Error writing data to file: " + filePath);
            e.printStackTrace();
        }
    }

    public void appendFile(ArrayList<LinkedHashMap<String, String>> maps) {
        if (maps.isEmpty()) {
            System.out.println("No data to append to file: " + filePath);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (LinkedHashMap<String, String> map : maps) {
                writeRow(writer, map);
            }
            System.out.println(String.format("Data is appended to file '%s'!", file.getName()));
        } catch (IOException e) {
            System.out.println("Error appending data to file: " + filePath);
            e.printStackTrace();
        }
    }

    private void writeRow(BufferedWriter writer, LinkedHashMap<String, String> map) throws IOException {
        boolean first = true;
        for (String value : map.values()) {
            if (!first) writer.write(", ");
            writer.write(escapeValue(value));
            first = false;
        }
        writer.write("\n");
    }
    
    public String fingerprint() {
        if (!file.exists()) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null; // null never equals a stored fingerprint, so we reload
        }
    }
}