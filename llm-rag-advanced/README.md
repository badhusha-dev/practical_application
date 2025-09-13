# LLM RAG Advanced Demo

A comprehensive Spring Boot application demonstrating advanced RAG (Retrieval-Augmented Generation) capabilities with LLM integration, tool calling, and real-time streaming.

## Features

### 🚀 Core RAG Pipeline
- **Document Ingestion**: Parse PDFs, DOCX, TXT files with Apache Tika
- **Text Splitting**: Intelligent chunking with overlap for optimal retrieval
- **Vector Embeddings**: Generate embeddings using OpenAI or Ollama
- **Similarity Search**: PostgreSQL with pgvector extension for efficient vector search
- **Context Retrieval**: MMR reranking and context assembly

### 🤖 LLM Integration
- **Provider Agnostic**: Support for OpenAI and Ollama
- **Streaming Responses**: Server-Sent Events (SSE) for real-time token streaming
- **Tool Calling**: Function calling with weather, math, and URL summary tools
- **JWT Authentication**: Secure API access with token-based authentication

### ⚡ Advanced Features
- **Redis Caching**: Cache search results and embeddings
- **Rate Limiting**: Redis-based sliding window rate limiting
- **Real-time Chat**: WebSocket-like experience with SSE
- **Document Management**: Upload, search, and manage documents
- **Tool System**: Extensible tool registry with JSON schema validation

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 15+ with pgvector extension
- Redis 6+
- OpenAI API key OR Ollama running locally

## Setup Instructions

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE llm_rag;

-- Install pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create user (optional)
CREATE USER llm_user WITH PASSWORD 'llm_pass';
GRANT ALL PRIVILEGES ON DATABASE llm_rag TO llm_user;
```

### 2. Redis Setup
```bash
# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
```

### 3. LLM Provider Setup

#### Option A: OpenAI
```bash
export OPENAI_API_KEY=sk-your-api-key-here
export LLM_PROVIDER=openai
export OPENAI_MODEL=gpt-4o-mini
```

#### Option B: Ollama
```bash
# Install and start Ollama
ollama serve

# Pull a model
ollama pull llama3.1
ollama pull nomic-embed-text

# Set environment variables
export LLM_PROVIDER=ollama
export OLLAMA_MODEL=llama3.1
export OLLAMA_BASE_URL=http://localhost:11434
```

### 4. Application Configuration
Update `src/main/resources/application.yml` if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/llm_rag
    username: postgres
    password: postgres
  redis:
    host: localhost
    port: 6379

app:
  provider: ${LLM_PROVIDER:openai}
  openai:
    apiKey: ${OPENAI_API_KEY:}
    model: ${OPENAI_MODEL:gpt-4o-mini}
  ollama:
    baseUrl: ${OLLAMA_BASE_URL:http://localhost:11434}
    model: ${OLLAMA_MODEL:llama3.1}
```

### 5. Run Application
```bash
# Navigate to project directory
cd llm-rag-advanced

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/llm-rag-advanced-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token
- `GET /auth/me` - Get current user info

### Document Management
- `POST /api/documents` - Upload document (multipart)
- `GET /api/documents` - List user documents
- `GET /api/documents/{id}` - Get document details
- `DELETE /api/documents/{id}` - Delete document

### Search & Retrieval
- `GET /api/search?q={query}` - Search documents
- `POST /api/embeddings` - Generate embeddings

### Chat
- `POST /api/chat` - Streaming chat (SSE)
- `POST /api/chat/nonstream` - Non-streaming chat
- `GET /api/chat/sessions` - Get chat sessions

### Tools
- `GET /api/tools` - List available tools
- `POST /api/tools/{name}/invoke` - Invoke specific tool
- `GET /api/tools/{name}/demo` - Tool demo endpoints

## Usage Examples

### 1. Register and Login
```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### 2. Upload Document
```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@document.pdf" \
  -F "tags=[\"research\",\"ai\"]"
```

### 3. Search Documents
```bash
curl -X GET "http://localhost:8080/api/search?q=machine%20learning" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Chat with RAG
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What is machine learning?",
    "useRag": true,
    "tools": ["math.eval", "weather.lookup"]
  }'
```

### 5. Streaming Chat (SSE)
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '{
    "message": "Explain quantum computing",
    "useRag": true
  }'
