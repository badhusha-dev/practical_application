package com.example.llm.tools;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class WeatherTool implements ToolRegistry.Tool {
    
    private static final Map<String, String[]> WEATHER_CONDITIONS = Map.of(
            "sunny", new String[]{"Clear skies", "Bright sunshine", "Partly cloudy"},
            "cloudy", new String[]{"Overcast", "Mostly cloudy", "Partly cloudy"},
            "rainy", new String[]{"Light rain", "Heavy rain", "Drizzle"},
            "snowy", new String[]{"Light snow", "Heavy snow", "Snow flurries"},
            "stormy", new String[]{"Thunderstorms", "Severe weather", "Windy conditions"}
    );
    
    @Override
    public String getName() {
        return "weather.lookup";
    }
    
    @Override
    public String getDescription() {
        return "Get current weather information for a specific city and date";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> cityParam = new HashMap<>();
        cityParam.put("type", "string");
        cityParam.put("description", "The city name to get weather for");
        properties.put("city", cityParam);
        
        Map<String, Object> dateParam = new HashMap<>();
        dateParam.put("type", "string");
        dateParam.put("description", "The date to get weather for (YYYY-MM-DD format)");
        properties.put("date", dateParam);
        
        parameters.put("properties", properties);
        parameters.put("required", new String[]{"city", "date"});
        
        return parameters;
    }
    
    @Override
    public CompletableFuture<String> invoke(JsonNode args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String city = args.get("city").asText();
                String dateStr = args.get("date").asText();
                
                log.debug("Weather lookup for city: {} on date: {}", city, dateStr);
                
                // Parse date
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (Exception e) {
                    return "Invalid date format. Please use YYYY-MM-DD format.";
                }
                
                // Generate mock weather data
                String weather = generateMockWeather(city, date);
                
                return String.format("Weather for %s on %s: %s", city, dateStr, weather);
                
            } catch (Exception e) {
                log.error("Error in weather tool", e);
                return "Error getting weather information: " + e.getMessage();
            }
        });
    }
    
    private String generateMockWeather(String city, LocalDate date) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // Generate temperature based on city and season
        int baseTemp = getBaseTemperature(city);
        int seasonalAdjustment = getSeasonalAdjustment(date);
        int temperature = baseTemp + seasonalAdjustment + random.nextInt(-10, 11);
        
        // Generate weather condition
        String[] conditions = {"sunny", "cloudy", "rainy", "snowy", "stormy"};
        String condition = conditions[random.nextInt(conditions.length)];
        String[] conditionDescriptions = WEATHER_CONDITIONS.get(condition);
        String description = conditionDescriptions[random.nextInt(conditionDescriptions.length)];
        
        // Generate humidity and wind
        int humidity = random.nextInt(30, 91);
        int windSpeed = random.nextInt(5, 26);
        String windDirection = getRandomWindDirection();
        
        return String.format(
                "Temperature: %d°C, Condition: %s, Humidity: %d%%, Wind: %d km/h %s",
                temperature, description, humidity, windSpeed, windDirection
        );
    }
    
    private int getBaseTemperature(String city) {
        // Mock base temperatures for different cities
        String cityLower = city.toLowerCase();
        if (cityLower.contains("london") || cityLower.contains("paris")) {
            return 15;
        } else if (cityLower.contains("tokyo") || cityLower.contains("seoul")) {
            return 18;
        } else if (cityLower.contains("moscow") || cityLower.contains("stockholm")) {
            return 5;
        } else if (cityLower.contains("sydney") || cityLower.contains("melbourne")) {
            return 20;
        } else if (cityLower.contains("new york") || cityLower.contains("chicago")) {
            return 12;
        } else if (cityLower.contains("miami") || cityLower.contains("los angeles")) {
            return 25;
        } else {
            return 18; // Default temperature
        }
    }
    
    private int getSeasonalAdjustment(LocalDate date) {
        int month = date.getMonthValue();
        if (month >= 3 && month <= 5) { // Spring
            return 5;
        } else if (month >= 6 && month <= 8) { // Summer
            return 15;
        } else if (month >= 9 && month <= 11) { // Autumn
            return 0;
        } else { // Winter
            return -10;
        }
    }
    
    private String getRandomWindDirection() {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[ThreadLocalRandom.current().nextInt(directions.length)];
    }
}
