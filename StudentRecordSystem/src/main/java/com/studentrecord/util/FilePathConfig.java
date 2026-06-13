package com.studentrecord.util;
// // Utility class managing all file paths and directory creation
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Central place for all file paths and directory setup.
 * Having it here means if we ever move files around, we only change one class.
 */
public class FilePathConfig {

    public static final String DATA_DIR        = "data";
    public static final String BACKUP_DIR      = "data/backup";

    public static final String TEXT_FILE       = DATA_DIR + "/students.txt";
    public static final String BINARY_FILE     = DATA_DIR + "/students.dat";
    public static final String SERIAL_FILE     = DATA_DIR + "/students.ser";
    public static final String REPORT_FILE     = DATA_DIR + "/report.txt";
    public static final String BACKUP_FILE     = BACKUP_DIR + "/students_backup.txt";

    /**
     * Called once at application startup. Creates data/ and data/backup/ if they don't exist.
     * Using File.mkdirs() handles nested directories in one shot.
     */
    public static void ensureDirectoriesExist() {
        new File(DATA_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
    }

    /**
     * Prints metadata about a file using the java.io.File class.
     * Good demo of what the File class can tell us beyond just path info.
     */
    public static void displayFileProperties(String path) {
        File f = new File(path);
        System.out.println("\n--- File Properties: " + f.getName() + " ---");
        System.out.println("  Absolute Path : " + f.getAbsolutePath());
        System.out.println("  Exists        : " + f.exists());
        System.out.println("  Size          : " + (f.exists() ? f.length() + " bytes" : "N/A"));
        System.out.println("  Last Modified : " + (f.exists()
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(f.lastModified()))
                : "N/A"));
        System.out.println("  Readable      : " + f.canRead());
        System.out.println("  Writable      : " + f.canWrite());
    }
}
