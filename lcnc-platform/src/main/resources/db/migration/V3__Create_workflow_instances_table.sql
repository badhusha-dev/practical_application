-- V3__Create_workflow_instances_table.sql
CREATE TABLE workflow_instances (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definitions(id),
    process_instance_id VARCHAR(255) NOT NULL UNIQUE,
    process_definition_key VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'RUNNING',
    variables JSONB,
    started_by VARCHAR(255),
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_workflow_instances_workflow_definition_id ON workflow_instances(workflow_definition_id);
CREATE INDEX idx_workflow_instances_process_instance_id ON workflow_instances(process_instance_id);
CREATE INDEX idx_workflow_instances_status ON workflow_instances(status);
CREATE INDEX idx_workflow_instances_started_by ON workflow_instances(started_by);
CREATE INDEX idx_workflow_instances_started_at ON workflow_instances(started_at);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_workflow_instances_updated_at 
    BEFORE UPDATE ON workflow_instances 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
