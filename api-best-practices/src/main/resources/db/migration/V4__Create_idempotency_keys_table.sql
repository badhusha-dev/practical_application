-- V4__Create_idempotency_keys_table.sql
CREATE TABLE idempotency_keys (
    key_value VARCHAR(255) PRIMARY KEY,
    request_hash VARCHAR(64) NOT NULL,
    response_body TEXT,
    http_status INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ NOT NULL
);

-- Create indexes for idempotency_keys table
CREATE INDEX idx_idempotency_keys_expires_at ON idempotency_keys(expires_at);
CREATE INDEX idx_idempotency_keys_request_hash ON idempotency_keys(request_hash);

-- Add comments
COMMENT ON TABLE idempotency_keys IS 'Stores idempotency keys to prevent duplicate requests';
COMMENT ON COLUMN idempotency_keys.key_value IS 'The idempotency key from the request header';
COMMENT ON COLUMN idempotency_keys.request_hash IS 'Hash of the request body for validation';
COMMENT ON COLUMN idempotency_keys.response_body IS 'Cached response body for duplicate requests';
COMMENT ON COLUMN idempotency_keys.expires_at IS 'When this idempotency key expires';
