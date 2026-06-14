package com.studentrecord.io;
// Handles text file reading and writing using Scanner and PrintWriter
import com.studentrecord.model.Student;
import com.studentrecord.util.FilePathConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TextFileIO {

    private final String filePath;

    public TextFileIO() {
        this.filePath = FilePathConfig.TEXT_FILE;
    }

    /**
     * Saves the entire list to the text file, overwriting whatever was there.
     * This is a full-replace strategy — simpler than trying to patch individual lines.
     */
    public void saveAll(List<Student> students) throws IOException {
        // BufferedWriter reduces disk I/O by batching writes — PrintWriter alone
        // would flush after every println() call which is slow on large datasets.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            for (Student s : students) {
                writer.println(s.toTextLine());
            }
        }
        // The try-with-resources block automatically calls writer.close() even if
        // an exception is thrown. This is why we always use it instead of finally {}.
    }

    /**
     * Reads all students back from the text file.
     * Returns an empty list (not null) if the file doesn't exist yet — callers
     * don't need to null-check, they can just iterate safely.
     */
    public List<Student> loadAll() throws IOException {
        List<Student> students = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) return students; // First run — nothing to load yet

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    students.add(Student.fromTextLine(line));
                }
            }
        }
        return students;
    }
}
