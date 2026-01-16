package com.example.authentification.dto;

import com.example.authentification.entity.ImportLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses d'import
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResponseDTO {

    private Long id;
    private String fileName;
    private String fileType;
    private Integer totalRows;
    private Integer successCount;
    private Integer failureCount;
    private String errorDetails;
    private Long uploadedByUserId;
    private LocalDateTime uploadedAt;
    private String filePath;
    private String status;

    /**
     * Convertir une entité ImportLog en DTO
     */
    public static ImportResponseDTO fromEntity(ImportLog importLog) {
        return ImportResponseDTO.builder()
                .id(importLog.getId())
                .fileName(importLog.getFileName())
                .fileType(importLog.getFileType())
                .totalRows(importLog.getTotalRows())
                .successCount(importLog.getSuccessCount())
                .failureCount(importLog.getFailureCount())
                .errorDetails(importLog.getErrorDetails())
                .uploadedByUserId(importLog.getUploadedByUserId())
                .uploadedAt(importLog.getUploadedAt())
                .filePath(importLog.getFilePath())
                .status(importLog.getStatus().name())
                .build();
    }
}

