package com.studentrecord.io;
// Handles binary file reading and writing using DataInputStream and DataOutputStream
import com.studentrecord.model.Student;
import com.studentrecord.util.FilePathConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing students in binary format using
 * DataInputStream and DataOutputStream.
 * WHY binary over text?
 * - Smaller file sizes (doubles stored as 8 raw bytes, not "3.75" as 4 chars)
 * - Slightly faster I/O since no String parsing is needed on read
 * - Not human-readable (trade-off: harder to debug manually)
 * We write each field in a fixed order and MUST read them back in the same order.
 * This is the most important rule with DataInputStream/DataOutputStream.
 */
public class BinaryFileIO {

    private final String filePath;

    public BinaryFileIO() {
        this.filePath = FilePathConfig.BINARY_FILE;
    }

    /**
     * Writes all students to the binary file.
     * Order per student: studentId (UTF), name (UTF), department (UTF), gpa (double)
     */
    public void saveAll(List<Student> students) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {

            // Write the count first so on load we know exactly how many records to expect
            dos.writeInt(students.size());

            for (Student s : students) {
                dos.writeUTF(s.getStudentId());
                dos.writeUTF(s.getName());
                dos.writeUTF(s.getDepartment());
                dos.writeDouble(s.getGpa());
            }
        }
    }

    /**
     * Reads all students back from the binary file.
     * Must match the write order exactly — if it doesn't, you'll get garbage data
     * or an EOFException. This is a common source of bugs with binary I/O.
     */
    public List<Student> loadAll() throws IOException {
        List<Student> students = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) return students;

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {

            int count = dis.readInt(); // How many students were saved?
            for (int i = 0; i < count; i++) {
                String id   = dis.readUTF();
                String name = dis.readUTF();
                String dept = dis.readUTF();
                double gpa  = dis.readDouble();
                students.add(new Student(id, name, dept, gpa));
            }
        }
        return students;
    }
}
