/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.server;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.*;

/**
 *
 * @author Administrator
 */
public class AutoBackup {

    public static void start() {
        String sourceFolderPath = "./src/main/java/";
        // Đường dẫn lưu file backup
        String backupFolderPath = "./backup";
        // Tạo tên file backup với ngày tháng năm
        String dateStr = new SimpleDateFormat("dd_MM_yyyy___HH'h'_mm'm'_ss's'").format(new Date());
        String backupFileName = "backup_" + dateStr + ".zip";
        String backupFilePath = backupFolderPath + File.separator + backupFileName;

        // Tạo folder backup nếu chưa tồn tại
        File backupDir = new File(backupFolderPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // Thực hiện backup code
        try {
            zipFolder(Paths.get(sourceFolderPath), Paths.get(backupFilePath));
            System.out.println("Backup code thành công tại: " + backupFilePath);
        } catch (IOException e) {
            
            System.err.println("Lỗi trong quá trình backup code: " + e.getMessage());
        }

        // Thực hiện backup SQL
        try {
            System.out.println("Đang thực hiện backup SQL...");
            String script = System.getProperty("os.name").toLowerCase().contains("win") ? "backupsql.bat" : "./backupsql.sh";
            ProcessBuilder processBuilder = System.getProperty("os.name").toLowerCase().contains("win")
                    ? new ProcessBuilder("cmd", "/c", script)
                    : new ProcessBuilder("bash", script);
            processBuilder.directory(new File(".")); // Đặt thư mục làm việc là thư mục gốc của project
            processBuilder.redirectErrorStream(true); // Gộp error stream với output stream

            Process process = processBuilder.start();

            // Đọc output từ process
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Chờ process hoàn thành
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Backup SQL thành công!");
            } else {
                System.err.println("Backup SQL thất bại với exit code: " + exitCode);
            }

        } catch (IOException e) {
            
            System.err.println("Lỗi khi chạy backup SQL: " + e.getMessage());
        } catch (InterruptedException e) {
            
            System.err.println("Backup SQL bị gián đoạn: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    public static void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceFolderPath)
                    .filter(path -> !Files.isDirectory(path)) // Lọc các file (bỏ folder)
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            // com.ngocrong.NQMP.// UtilsNQMP.logError(e);
                            System.err.println("Lỗi khi nén file: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }
}
