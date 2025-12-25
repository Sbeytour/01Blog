package blog.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileStorageUtils {

    // Saves an uploaded file to the specified directory with a unique filename
    //  file The multipart file to save
    //  uploadDir The directory where the file should be saved
    //  defaultExtension Default extension if file has none
    public static String saveUploadedFile(MultipartFile file, String uploadDir, String defaultExtension) throws IOException {
        // Create directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Extract file extension
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : defaultExtension;

        // Generate unique filename
        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir, fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public static void deleteFile(String fileUrl, String baseDir) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(baseDir).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
        }
    }
}
