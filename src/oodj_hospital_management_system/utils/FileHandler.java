/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

/**
 *
 * @author sheaw
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

// Import Internal Package
import oodj_hospital_management_system.config.SystemConfiguration;

public class FileHandler {
    private File file;
    private String filePath;
    private String txtDelimiter = SystemConfiguration.txtDelimiter;

    public static void main(String[] args) {
        FileHandler handler = new FileHandler("txt/users/users.txt");
        System.out.println(handler.checkFileExistence());
        // ArrayList<LinkedHashMap<String, String>> users = handler.readFile();
        // System.out.printf("%-19s %-19s %-19s %-19s%n", "User ID", "Name", "Password",
        // "Role");
        // for (LinkedHashMap<String, String> map : users) {
        //     System.out.printf("%-19s %-19s %-19s %-19s%n", map.get("userId"), map.get("userName"), map.get("userPwd"), map.get("userType"));
        // }
        ArrayList<LinkedHashMap<String, String>> map = new ArrayList<>();
        LinkedHashMap<String, String> n = new LinkedHashMap<>();
        n.put("userId", "#STFd4e8f");
        n.put("userName", "haha");
        n.put("userPwd", "hehe123");
        n.put("userType", "staff");
        map.add(n);
        handler.writeFile(map);
    }

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

    public ArrayList<LinkedHashMap<String, String>> readFile() {
        ArrayList<LinkedHashMap<String, String>> maps = new ArrayList<>();
        if (file.exists()) {
            try (Scanner reader = new Scanner(file)) {
                List<String> header = new ArrayList<>(); // List is not instantiable (cannot create instance), so use
                                                         // ArrayList
                for (int i = 0; reader.hasNextLine(); i++) {
                    if (i == 0) {
                        String line = reader.nextLine();
                        header = Arrays.asList(line.split(", "));
                    } else {
                        String line = reader.nextLine();
                        List<String> row = Arrays.asList(line.split(", "));
                        if (row.size() == header.size()) {
                            LinkedHashMap<String, String> map = new LinkedHashMap<>();
                            for (int index = 0; index < row.size(); index++) {
                                map.put(header.get(index), row.get(index).replace(txtDelimiter, ", "));
                            }
                            maps.add(map);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + filePath);
                e.printStackTrace(); // print error details
            }
        }
        return maps;
    }

    public void writeFile(ArrayList<LinkedHashMap<String, String>> maps) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // prevent writing empty file
            if (maps.isEmpty()) {
                System.out.println("No data to write to file: " + filePath);
                return;
            }
            List<String> header = new ArrayList<>(maps.get(0).keySet());
            writer.write(String.join(", ", header) + "\n");
            writer.flush();
            for (LinkedHashMap<String, String> map : maps) {
                List<String> row = new ArrayList<>(map.values());
                for(int i = 0; i < row.size(); i++) {
                    row.set(i, row.get(i).replace(", ", txtDelimiter).replace(",", txtDelimiter).replace(",  ", txtDelimiter));
                }
                writer.write(String.join(", ", row) + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Error writing data to file: " + filePath);
            e.printStackTrace(); // print error details
        }
    }

    public void appendFile(ArrayList<LinkedHashMap<String, String>> maps) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            if (maps.isEmpty()) {
                System.out.println("No data to append to file: " + filePath);
                return;
            }
            for (LinkedHashMap<String, String> map : maps) {
                List<String> row = new ArrayList<>(map.values());
                for(int i = 0; i < row.size(); i++) {
                    row.set(i, row.get(i).replace(", ", txtDelimiter).replace(",", txtDelimiter).replace(",  ", txtDelimiter));
                }
                writer.write(String.join(", ", row) + "\n");
                writer.flush();
            }
            System.out.println(String.format("Data is appended to file '%s'!", file.getName()));
        } catch (IOException e) {
            System.out.println("Error appending data to file: " + filePath);
            e.printStackTrace(); // print error details
        }
    }
}
