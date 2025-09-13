-- V1__Create_app_definitions_table.sql
CREATE TABLE app_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    config_json TEXT NOT NULL,
    description TEXT,
    version VARCHAR(50) DEFAULT '1.0.0',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_app_definitions_name ON app_definitions(name);
CREATE INDEX idx_app_definitions_active ON app_definitions(active);
CREATE INDEX idx_app_definitions_version ON app_definitions(version);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_app_definitions_updated_at 
    BEFORE UPDATE ON app_definitions 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
