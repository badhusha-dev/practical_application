package com.example.llm.dto;

import com.example.llm.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);
    
    UserDTO toUserDTO(User user);
    List<UserDTO> toUserDTOs(List<User> users);
    
    DocumentDTO toDocumentDTO(Document document);
    List<DocumentDTO> toDocumentDTOs(List<Document> documents);
    
    ChunkDTO toChunkDTO(Chunk chunk);
    List<ChunkDTO> toChunkDTOs(List<Chunk> chunks);
    
    ChatSessionDTO toChatSessionDTO(ChatSession session);
    List<ChatSessionDTO> toChatSessionDTOs(List<ChatSession> sessions);
    
    ChatMessageDTO toChatMessageDTO(ChatMessage message);
    List<ChatMessageDTO> toChatMessageDTOs(List<ChatMessage> messages);
    
    // Reverse mappings
    User toUser(UserDTO userDTO);
    Document toDocument(DocumentDTO documentDTO);
    Chunk toChunk(ChunkDTO chunkDTO);
    ChatSession toChatSession(ChatSessionDTO sessionDTO);
    ChatMessage toChatMessage(ChatMessageDTO messageDTO);
}
