package com.example.jasper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportRequest {
    
    private String format; // "pdf" or "xlsx"
    
    public boolean isValidFormat() {
        return "pdf".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format);
    }
}

