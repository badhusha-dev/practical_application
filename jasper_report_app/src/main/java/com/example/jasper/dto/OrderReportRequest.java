package com.example.jasper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportRequest {
    
    private LocalDate fromDate;
    private LocalDate toDate;
    private String format; // "pdf" or "xlsx"
    
    public boolean isValidFormat() {
        return "pdf".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format);
    }
}

