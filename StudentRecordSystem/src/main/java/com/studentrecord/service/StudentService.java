package com.studentrecord.service;

import com.studentrecord.io.BinaryFileIO;
import com.studentrecord.io.SerialFileIO;
import com.studentrecord.io.TextFileIO;
import com.studentrecord.model.Student;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The brain of the application. All CRUD operations live here.
 *
 * Design decision: we keep one authoritative in-memory list (students)
 * and after every mutation we persist to ALL THREE file formats. This
 * keeps them in sync so whichever format a future reader uses, it gets
 * the latest data.
 *
 * In a real system you'd pick just one storage format. We use three here
 * purely to demonstrate the different Java I/O approaches.
 */
public class StudentService {

    private List<Student> students;      // Our working copy in memory
    private final TextFileIO   textIO;
    private final BinaryFileIO binaryIO;
    private final SerialFileIO serialIO;

    public StudentService() throws IOException {
        this.textIO   = new TextFileIO();
        this.binaryIO = new BinaryFileIO();
        this.serialIO = new SerialFileIO();

        // Load from text file on startup (text is our "source of truth" for readability)
        this.students = textIO.loadAll();
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    public void addStudent(Student student) throws IOException {
        // Prevent duplicate IDs
        if (findById(student.getStudentId()).isPresent()) {
            throw new IllegalArgumentException(
                "A student with ID " + student.getStudentId() + " already exists.");
        }

        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0.");
        }

        students.add(student);
        persistAll();
        System.out.println("  ✓ Student added: " + student.getName());
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public Optional<Student> findById(String id) {
        // Stream + filter is clean here. Optional lets callers handle "not found"
        // without us returning null — null references are a classic source of bugs.
        return students.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(id))
                .findFirst();
    }

    public List<Student> getAllStudents() {
        return students;
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateStudent(String id, String newName, String newDept, double newGpa)
            throws IOException {

        Optional<Student> found = findById(id);
        if (found.isEmpty()) return false;

        if (newGpa < 0.0 || newGpa > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0.");
        }

        Student s = found.get();
        s.setName(newName);
        s.setDepartment(newDept);
        s.setGpa(newGpa);

        persistAll();
        System.out.println("  ✓ Student updated: " + id);
        return true;
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteStudent(String id) throws IOException {
        boolean removed = students.removeIf(s -> s.getStudentId().equalsIgnoreCase(id));
        if (removed) {
            persistAll();
            System.out.println("  ✓ Student deleted: " + id);
        }
        return removed;
    }

    // ─── REPORT DATA ───────────────────────────────────────────────────────────

    public int getTotalCount() { return students.size(); }

    public Student getHighestGpa() {
        return students.stream()
                .max(Comparator.comparingDouble(Student::getGpa))
                .orElse(null);
    }

    public Student getLowestGpa() {
        return students.stream()
                .min(Comparator.comparingDouble(Student::getGpa))
                .orElse(null);
    }

    public double getAverageGpa() {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    // ─── INTERNAL ──────────────────────────────────────────────────────────────

    /**
     * Every mutation calls this to keep all three file formats in sync.
     * IOException propagates up to the UI layer (Main) where it's reported to the user.
     */
    private void persistAll() throws IOException {
        textIO.saveAll(students);
        binaryIO.saveAll(students);
        try {
            serialIO.saveAll(students);
        } catch (IOException e) {
            // Serialization failure shouldn't crash the app — log and move on
            System.err.println("  [Warning] Could not write serialized file: " + e.getMessage());
        }
    }
}