```

## Tool System

### Available Tools

#### 1. Weather Tool (`weather.lookup`)
```json
{
  "name": "weather.lookup",
  "args": {
    "city": "London",
    "date": "2024-01-15"
  }
}
```

#### 2. Math Tool (`math.eval`)
```json
{
  "name": "math.eval",
  "args": {
    "expression": "2 + 3 * 4"
  }
}
```

#### 3. URL Summary Tool (`url.summary`)
```json
{
  "name": "url.summary",
  "args": {
    "url": "https://en.wikipedia.org/wiki/Artificial_intelligence"
  }
}
```

### Tool Calling in Chat
The LLM can automatically call tools based on user requests:
- "What's the weather in Paris tomorrow?" → Weather tool
- "Calculate 15 * 23 + 45" → Math tool
- "Summarize this article: https://example.com" → URL summary tool

## Web Interface

Access the web interface at `http://localhost:8080` to:
- Register/Login with JWT authentication
- Upload documents with drag & drop
- Search documents with real-time results
- Chat with RAG-enabled responses
- Use tools through natural language

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │  Spring Boot    │    │   PostgreSQL    │
│                 │◄──►│   Application   │◄──►│   + pgvector    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │      Redis      │
                       │                 │
                       │ • Cache Layer   │
                       │ • Rate Limiting │
                       │ • Session Store │
                       └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   LLM Provider   │
                       │                 │
                       │ • OpenAI API    │
                       │ • Ollama Local  │
                       │ • Embeddings    │
                       │ • Chat/Stream   │
                       └─────────────────┘
```

## Configuration Options

### Application Properties
```yaml
app:
  provider: openai                    # openai or ollama
  rag:
    topK: 6                          # Number of chunks to retrieve
    maxContextChars: 10000           # Maximum context size
    chunkSize: 3000                  # Chunk size in characters
    chunkOverlap: 200                # Overlap between chunks
  rateLimit:
    requestsPerMinute: 60            # Rate limit per user
    windowSizeMinutes: 1             # Rate limit window
  tools:
    enabled: true                    # Enable tool calling
    maxToolCalls: 3                  # Max tool calls per request
    toolTimeoutSeconds: 30           # Tool execution timeout
```

### Environment Variables
- `LLM_PROVIDER` - LLM provider (openai/ollama)
- `OPENAI_API_KEY` - OpenAI API key
- `OPENAI_MODEL` - OpenAI model name
- `OLLAMA_BASE_URL` - Ollama server URL
- `OLLAMA_MODEL` - Ollama model name
- `JWT_SECRET` - JWT signing secret

## Monitoring & Health Checks

### Actuator Endpoints
- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus

### Key Metrics
- Chat request count and duration
- Document upload success rate
- Embedding generation time
- Cache hit/miss ratios
- Rate limit violations

## Troubleshooting

### Common Issues

1. **pgvector Extension Not Found**
   ```sql
   CREATE EXTENSION IF NOT EXISTS vector;
   ```

2. **Redis Connection Failed**
   - Ensure Redis server is running
   - Check Redis host/port configuration

3. **OpenAI API Errors**
   - Verify API key is valid
   - Check rate limits and billing

4. **Ollama Connection Issues**
   - Ensure Ollama is running: `ollama serve`
   - Verify model is pulled: `ollama list`

5. **Document Upload Fails**
   - Check file size limits (50MB default)
   - Verify file format is supported

### Logs
Application logs show detailed information:
```yaml
logging:
  level:
    com.example.llm: DEBUG
    org.springframework.security: INFO
    org.springframework.data.redis: INFO
```

## Development

### Adding New Tools
1. Implement `ToolRegistry.Tool` interface
2. Register tool in `LlmRagAdvancedApplication`
3. Add tool to chat interface checkboxes

### Custom LLM Providers
1. Implement `LlmProvider` interface
2. Add provider configuration
3. Update `LlmProviderConfig`

### Extending RAG Pipeline
1. Modify `TextSplitter` for custom chunking
2. Update `RetrieverService` for hybrid search
3. Enhance `RerankerService` for better ranking

## Performance Tips

- Use Redis caching for frequently accessed data
- Optimize chunk size based on your documents
- Tune `topK` parameter for optimal retrieval
- Monitor embedding generation costs
- Use connection pooling for database

## Security Considerations

- JWT tokens expire after 24 hours
- Rate limiting prevents abuse
- File upload size limits prevent DoS
- Tool execution has timeouts
- URL summary tool blocks dangerous domains

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
