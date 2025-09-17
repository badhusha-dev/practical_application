-- V2__Create_file_metadata_table.sql
CREATE TABLE file_metadata (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100),
    s3_key VARCHAR(500) NOT NULL,
    s3_bucket VARCHAR(100) NOT NULL,
    file_hash VARCHAR(64),
    version INTEGER NOT NULL DEFAULT 1,
    is_latest BOOLEAN NOT NULL DEFAULT true,
    parent_file_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_file_metadata_user_id ON file_metadata(user_id);
CREATE INDEX idx_file_metadata_s3_key ON file_metadata(s3_key);
CREATE INDEX idx_file_metadata_s3_bucket ON file_metadata(s3_bucket);
CREATE INDEX idx_file_metadata_parent_file_id ON file_metadata(parent_file_id);
CREATE INDEX idx_file_metadata_is_latest ON file_metadata(is_latest);
CREATE INDEX idx_file_metadata_created_at ON file_metadata(created_at);
CREATE INDEX idx_file_metadata_filename ON file_metadata(original_filename);
CREATE INDEX idx_file_metadata_content_type ON file_metadata(content_type);

-- Create trigger for updated_at
CREATE TRIGGER update_file_metadata_updated_at 
    BEFORE UPDATE ON file_metadata 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add foreign key constraint for parent_file_id
ALTER TABLE file_metadata 
ADD CONSTRAINT fk_file_metadata_parent_file 
FOREIGN KEY (parent_file_id) REFERENCES file_metadata(id) ON DELETE SET NULL;
