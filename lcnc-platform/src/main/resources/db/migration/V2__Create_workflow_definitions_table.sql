-- V2__Create_workflow_definitions_table.sql
CREATE TABLE workflow_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    bpmn_xml TEXT NOT NULL,
    description TEXT,
    version VARCHAR(50) DEFAULT '1.0.0',
    deployed BOOLEAN DEFAULT FALSE,
    camunda_deployment_id VARCHAR(255),
    process_definition_key VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_workflow_definitions_name ON workflow_definitions(name);
CREATE INDEX idx_workflow_definitions_active ON workflow_definitions(active);
CREATE INDEX idx_workflow_definitions_deployed ON workflow_definitions(deployed);
CREATE INDEX idx_workflow_definitions_camunda_deployment_id ON workflow_definitions(camunda_deployment_id);
CREATE INDEX idx_workflow_definitions_process_definition_key ON workflow_definitions(process_definition_key);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_workflow_definitions_updated_at 
    BEFORE UPDATE ON workflow_definitions 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
