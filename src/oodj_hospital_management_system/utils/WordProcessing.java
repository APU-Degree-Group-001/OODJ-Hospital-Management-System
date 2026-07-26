/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

/**
 *
 * @author sheaw
 */
public class WordProcessing {
    public static String capitalizeEachWord(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split(" "); // Split words by space
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return result.toString().trim();
    }
}
