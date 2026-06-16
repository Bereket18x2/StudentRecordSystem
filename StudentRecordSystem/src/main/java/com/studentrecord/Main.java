package com.studentrecord;
// Entry point for the Student Record Management System
import com.studentrecord.io.BackupService;
import com.studentrecord.model.Student;
import com.studentrecord.service.ReportService;
import com.studentrecord.service.StudentService;
import com.studentrecord.util.FilePathConfig;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Entry point. Handles the console menu and delegates all real work to the service layer.
 *
 * The separation here is intentional:
 * - Main knows about the user interface (menus, prompts, printing)
 * - StudentService knows about business logic (CRUD, validation)
 * - The IO classes know about file formats
 * - Nobody mixes these concerns.
 */
public class Main {

    private static StudentService  studentService;
    private static ReportService   reportService;
    private static BackupService   backupService;
    private static Scanner         scanner;

    public static void main(String[] args) {
        // Step 1: Make sure required directories and files exist before we do anything
        FilePathConfig.ensureDirectoriesExist();

        scanner = new Scanner(System.in);

        try {
            studentService = new StudentService();
            reportService  = new ReportService(studentService);
            backupService  = new BackupService();
        } catch (IOException e) {
            System.err.println("Failed to load student data: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   Student Record Management System       ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addStudent();
                    case "2" -> searchStudent();
                    case "3" -> updateStudent();
                    case "4" -> deleteStudent();
                    case "5" -> displayAll();
                    case "6" -> reportService.generateReport();
                    case "7" -> showFileProperties();
                    case "8" -> backupService.backupTextFile();
                    case "0" -> {
                        System.out.println("\nGoodbye!\n");
                        running = false;
                    }
                    default  -> System.out.println("  Invalid choice. Try again.");
                }
            } catch (IllegalArgumentException e) {
                // Validation errors — user-facing, friendly message
                System.out.println("  [Validation Error] " + e.getMessage());
            } catch (IOException e) {
                // File I/O problems — shouldn't happen in normal use but must be handled
                System.out.println("  [File Error] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("  [Unexpected Error] " + e.getMessage());
            }
        }

        scanner.close();
    }

    // ─── MENU HANDLERS ─────────────────────────────────────────────────────────

    private static void addStudent() throws IOException {
        System.out.println("\n-- Add New Student --");
        System.out.print("  Student ID   : "); String id   = scanner.nextLine().trim();
        System.out.print("  Name         : "); String name = scanner.nextLine().trim();
        System.out.print("  Department   : "); String dept = scanner.nextLine().trim();
        System.out.print("  GPA (0-4)    : ");
        double gpa = Double.parseDouble(scanner.nextLine().trim());

        studentService.addStudent(new Student(id, name, dept, gpa));
    }

    private static void searchStudent() {
        System.out.println("\n-- Search Student by ID --");
        System.out.print("  Enter Student ID: ");
        String id = scanner.nextLine().trim();

        Optional<Student> found = studentService.findById(id);
        if (found.isPresent()) {
            System.out.println("  Found: " + found.get());
        } else {
            System.out.println("  No student found with ID: " + id);
        }
    }

    private static void updateStudent() throws IOException {
        System.out.println("\n-- Update Student --");
        System.out.print("  Enter Student ID to update: ");
        String id = scanner.nextLine().trim();

        Optional<Student> found = studentService.findById(id);
        if (found.isEmpty()) {
            System.out.println("  No student found with ID: " + id);
            return;
        }

        Student current = found.get();
        System.out.println("  Current record: " + current);
        System.out.print("  New Name       [" + current.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = current.getName();

        System.out.print("  New Department [" + current.getDepartment() + "]: ");
        String dept = scanner.nextLine().trim();
        if (dept.isEmpty()) dept = current.getDepartment();

        System.out.print("  New GPA        [" + current.getGpa() + "]: ");
        String gpaStr = scanner.nextLine().trim();
        double gpa = gpaStr.isEmpty() ? current.getGpa() : Double.parseDouble(gpaStr);

        studentService.updateStudent(id, name, dept, gpa);
    }

    private static void deleteStudent() throws IOException {
        System.out.println("\n-- Delete Student --");
        System.out.print("  Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();

        System.out.print("  Confirm delete '" + id + "'? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("  Cancelled.");
            return;
        }

        boolean deleted = studentService.deleteStudent(id);
        if (!deleted) System.out.println("  No student found with ID: " + id);
    }

    private static void displayAll() {
        System.out.println("\n-- All Students --");
        List<Student> all = studentService.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("  No students on record.");
        } else {
            all.forEach(s -> System.out.println("  " + s));
            System.out.println("  Total: " + all.size());
        }
    }

    private static void showFileProperties() {
        System.out.println("\n-- File Properties --");
        FilePathConfig.displayFileProperties(FilePathConfig.TEXT_FILE);
        FilePathConfig.displayFileProperties(FilePathConfig.BINARY_FILE);
        FilePathConfig.displayFileProperties(FilePathConfig.SERIAL_FILE);
        FilePathConfig.displayFileProperties(FilePathConfig.BACKUP_FILE);
    }

    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│  1. Add Student             │");
        System.out.println("│  2. Search by ID            │");
        System.out.println("│  3. Update Student          │");
        System.out.println("│  4. Delete Student          │");
        System.out.println("│  5. Display All Students    │");
        System.out.println("│  6. Generate Report         │");
        System.out.println("│  7. Show File Properties    │");
        System.out.println("│  8. Backup Records          │");
        System.out.println("│  0. Exit                    │");
        System.out.println("└─────────────────────────────┘");
        System.out.print("  Choice: ");
    }
}
