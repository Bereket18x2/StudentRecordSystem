# Student Record Management System

A console-based Java application that manages student records using three different File I/O strategies — built as a Home Test for OOP (Java File I/O).

---

## What This System Does

You can add, search, update, and delete student records through a simple numbered menu. Every change is immediately saved to disk in three formats at once. There's also a report generator, a file properties viewer, and a backup feature.

---

## Project Structure

```
StudentRecordSystem/
├── src/main/java/com/studentrecord/
│   ├── Main.java                        ← Entry point and console menu
│   ├── model/
│   │   └── Student.java                 ← The Student data class
│   ├── service/
│   │   ├── StudentService.java          ← All CRUD logic lives here
│   │   └── ReportService.java           ← Report generation
│   ├── io/
│   │   ├── TextFileIO.java              ← Scanner + PrintWriter (text)
│   │   ├── BinaryFileIO.java            ← DataInputStream/OutputStream
│   │   ├── SerialFileIO.java            ← ObjectInputStream/OutputStream
│   │   └── BackupService.java           ← Buffered byte-level copy
│   └── util/
│       └── FilePathConfig.java          ← File paths + File class demo
└── data/
    ├── students.txt                     ← Human-readable records
    ├── students.dat                     ← Binary records
    ├── students.ser                     ← Serialized object records
    ├── report.txt                       ← Generated on demand
    └── backup/
        └── students_backup.txt          ← Created via Buffered Streams
```

---

## System Design

The project is split into three layers that each mind their own business:

**UI Layer (`Main.java`)** — handles menus, reads user input, prints results. Knows nothing about files.

**Service Layer (`StudentService`, `ReportService`)** — all business logic. Validates data, performs CRUD, calculates report stats. Knows nothing about how data is stored on disk.

**I/O Layer (`io/` package)** — one class per file format. Each class only knows how to read/write its own format.

This separation means if you wanted to swap text files for a database later, you'd only touch the I/O layer.

### Why Three File Formats?

The assignment asks us to demonstrate each approach:

| Format | Classes Used | Readable? | Best For |
|--------|-------------|-----------|----------|
| Text (`.txt`) | `Scanner`, `PrintWriter` | ✅ Yes | Debugging, portability |
| Binary (`.dat`) | `DataInputStream/OutputStream` | ❌ No | Compact, fast, primitive types |
| Serialized (`.ser`) | `ObjectInputStream/OutputStream` | ❌ No | Writing whole objects without field-by-field code |

In a real system you'd pick just one. Here, all three stay in sync after every change.

---

## How to Run

### Option 1 — IntelliJ IDEA (Recommended)

See the **"Clone & Run in IntelliJ"** section below.

### Option 2 — Command Line

```bash
# Compile (from project root)
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Run
java -cp out com.studentrecord.Main
```

---

## Clone & Run in IntelliJ IDEA

### Step 1 — Clone the Repository

1. Open IntelliJ IDEA
2. On the Welcome screen, click **"Get from VCS"**
   *(or go to **File → New → Project from Version Control**)*
3. Paste your GitHub repository URL
4. Choose where to save it on your computer
5. Click **Clone**

IntelliJ will clone the repo and open it automatically.

### Step 2 — Mark the Source Root

1. In the Project panel on the left, right-click the `src/main/java` folder
2. Select **Mark Directory as → Sources Root**
   *(It should turn blue)*

### Step 3 — Set the Run Configuration

1. Click **Run → Edit Configurations** (top menu)
2. Click the **+** button → choose **Application**
3. Set:
   - **Name:** `StudentRecordSystem`
   - **Main class:** `com.studentrecord.Main`
4. Click **OK**

### Step 4 — Run

Press the green **▶ Run** button or hit `Shift + F10`.

The application starts in the terminal panel at the bottom. The `data/` folder is created automatically on first run.

---

## Connecting to GitHub

### First Time (Pushing a New Project)

```bash
# Inside your project folder
git init
git add .
git commit -m "Initial commit: Student Record Management System"

# On GitHub: create a new empty repo, then:
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

### From IntelliJ (after cloning)

IntelliJ handles Git automatically once you've cloned. Use:
- `Ctrl + K` (or `Cmd + K` on Mac) → **Commit**
- `Ctrl + Shift + K` → **Push**

Or use the **Git** menu at the top.

---

## Sample Data

The `data/students.txt` file comes pre-loaded with 8 students so you can explore the system without adding records manually. Just run the app and try options 5 (Display All) or 6 (Generate Report) straight away.

---

## Exception Handling

Every file operation that can fail declares or catches `IOException`. The menu loop catches exceptions at the top level and shows a friendly message instead of crashing. Validation errors (duplicate IDs, GPA out of range) throw `IllegalArgumentException` which is also caught and shown to the user.
