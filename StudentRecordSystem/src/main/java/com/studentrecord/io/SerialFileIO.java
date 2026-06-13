package com.studentrecord.io;

import com.studentrecord.model.Student;
import com.studentrecord.util.FilePathConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles student persistence using Java Object Serialization.
 *
 * WHY serialization vs. the other two approaches?
 * - We write the entire Student object directly — no need to manually handle
 *   each field. Java figures out the structure from the class definition.
 * - Best when your object has lots of fields and you don't want the verbosity
 *   of DataOutputStream (writeUTF for every field).
 * - The catch: both writer and reader must have the same Student class
 *   (which is why serialVersionUID in Student.java matters).
 *
 * We serialize the whole List<Student> at once — one object write/read.
 * This is the simplest approach and works perfectly for moderate data sizes.
 */
public class SerialFileIO {

    private final String filePath;

    public SerialFileIO() {
        this.filePath = FilePathConfig.SERIAL_FILE;
    }

    /**
     * Serializes the full list to a .ser file in one shot.
     */
    @SuppressWarnings("unchecked")
    public void saveAll(List<Student> students) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(students);
        }
    }

    /**
     * Deserializes the list back from the .ser file.
     * The unchecked cast is unavoidable here since readObject() returns Object.
     * We suppress the warning because we know what type we wrote.
     */
    @SuppressWarnings("unchecked")
    public List<Student> loadAll() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            return (List<Student>) ois.readObject();
        }
    }
}
