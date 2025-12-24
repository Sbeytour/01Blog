package blog.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorageUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtils.class);

    /**
     * Deletes a file from the filesystem
     * @param fileUrl URL of the file to delete
     * @param baseDir Base directory where files are stored
     */
    public static void deleteFile(String fileUrl, String baseDir) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(baseDir).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            logger.info("Successfully deleted file: {}", fileUrl);
        } catch (IOException e) {
            logger.error("Could not delete file: {}. Error: {}", fileUrl, e.getMessage());
        }
    }

    /**
     * Extracts filename from a file URL
     * @param fileUrl URL of the file
     * @return Filename extracted from the URL
     */
    public static String extractFilename(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
