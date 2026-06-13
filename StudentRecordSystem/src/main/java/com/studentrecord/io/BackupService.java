package com.studentrecord.io;

import com.studentrecord.util.FilePathConfig;

import java.io.*;

/**
 * Creates a backup of the text student file using Buffered Streams.
 *
 * The requirement specifically asks for BufferedInputStream/BufferedOutputStream
 * for the backup. Here's why buffering matters:
 *
 * Without buffering, every read/write call hits the OS (and potentially the disk).
 * With buffering, Java batches reads into chunks (default 8KB) so the disk is
 * only touched every 8KB instead of every single byte. Much faster.
 *
 * For a backup specifically, we're doing a raw byte-level copy — we don't care
 * what the content is, just copy it faithfully.
 */
public class BackupService {

    public void backupTextFile() throws IOException {
        File source = new File(FilePathConfig.TEXT_FILE);

        if (!source.exists()) {
            System.out.println("  [Backup] No text file found to back up yet.");
            return;
        }

        try (
            BufferedInputStream  in  = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FilePathConfig.BACKUP_FILE))
        ) {
            byte[] buffer = new byte[8192]; // 8KB chunks
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            // out.flush() is called automatically by close() in try-with-resources,
            // but being explicit here shows we understand buffered streams can hold
            // unwritten bytes in memory until flushed.
        }

        System.out.println("  [Backup] Backup created at: " + FilePathConfig.BACKUP_FILE);
    }
}
