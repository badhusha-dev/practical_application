package com.example.graphql.resolver;

import com.example.graphql.dto.ProductDTO;
import com.example.graphql.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.event.EventListener;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubscriptionResolver {
    
    private final ConcurrentMap<String, Sinks.Many<ProductDTO>> productSinks = new ConcurrentHashMap<>();
    
    @SubscriptionMapping
    public Publisher<ProductDTO> productAdded() {
        log.info("GraphQL Subscription: productAdded - new subscriber");
        
        String subscriberId = java.util.UUID.randomUUID().toString();
        Sinks.Many<ProductDTO> sink = Sinks.many().multicast().onBackpressureBuffer();
        productSinks.put(subscriberId, sink);
        
        // Clean up when subscriber disconnects
        sink.asFlux()
            .doOnCancel(() -> {
                log.info("GraphQL Subscription: productAdded - subscriber {} cancelled", subscriberId);
                productSinks.remove(subscriberId);
            })
            .doOnTerminate(() -> {
                log.info("GraphQL Subscription: productAdded - subscriber {} terminated", subscriberId);
                productSinks.remove(subscriberId);
            });
        
        return sink.asFlux();
    }
    
    @EventListener
    public void handleProductCreated(ProductCreatedEvent event) {
        ProductDTO product = event.getProduct();
        log.info("Publishing product added event: {}", product.getName());
        
        // Send to all active subscribers
        productSinks.values().forEach(sink -> {
            try {
                sink.tryEmitNext(product);
            } catch (Exception e) {
                log.warn("Failed to emit product to subscriber: {}", e.getMessage());
            }
        });
        
        // Remove completed sinks
        productSinks.entrySet().removeIf(entry -> 
            entry.getValue().currentSubscriberCount() == 0);
    }
    
    public int getActiveSubscriberCount() {
        return productSinks.size();
    }
}
