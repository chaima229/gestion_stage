-- Migration V4: Add ImportLog table for tracking Excel imports
-- Date: 2026-01-12
-- Description: Create import_logs table for SOUS_ADMIN file uploads

CREATE TABLE IF NOT EXISTS import_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    total_rows INT NOT NULL,
    success_count INT NOT NULL,
    failure_count INT NOT NULL,
    error_details LONGTEXT,
    uploaded_by_user_id BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    file_path VARCHAR(255),
    status ENUM('PENDING', 'SUCCESS', 'PARTIAL_SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_uploaded_by_user_id (uploaded_by_user_id),
    INDEX idx_file_type (file_type),
    INDEX idx_status (status),
    INDEX idx_uploaded_at (uploaded_at),
    CONSTRAINT fk_import_logs_user FOREIGN KEY (uploaded_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

