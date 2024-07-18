package com.example.common.utils;


import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@UtilityClass
public class FileUtils {
    public File toFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    public String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);
        }
        throw new IllegalArgumentException("Cannot find dot from filename");
    }
    public String extractFileName(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        throw new IllegalArgumentException("Cannot find dot from filename");
    }

    public Path uploadFile(MultipartFile multipartFile, String uploadDir) throws IOException {
        if (multipartFile == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }
//        String[] dirs = uploadDir.split("/");
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = multipartFile.getOriginalFilename();
        String newFileName = extractFileName(fileName) + "_" + UUID.randomUUID() + extractExtension(fileName);
        Path destinationPath = uploadPath.resolve(newFileName);
        Files.copy(multipartFile.getInputStream(), destinationPath);
        return destinationPath;
    }

    public void deleteFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File innerFile : files) {
                    deleteFiles(innerFile);
                }
            }
        }
        file.delete();
    }
}
