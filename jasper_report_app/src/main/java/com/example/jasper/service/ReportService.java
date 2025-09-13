package com.example.jasper.service;

import com.example.jasper.entity.Order;
import com.example.jasper.entity.User;
import com.example.jasper.repository.OrderRepository;
import com.example.jasper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final Map<String, JasperReport> compiledReports;
    
    public byte[] generateUserReport(String format) {
        try {
            log.info("Generating user report in format: {}", format);
            
            // Get users data
            List<User> users = userRepository.findAll();
            
            // Get compiled report
            JasperReport report = compiledReports.get("users");
            if (report == null) {
                throw new RuntimeException("Users report template not found");
            }
            
            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
            
            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
            
            // Export based on format
            return exportReport(jasperPrint, format);
            
        } catch (Exception e) {
            log.error("Error generating user report", e);
            throw new RuntimeException("Failed to generate user report", e);
        }
    }
    
    public byte[] generateOrderReport(String format, LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            log.info("Generating order report from {} to {} in format: {}", fromDate, toDate, format);
            
            // Get orders data
            List<Order> orders;
            if (fromDate != null && toDate != null) {
                orders = orderRepository.findOrdersWithDetailsByDateRange(fromDate, toDate);
            } else {
                orders = orderRepository.findAllOrdersWithDetails();
            }
            
            // Get compiled report
            JasperReport report = compiledReports.get("orders");
            if (report == null) {
                throw new RuntimeException("Orders report template not found");
            }
            
            // Create parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("totalOrders", orders.size());
            
            // Calculate total amount
            double totalAmount = orders.stream()
                    .mapToDouble(order -> order.getTotalAmount().doubleValue())
                    .sum();
            parameters.put("totalAmount", totalAmount);
            
            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
            
            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
            // Export based on format
            return exportReport(jasperPrint, format);
            
        } catch (Exception e) {
            log.error("Error generating order report", e);
            throw new RuntimeException("Failed to generate order report", e);
        }
    }
    
    private byte[] exportReport(JasperPrint jasperPrint, String format) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        if ("pdf".equalsIgnoreCase(format)) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        
        return outputStream.toByteArray();
    }
}
