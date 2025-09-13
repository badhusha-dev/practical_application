package com.example.llm;

import com.example.llm.tools.MathTool;
import com.example.llm.tools.ToolRegistry;
import com.example.llm.tools.UrlSummaryTool;
import com.example.llm.tools.WeatherTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class LlmRagAdvancedApplication {
    
    public static void main(String[] args) {
        log.info("Starting LLM RAG Advanced Application...");
        SpringApplication.run(LlmRagAdvancedApplication.class, args);
        log.info("LLM RAG Advanced Application started successfully!");
    }
    
    @Bean
    public CommandLineRunner initializeTools(ToolRegistry toolRegistry,
                                           WeatherTool weatherTool,
                                           MathTool mathTool,
                                           UrlSummaryTool urlSummaryTool) {
        return args -> {
            log.info("Initializing tools...");
            
            toolRegistry.registerTool(weatherTool);
            toolRegistry.registerTool(mathTool);
            toolRegistry.registerTool(urlSummaryTool);
            
            log.info("Tools initialized successfully");
            log.info("Available tools: {}", toolRegistry.getAllTools().keySet());
        };
    }
}
