package com.example.backend_j.vector.infrastructrue;


import com.example.backend_j.vector.application.domain.StoredFile;
import com.example.backend_j.vector.application.repository.LocalUploadsRepository;
// com.example.backend_j.utils.CryptoUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@Component
public class LocalUploadsStorageAdapter implements LocalUploadsRepository {

    private final Path uploadsRoot = Path.of("uploads").toAbsolutePath().normalize();


    @Override
    public StoredFile store(MultipartFile file ) {
        return storeInternal(file);
    }

    private StoredFile storeInternal(MultipartFile file) {
        Objects.requireNonNull(file, "file is required");

        String safeCategory = "common";
        String originalName = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "file"
                : file.getOriginalFilename();

        String cleanedOriginalName = sanitizeFilename(originalName);
        String extension = extractExtension(cleanedOriginalName);

        LocalDate today = LocalDate.now();
        Path dir = uploadsRoot
                .resolve(safeCategory)
                .resolve(String.valueOf(today.getYear()))
                .resolve(String.format("%02d", today.getMonthValue()))
                .resolve(String.format("%02d", today.getDayOfMonth()))
                .normalize();

        try {
            Files.createDirectories(dir);

            String seed = cleanedOriginalName
                    + ":" + file.getSize()
                    + ":" + LocalDateTime.now().toString();

            // String hashedName = CryptoUtils.sha256Hex(seed);
            String hashedName = seed;
            String storedName = extension.isEmpty() ? hashedName : (hashedName + "." + extension);

            Path target = dir.resolve(storedName).normalize();

            if (!target.startsWith(uploadsRoot)) {
                throw new IllegalStateException("Invalid storage path");
            }

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return StoredFile.builder()
                    .originalFilename(cleanedOriginalName)
                    .storedFilename(storedName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .absolutePath(target.toString())
                    .relativePath(uploadsRoot.relativize(target).toString().replace('\\', '/'))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to store file in uploads folder", e);
        }
    }

    @Override
    public void delete(String relativePath) {
        Path target = uploadsRoot.resolve(relativePath).normalize();
        if (!target.startsWith(uploadsRoot)) {
            throw new IllegalStateException("Invalid storage path");
        }
        try {
            Files.deleteIfExists(target);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete file: " + relativePath, e);
        }
    }


    private String sanitizeFilename(String filename) {
        String s = filename.trim();

        // 디렉토리 제거
        s = s.replace("\\", "/");
        int idx = s.lastIndexOf('/');
        if (idx >= 0) s = s.substring(idx + 1);

        // 위험 문자 제거
        s = s.replace("..", "_");
        s = s.replaceAll("[\\r\\n\\t]", "_");
        s = s.replaceAll("[:*?\"<>|]", "_");

        // 공백 정리
        s = s.replaceAll("\\s+", "_");

        // 너무 길면 제한(대략)
        if (s.length() > 200) s = s.substring(s.length() - 200);

        return s.isBlank() ? "file" : s;
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        String ext = filename.substring(dot + 1).trim();
        // 확장자 길이 제한(테이블 정의가 10이었으므로 동일하게 맞춤)
        if (ext.length() > 10) ext = ext.substring(0, 10);
        return ext;
    }

}

