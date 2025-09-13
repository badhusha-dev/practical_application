package com.example.llm.dto;

import com.example.llm.entity.ChatMessage;
import com.example.llm.entity.ChatSession;
import com.example.llm.entity.Chunk;
import com.example.llm.entity.Document;
import com.example.llm.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-12T19:57:34+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class EntityMapperImpl implements EntityMapper {

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setCreatedAt( user.getCreatedAt() );
        userDTO.setId( user.getId() );
        userDTO.setRoles( user.getRoles() );
        userDTO.setUsername( user.getUsername() );

        return userDTO;
    }

    @Override
    public List<UserDTO> toUserDTOs(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toUserDTO( user ) );
        }

        return list;
    }

    @Override
    public DocumentDTO toDocumentDTO(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentDTO documentDTO = new DocumentDTO();

        documentDTO.setChecksum( document.getChecksum() );
        documentDTO.setContentType( document.getContentType() );
        documentDTO.setCreatedAt( document.getCreatedAt() );
        documentDTO.setFilename( document.getFilename() );
        documentDTO.setId( document.getId() );
        documentDTO.setSize( document.getSize() );
        documentDTO.setTags( document.getTags() );

        return documentDTO;
    }

    @Override
    public List<DocumentDTO> toDocumentDTOs(List<Document> documents) {
        if ( documents == null ) {
            return null;
        }

        List<DocumentDTO> list = new ArrayList<DocumentDTO>( documents.size() );
        for ( Document document : documents ) {
            list.add( toDocumentDTO( document ) );
        }

        return list;
    }

    @Override
    public ChunkDTO toChunkDTO(Chunk chunk) {
        if ( chunk == null ) {
            return null;
        }

        ChunkDTO chunkDTO = new ChunkDTO();

        chunkDTO.setChunkIndex( chunk.getChunkIndex() );
        chunkDTO.setDocumentId( chunk.getDocumentId() );
        chunkDTO.setId( chunk.getId() );
        chunkDTO.setMetadata( chunk.getMetadata() );
        chunkDTO.setText( chunk.getText() );

        return chunkDTO;
    }

    @Override
    public List<ChunkDTO> toChunkDTOs(List<Chunk> chunks) {
        if ( chunks == null ) {
            return null;
        }

        List<ChunkDTO> list = new ArrayList<ChunkDTO>( chunks.size() );
        for ( Chunk chunk : chunks ) {
            list.add( toChunkDTO( chunk ) );
        }

        return list;
    }

    @Override
    public ChatSessionDTO toChatSessionDTO(ChatSession session) {
        if ( session == null ) {
            return null;
        }

        ChatSessionDTO chatSessionDTO = new ChatSessionDTO();

        chatSessionDTO.setCreatedAt( session.getCreatedAt() );
        chatSessionDTO.setId( session.getId() );
        chatSessionDTO.setTitle( session.getTitle() );
        chatSessionDTO.setUserId( session.getUserId() );

        return chatSessionDTO;
    }

    @Override
    public List<ChatSessionDTO> toChatSessionDTOs(List<ChatSession> sessions) {
        if ( sessions == null ) {
            return null;
        }

        List<ChatSessionDTO> list = new ArrayList<ChatSessionDTO>( sessions.size() );
        for ( ChatSession chatSession : sessions ) {
            list.add( toChatSessionDTO( chatSession ) );
        }

        return list;
    }

    @Override
    public ChatMessageDTO toChatMessageDTO(ChatMessage message) {
        if ( message == null ) {
            return null;
        }

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();

        chatMessageDTO.setContent( message.getContent() );
        chatMessageDTO.setCreatedAt( message.getCreatedAt() );
        chatMessageDTO.setId( message.getId() );
        chatMessageDTO.setRole( message.getRole() );
        chatMessageDTO.setSessionId( message.getSessionId() );
        chatMessageDTO.setTokensIn( message.getTokensIn() );
        chatMessageDTO.setTokensOut( message.getTokensOut() );

        return chatMessageDTO;
    }

    @Override
    public List<ChatMessageDTO> toChatMessageDTOs(List<ChatMessage> messages) {
        if ( messages == null ) {
            return null;
        }

        List<ChatMessageDTO> list = new ArrayList<ChatMessageDTO>( messages.size() );
        for ( ChatMessage chatMessage : messages ) {
            list.add( toChatMessageDTO( chatMessage ) );
        }

        return list;
    }

    @Override
    public User toUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setCreatedAt( userDTO.getCreatedAt() );
        user.setId( userDTO.getId() );
        user.setRoles( userDTO.getRoles() );
        user.setUsername( userDTO.getUsername() );

        return user;
    }

    @Override
    public Document toDocument(DocumentDTO documentDTO) {
        if ( documentDTO == null ) {
            return null;
        }

        Document document = new Document();

        document.setChecksum( documentDTO.getChecksum() );
        document.setContentType( documentDTO.getContentType() );
        document.setCreatedAt( documentDTO.getCreatedAt() );
        document.setFilename( documentDTO.getFilename() );
        document.setId( documentDTO.getId() );
        document.setSize( documentDTO.getSize() );
        document.setTags( documentDTO.getTags() );

        return document;
    }

    @Override
    public Chunk toChunk(ChunkDTO chunkDTO) {
        if ( chunkDTO == null ) {
            return null;
        }

        Chunk chunk = new Chunk();

        chunk.setChunkIndex( chunkDTO.getChunkIndex() );
        chunk.setDocumentId( chunkDTO.getDocumentId() );
        chunk.setId( chunkDTO.getId() );
        chunk.setMetadata( chunkDTO.getMetadata() );
        chunk.setText( chunkDTO.getText() );

        return chunk;
    }

    @Override
    public ChatSession toChatSession(ChatSessionDTO sessionDTO) {
        if ( sessionDTO == null ) {
            return null;
        }

        ChatSession chatSession = new ChatSession();

        chatSession.setCreatedAt( sessionDTO.getCreatedAt() );
        chatSession.setId( sessionDTO.getId() );
        chatSession.setTitle( sessionDTO.getTitle() );
        chatSession.setUserId( sessionDTO.getUserId() );

        return chatSession;
    }

    @Override
    public ChatMessage toChatMessage(ChatMessageDTO messageDTO) {
        if ( messageDTO == null ) {
            return null;
        }

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent( messageDTO.getContent() );
        chatMessage.setCreatedAt( messageDTO.getCreatedAt() );
        chatMessage.setId( messageDTO.getId() );
        chatMessage.setRole( messageDTO.getRole() );
        chatMessage.setSessionId( messageDTO.getSessionId() );
        chatMessage.setTokensIn( messageDTO.getTokensIn() );
        chatMessage.setTokensOut( messageDTO.getTokensOut() );

        return chatMessage;
    }
}
