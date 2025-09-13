# Jasper Reports Demo

A Spring Boot application demonstrating JasperReports integration with PostgreSQL database, featuring user and order reports in PDF and Excel formats. Now includes a modern web interface built with Thymeleaf and Bootstrap.

## Features

- **Database Integration**: PostgreSQL with Flyway migrations
- **Report Generation**: PDF and Excel (XLSX) formats
- **REST API**: Simple endpoints for report generation
- **Web Interface**: Modern, responsive web UI with Thymeleaf
- **Sample Data**: Pre-populated with users, products, and orders
- **Advanced Reports**: Date filtering, totals, and formatted output

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

## Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE jasper_report_db;
```

Update database credentials in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jasper_report_db
    username: your_username
    password: your_password
```

### 2. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Web Interface

### Home Page
- **URL**: `http://localhost:8080`
- **Features**: Navigation menu, report overview, feature highlights

### Users Report Page
- **URL**: `http://localhost:8080/reports/users/page`
- **Features**: Format selection (PDF/Excel), download buttons, report information

### Orders Report Page
- **URL**: `http://localhost:8080/reports/orders/page`
- **Features**: Date range filtering, format selection, download buttons, helpful tips

## API Endpoints

### User Reports

Generate a PDF report of all users:
```
GET /reports/users?format=pdf
```

Generate an Excel report of all users:
```
GET /reports/users?format=xlsx
```

### Order Reports

Generate a PDF report of all orders:
```
GET /reports/orders?format=pdf
```

Generate an Excel report of orders within a date range:
```
GET /reports/orders?from=2024-01-01&to=2024-01-31&format=xlsx
```

## Project Structure

```
src/main/java/com/example/jasper/
├── entity/
│   ├── User.java          # User entity
│   ├── Product.java       # Product entity
│   └── Order.java         # Order entity
├── repository/
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── service/
│   └── ReportService.java # JasperReports generation logic
├── controller/
│   └── ReportController.java # REST endpoints
├── config/
│   └── JasperConfig.java  # JasperReports configuration
└── dto/
    ├── UserReportRequest.java
    └── OrderReportRequest.java

src/main/resources/
├── templates/
│   ├── index.html          # Home page
│   ├── layout.html         # Layout template
│   ├── error.html          # Error page
│   └── reports/
│       ├── users.html      # Users report page
│       └── orders.html     # Orders report page
├── static/css/
│   └── style.css           # Custom CSS styles
├── reports/
│   ├── users_report.jrxml  # User report template
│   └── orders_report.jrxml # Order report template
├── db/migration/
│   ├── V1__create_tables.sql
│   └── V2__insert_sample_data.sql
└── application.yml
```

## Sample Data

The application includes sample data:

- **5 Users**: John Doe, Jane Smith, Bob Johnson, Alice Brown, Charlie Wilson
- **8 Products**: Laptop, Mouse, Keyboard, Monitor, Headphones, Webcam, Tablet, Smartphone
- **15 Orders**: Various combinations of users and products with realistic pricing

## Report Features

### User Report
- User ID, Name, Email, Creation Date
- Professional table layout with headers
- Total count summary

### Order Report
- Order details with customer and product information
- Quantity, unit price, and total amount
- Date range filtering support
- Summary statistics (total orders, total amount)
- Currency formatting

## Advanced Features

- **Date Range Filtering**: Filter orders by date range
- **Multiple Formats**: Support for both PDF and Excel output
- **Professional Styling**: Formatted tables with borders and headers
- **Summary Statistics**: Automatic calculation of totals and counts
- **Error Handling**: Comprehensive error handling and logging

## Customization

### Adding New Reports

1. Create a new `.jrxml` file in `src/main/resources/reports/`
2. Add compilation logic in `JasperConfig.java`
3. Create service methods in `ReportService.java`
4. Add REST endpoints in `ReportController.java`

### Modifying Existing Reports

Edit the `.jrxml` files in `src/main/resources/reports/` and restart the application.

## Troubleshooting

### Common Issues

1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **Report Compilation**: Check that `.jrxml` files are valid XML
3. **Memory Issues**: Increase JVM heap size for large reports: `-Xmx2g`

### Logs

Enable debug logging by updating `application.yml`:

```yaml
logging:
  level:
    com.example.jasper: DEBUG
```

## Dependencies

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Boot Thymeleaf
- PostgreSQL Driver
- Flyway
- JasperReports 6.20.0
- Apache POI (for Excel export)
- Lombok
- Bootstrap 5.1.3 (CDN)
- Font Awesome (CDN)

## License

This project is for demonstration purposes.

