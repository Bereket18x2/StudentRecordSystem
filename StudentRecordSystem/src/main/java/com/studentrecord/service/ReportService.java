package com.studentrecord.service;

import com.studentrecord.model.Student;
import com.studentrecord.util.FilePathConfig;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates a summary report and writes it to data/report.txt
 */
public class ReportService {

    private final StudentService studentService;

    public ReportService(StudentService studentService) {
        this.studentService = studentService;
    }

    public void generateReport() throws IOException {
        String separator = "=".repeat(55);
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Student highest = studentService.getHighestGpa();
        Student lowest  = studentService.getLowestGpa();

        // Build the report content as a string first, then write it.
        // Using StringBuilder avoids multiple small writes to disk.
        StringBuilder sb = new StringBuilder();
        sb.append(separator).append("\n");
        sb.append("     STUDENT RECORD MANAGEMENT - REPORT\n");
        sb.append("     Generated: ").append(timestamp).append("\n");
        sb.append(separator).append("\n\n");

        sb.append(String.format("  Total Students : %d%n", studentService.getTotalCount()));
        sb.append(String.format("  Average GPA    : %.2f%n", studentService.getAverageGpa()));

        sb.append("\n  Highest GPA:\n");
        sb.append(highest != null ? "    " + highest : "    N/A").append("\n");

        sb.append("\n  Lowest GPA:\n");
        sb.append(lowest != null ? "    " + lowest : "    N/A").append("\n");

        sb.append("\n").append(separator).append("\n");
        sb.append("  FULL STUDENT LIST\n").append(separator).append("\n");

        if (studentService.getAllStudents().isEmpty()) {
            sb.append("  (no students on record)\n");
        } else {
            for (Student s : studentService.getAllStudents()) {
                sb.append("  ").append(s).append("\n");
            }
        }
        sb.append(separator).append("\n");

        // Write to file with PrintWriter + BufferedWriter for efficiency
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(
                new FileWriter(FilePathConfig.REPORT_FILE)))) {
            pw.print(sb);
        }

        // Also print to console so the user sees it immediately
        System.out.println("\n" + sb);
        System.out.println("  Report saved to: " + FilePathConfig.REPORT_FILE);
    }
}
