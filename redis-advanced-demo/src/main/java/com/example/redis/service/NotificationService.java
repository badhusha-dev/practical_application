package com.example.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;
    
    private final List<String> messages = new CopyOnWriteArrayList<>();
    private final ChannelTopic topic = new ChannelTopic("notifications");
    
    public void publishMessage(String message) {
        try {
            String fullMessage = String.format("[%s] %s", LocalDateTime.now(), message);
            redisTemplate.convertAndSend(topic.getTopic(), fullMessage);
            logger.info("Published message to Redis channel: {}", fullMessage);
        } catch (Exception e) {
            logger.error("Error publishing message to Redis", e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
    
    public void subscribeToMessages() {
        try {
            MessageListenerAdapter adapter = new MessageListenerAdapter(this, "handleMessage");
            messageListenerContainer.addMessageListener(adapter, topic);
            logger.info("Subscribed to Redis channel: {}", topic.getTopic());
        } catch (Exception e) {
            logger.error("Error subscribing to Redis channel", e);
            throw new RuntimeException("Failed to subscribe to channel", e);
        }
    }
    
    public void handleMessage(String message) {
        logger.info("Received message from Redis: {}", message);
        messages.add(message);
        
        // Keep only last 100 messages to prevent memory issues
        if (messages.size() > 100) {
            messages.remove(0);
        }
    }
    
    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public void clearMessages() {
        messages.clear();
        logger.info("Cleared all messages");
    }
}
