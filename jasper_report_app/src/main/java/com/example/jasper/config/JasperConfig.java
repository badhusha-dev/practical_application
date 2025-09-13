package com.example.jasper.config;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JasperConfig {
    
    @Bean
    public Map<String, JasperReport> compiledReports() {
        Map<String, JasperReport> reports = new HashMap<>();
        
        try {
            // Compile users report
            InputStream usersReportStream = getClass().getResourceAsStream("/reports/users_report.jrxml");
            if (usersReportStream != null) {
                JasperReport usersReport = JasperCompileManager.compileReport(usersReportStream);
                reports.put("users", usersReport);
                usersReportStream.close();
            }
            
            // Compile orders report
            InputStream ordersReportStream = getClass().getResourceAsStream("/reports/orders_report.jrxml");
            if (ordersReportStream != null) {
                JasperReport ordersReport = JasperCompileManager.compileReport(ordersReportStream);
                reports.put("orders", ordersReport);
                ordersReportStream.close();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile JasperReports", e);
        }
        
        return reports;
    }
}

