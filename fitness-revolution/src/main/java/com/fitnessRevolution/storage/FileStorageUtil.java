package com.fitnessRevolution.storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileStorageUtil {

    private static final String DATA_DIR   = "src/main/resources/data/";
    public  static final String MEMBERS    = DATA_DIR + "members.txt";
    public  static final String PAYMENTS   = DATA_DIR + "payments.txt";
    public  static final String INVOICES   = DATA_DIR + "invoices.txt";
    public  static final String REFUNDS    = DATA_DIR + "refunds.txt";
    public  static final String TRAINERS   = DATA_DIR + "trainers.txt";
    public  static final String SESSIONS   = DATA_DIR + "sessions.txt";
    public  static final String ATTENDANCE = DATA_DIR + "attendance.txt";

    public static void appendLine(String path, String line) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.writeString(Paths.get(path), line + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Write error: " + e.getMessage());
        }
    }

    public static List<String> readLines(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return new ArrayList<>();
            return Files.readAllLines(Paths.get(path))
                    .stream().filter(l -> !l.isBlank()).toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void writeLines(String path, List<String> lines) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.write(Paths.get(path), lines);
        } catch (IOException e) {
            System.err.println("Write error: " + e.getMessage());
        }
    }
}