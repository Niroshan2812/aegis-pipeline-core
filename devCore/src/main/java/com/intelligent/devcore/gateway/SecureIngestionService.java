package com.intelligent.devcore.gateway;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

// perform apache tika and UUID generation
public class SecureIngestionService {
    private final StorageProvider storageProvider;
    private final Tika tika;
    private final List<String> allowedMimeTypes;

    public SecureIngestionService(StorageProvider storageProvider, Tika tika, List<String> allowedMimeTypes) {
        this.storageProvider = storageProvider;
        this.tika = tika;
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public String processUpload(MultipartFile file)throws Exception{

        // Apache tika to read the magic bytes from the file's input stream to
        // determine is it real MIME type, completely ignoring the user-provided file extension.
        String detectedType = tika.detect(file.getInputStream());

        // check if the detected type is in our allowed list to fail-fast and reject
        //malicious payload before they reach the AI agents.
        if (!allowedMimeTypes.contains(detectedType)) {
            throw new SecurityException("Security Alert: Invalid file type detected -> " + detectedType);
        }

        // generate random UUID TO COMPLETELY strip the original filename
        // neutralizing any Path Traversal attacks from hackers.
        String extension = getExtensionFromMimeType(detectedType);
        String secureFileName = UUID.randomUUID().toString() + extension;

        // Pass the raw inputStream into abstract storage provider to be saved.
        return storageProvider.saveFiles(file.getInputStream(), secureFileName);
    }

    private String getExtensionFromMimeType(String mimeType) {
        return switch (mimeType) {
            case "video/mp4" -> ".mp4";
            case "audio/ogg" -> ".ogg";
            case "audio/wav" -> ".wav";
            case "application/pdf" -> ".pdf";
            case "application/zip" -> ".zip";
            case "application/msword" -> ".doc";
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> ".bin";
        };
    }
}
