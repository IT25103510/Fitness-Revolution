package com.fitnessRevolution.storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileStorageUtil {

    // Not relative for project root
    // Absolute path will be find during runtime
    private static final String DATA_DIR;

    static {
        try {
            // Find project root from fileStorageUtil.class file's location
            // target/classes - project root - src/main/resources/data/
            Path classPath = Paths.get(
                    FileStorageUtil.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI()
            );
            Path dataPath = classPath.getParent().getParent()
                    .resolve("src/main/resources/data/");
            DATA_DIR = dataPath.toString() + File.separator;
        } catch (Exception e) {
            throw new RuntimeException("DATA_DIR resolve කරන්න බැරි වුණා: " + e.getMessage(), e);
        }
    }

    public  static final String MEMBERS          = DATA_DIR + "members.txt";
    public  static final String PAYMENTS         = DATA_DIR + "payments.txt";
    public  static final String INVOICES         = DATA_DIR + "invoices.txt";
    public  static final String REFUNDS          = DATA_DIR + "refunds.txt";
    public  static final String TRAINERS         = DATA_DIR + "trainers.txt";
    public  static final String SESSIONS         = DATA_DIR + "sessions.txt";
    public  static final String ATTENDANCE       = DATA_DIR + "attendance.txt";
    public  static final String MEMBERSHIP_TYPES = DATA_DIR + "membership_types.txt";

    public static void appendLine(String path, String line) {
        try {
            Files.createDirectories(Paths.get(path).getParent());
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
            Files.createDirectories(Paths.get(path).getParent());
            Files.write(Paths.get(path), lines);
        } catch (IOException e) {
            System.err.println("Write error: " + e.getMessage());
        }
    }
}
