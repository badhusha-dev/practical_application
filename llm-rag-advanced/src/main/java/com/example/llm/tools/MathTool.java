package com.example.llm.tools;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MathTool implements ToolRegistry.Tool {
    
    // Pattern to match safe mathematical expressions
    private static final Pattern SAFE_EXPRESSION_PATTERN = Pattern.compile(
            "^[0-9+\\-*/().\\s]+$"
    );
    
    // Pattern to match potentially dangerous operations
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
            "(java|script|import|class|new|throw|catch|try|exec|eval|system|runtime|process|file|io|net|sql|reflect)",
            Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public String getName() {
        return "math.eval";
    }
    
    @Override
    public String getDescription() {
        return "Safely evaluate mathematical expressions";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> expressionParam = new HashMap<>();
        expressionParam.put("type", "string");
        expressionParam.put("description", "The mathematical expression to evaluate (e.g., '2 + 3 * 4')");
        properties.put("expression", expressionParam);
        
        parameters.put("properties", properties);
        parameters.put("required", new String[]{"expression"});
        
        return parameters;
    }
    
    @Override
    public CompletableFuture<String> invoke(JsonNode args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String expression = args.get("expression").asText().trim();
                
                log.debug("Evaluating math expression: {}", expression);
                
                // Validate expression safety
                if (!isSafeExpression(expression)) {
                    return "Error: Invalid or unsafe mathematical expression. Only basic arithmetic operations are allowed.";
                }
                
                // Evaluate the expression
                double result = evaluateExpression(expression);
                
                return String.format("Result: %s = %.6f", expression, result);
                
            } catch (Exception e) {
                log.error("Error in math tool", e);
                return "Error evaluating expression: " + e.getMessage();
            }
        });
    }
    
    private boolean isSafeExpression(String expression) {
        // Check for dangerous patterns
        if (DANGEROUS_PATTERN.matcher(expression).find()) {
            return false;
        }
        
        // Check if expression matches safe pattern
        if (!SAFE_EXPRESSION_PATTERN.matcher(expression).matches()) {
            return false;
        }
        
        // Additional checks
        if (expression.length() > 100) {
            return false; // Too long
        }
        
        // Check for balanced parentheses
        int openParens = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                openParens++;
            } else if (c == ')') {
                openParens--;
                if (openParens < 0) {
                    return false; // Unbalanced parentheses
                }
            }
        }
        
        return openParens == 0; // Balanced parentheses
    }
    
    private double evaluateExpression(String expression) {
        try {
            // Simple expression evaluator for basic arithmetic
            return evaluateSimpleExpression(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid mathematical expression: " + e.getMessage());
        }
    }
    
    private double evaluateSimpleExpression(String expression) {
        // Remove whitespace
        expression = expression.replaceAll("\\s+", "");
        
        // Handle parentheses first
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf("(");
            int closeIndex = expression.indexOf(")", openIndex);
            
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Unbalanced parentheses");
            }
            
            String subExpression = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleExpression(subExpression);
            
            expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
        }
        
        // Handle multiplication and division
        while (expression.contains("*") || expression.contains("/")) {
            int mulIndex = expression.indexOf("*");
            int divIndex = expression.indexOf("/");
            
            int opIndex;
            if (mulIndex == -1) {
                opIndex = divIndex;
            } else if (divIndex == -1) {
                opIndex = mulIndex;
            } else {
                opIndex = Math.min(mulIndex, divIndex);
            }
            
            char operator = expression.charAt(opIndex);
            double left = getLeftOperand(expression, opIndex);
            double right = getRightOperand(expression, opIndex);
            
            double result = (operator == '*') ? left * right : left / right;
            
            expression = expression.substring(0, opIndex - String.valueOf(left).length()) + 
                        result + expression.substring(opIndex + String.valueOf(right).length() + 1);
        }
        
        // Handle addition and subtraction
        while (expression.contains("+") || (expression.contains("-") && !expression.startsWith("-"))) {
            int addIndex = expression.indexOf("+");
            int subIndex = expression.indexOf("-", 1); // Skip negative sign at start
            
            int opIndex;
            if (addIndex == -1) {
                opIndex = subIndex;
            } else if (subIndex == -1) {
                opIndex = addIndex;
            } else {
                opIndex = Math.min(addIndex, subIndex);
            }
            
            char operator = expression.charAt(opIndex);
            double left = getLeftOperand(expression, opIndex);
            double right = getRightOperand(expression, opIndex);
            
            double result = (operator == '+') ? left + right : left - right;
            
            expression = expression.substring(0, opIndex - String.valueOf(left).length()) + 
                        result + expression.substring(opIndex + String.valueOf(right).length() + 1);
        }
        
        return Double.parseDouble(expression);
    }
    
    private double getLeftOperand(String expression, int opIndex) {
        int start = opIndex - 1;
        while (start >= 0 && (Character.isDigit(expression.charAt(start)) || expression.charAt(start) == '.')) {
            start--;
        }
        return Double.parseDouble(expression.substring(start + 1, opIndex));
    }
    
    private double getRightOperand(String expression, int opIndex) {
        int end = opIndex + 1;
        while (end < expression.length() && (Character.isDigit(expression.charAt(end)) || expression.charAt(end) == '.')) {
            end++;
        }
        return Double.parseDouble(expression.substring(opIndex + 1, end));
    }
}
