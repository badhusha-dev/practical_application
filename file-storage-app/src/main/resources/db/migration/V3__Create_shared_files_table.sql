-- V3__Create_shared_files_table.sql
CREATE TABLE shared_files (
    id BIGSERIAL PRIMARY KEY,
    share_token VARCHAR(64) UNIQUE NOT NULL,
    expires_at TIMESTAMP,
    download_count INTEGER NOT NULL DEFAULT 0,
    max_downloads INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_id BIGINT NOT NULL REFERENCES file_metadata(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_shared_files_share_token ON shared_files(share_token);
CREATE INDEX idx_shared_files_file_id ON shared_files(file_id);
CREATE INDEX idx_shared_files_user_id ON shared_files(user_id);
CREATE INDEX idx_shared_files_is_active ON shared_files(is_active);
CREATE INDEX idx_shared_files_expires_at ON shared_files(expires_at);
CREATE INDEX idx_shared_files_created_at ON shared_files(created_at);
