package com.studentrecord.io;
// Handles object serialization using ObjectInputStream and ObjectOutputStream
import com.studentrecord.model.Student;
import com.studentrecord.util.FilePathConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


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
