package ua.thecoon.tech.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class PhotoDownloader {

    private static final Logger logger = Logger.getLogger(PhotoDownloader.class.getName());

    public static void downloadPhoto(String photoUrl) throws IOException {
        logger.info("Downloading profile photo from URL: " + photoUrl);


        Path directoryPath = Paths.get("photos");
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);

            logger.info("Created 'photos' directory.");
        }

        URL url = new URL(photoUrl);

        try (InputStream in = url.openStream()) {
            Path filePath = Paths.get("photos/profile-photo.jpg");
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Profile photo downloaded to 'profile-photo.jpg'.");
        } catch (IOException e) {
            logger.severe("Failed to download photo: " + e.getMessage());
            throw e;
        }
    }

}
