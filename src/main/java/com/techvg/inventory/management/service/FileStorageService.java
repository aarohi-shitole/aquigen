package com.techvg.inventory.management.service;

import com.techvg.inventory.management.web.rest.errors.MentionedFileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(Environment env) {
        this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir", "./uploads/files")).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String[] fileNameParts = fileName.split("\\.");

        return fileNameParts[fileNameParts.length - 1];
    }

    //    public String storeFile(MultipartFile file, Long productId) {
    //        // Normalize file name
    //        String fileName = "COA_" + productId + "." + getFileExtension(file.getOriginalFilename());
    //
    //        try {
    //            // Check if the filename contains invalid characters
    //            if (fileName.contains("..")) {
    //                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
    //            }
    //
    //            Path targetLocation = this.fileStorageLocation.resolve(fileName);
    //            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    //
    //            return fileName;
    //        } catch (IOException ex) {
    //            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
    //        }
    //    }

    public String storeFile(MultipartFile file, String filename) {
        // Normalize file name
        //String fileName = "COA_" + productId + "." + getFileExtension(file.getOriginalFilename());

        try {
            // Check if the filename contains invalid characters
            if (filename.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + filename);
            }

            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + filename + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MentionedFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MentionedFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
