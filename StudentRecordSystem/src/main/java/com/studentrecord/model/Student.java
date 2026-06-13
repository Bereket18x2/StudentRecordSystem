package com.studentrecord.model;

import java.io.Serializable;


public class Student implements Serializable {

    // This version ID is used during deserialization ( reads those bytes from the .ser file and rebuilds the exact same Student object in memory)  to make sure the

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private String department;
    private double gpa;

    // Default constructor needed for some I/O operations
    public Student() {}

    public Student(String studentId, String name, String department, double gpa) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.gpa = gpa;
    }

    // --- Getters and Setters ---

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String toTextLine() {
        return studentId + "|" + name + "|" + department + "|" + gpa;
    }


    public static Student fromTextLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Corrupted record line: " + line);
        }
        return new Student(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
    }

    @Override
    public String toString() {
        return String.format("ID: %-10s | Name: %-20s | Dept: %-15s | GPA: %.2f",  // the numbers is a box 20 character wide left
                studentId, name, department, gpa);
    }
}
