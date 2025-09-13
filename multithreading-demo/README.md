# Multithreading & Concurrency Demo

A comprehensive Spring Boot application demonstrating practical multithreading and concurrency concepts in Java. This project provides interactive demonstrations of various concurrency patterns and synchronization mechanisms commonly used in Java applications.

## 🚀 Features

### Concurrency Demos
- **Odd/Even Number Printing**: Two threads printing numbers alternately using synchronization
- **Producer/Consumer**: Classic producer-consumer pattern using `wait()` and `notify()`
- **Deadlock Demo**: Demonstrates deadlock scenarios with prevention options
- **Thread-safe Singleton**: Double-checked locking pattern implementation
- **CountDownLatch**: Coordination between multiple threads
- **CyclicBarrier**: Synchronization at multiple barrier points
- **ReadWriteLock**: Optimized locking for read-heavy operations
- **CompletableFuture**: Asynchronous programming with CompletableFuture
- **ThreadPool**: Different thread pool configurations and behaviors

### Technical Features
- **Modern UI**: Responsive web interface built with Thymeleaf and Bootstrap
- **Real-time Results**: Live demonstration of thread execution with detailed logging
- **Interactive Controls**: Configurable parameters for each demo
- **Comprehensive Logging**: Detailed thread execution logs using SLF4J
- **Clean Architecture**: Modular, extensible codebase following Spring Boot best practices

## 🛠️ Tech Stack

- **Java 17**: Latest LTS version with modern language features
- **Spring Boot 3.x**: Latest Spring Boot framework
- **Thymeleaf**: Server-side templating engine for UI
- **Bootstrap 5**: Modern CSS framework for responsive design
- **Maven**: Build and dependency management
- **Lombok**: Reduces boilerplate code
- **SLF4J**: Logging framework

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Web browser (Chrome, Firefox, Safari, Edge)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd multithreading-demo
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

### 4. Access the Application
Open your web browser and navigate to:
```
http://localhost:8080
```

## 🎯 How to Use

### Running Demos
1. **Navigate to the Homepage**: The main dashboard displays all available demos
2. **Configure Parameters**: Each demo has configurable parameters (thread count, item count, etc.)
3. **Click Run**: Click the demo button to execute the demonstration
4. **View Results**: Results appear in the bottom section with detailed logs

### Demo Descriptions

#### 🔢 Odd/Even Number Printing
- **Purpose**: Demonstrates thread synchronization
- **Parameters**: Maximum number to print
- **What it shows**: Two threads printing odd and even numbers alternately

#### 🔄 Producer/Consumer
- **Purpose**: Classic synchronization pattern
- **Parameters**: Number of items to produce/consume
- **What it shows**: Producer creates items, Consumer consumes them using wait/notify

#### ⚠️ Deadlock Demo
- **Purpose**: Shows deadlock scenarios and prevention
- **Parameters**: Enable/disable deadlock prevention
- **What it shows**: How deadlocks occur and how to prevent them

#### 🏗️ Thread-safe Singleton
- **Purpose**: Demonstrates singleton pattern with thread safety
- **Parameters**: Number of threads accessing singleton
- **What it shows**: Multiple threads safely accessing singleton instance

#### 🔢 CountDownLatch
- **Purpose**: Coordination between multiple threads
- **Parameters**: Number of worker threads
- **What it shows**: Workers completing tasks before main thread proceeds

#### 🔄 CyclicBarrier
- **Purpose**: Synchronization at multiple points
- **Parameters**: Thread count and number of phases
- **What it shows**: Threads synchronizing at barrier points in multiple phases

#### 📖 ReadWriteLock
- **Purpose**: Optimized locking for read-heavy operations
- **Parameters**: Number of reader and writer threads
- **What it shows**: Multiple readers accessing data concurrently, writers getting exclusive access

#### ⚡ CompletableFuture
- **Purpose**: Asynchronous programming
- **Parameters**: None (fixed demonstration)
- **What it shows**: Asynchronous task execution and composition

#### 🏊 ThreadPool
- **Purpose**: Different thread pool configurations
- **Parameters**: Pool type and task count
- **What it shows**: How different thread pools handle tasks

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/concurrency/multithreading/
│   │       ├── MultithreadingDemoApplication.java
│   │       ├── controller/
│   │       │   └── DemoController.java
│   │       ├── service/
│   │       │   ├── OddEvenService.java
│   │       │   ├── ProducerConsumerService.java
│   │       │   ├── DeadlockService.java
│   │       │   ├── SingletonService.java
│   │       │   ├── CountDownLatchService.java
│   │       │   ├── CyclicBarrierService.java
│   │       │   ├── ReadWriteLockService.java
│   │       │   ├── CompletableFutureService.java
│   │       │   └── ThreadPoolService.java
│   │       ├── model/
│   │       │   ├── DemoResult.java
│   │       │   └── SharedData.java
│   │       └── util/
│   │           └── ThreadUtils.java
│   └── resources/
│       ├── templates/
│       │   └── index.html
│       └── static/
│           ├── css/
│           │   └── style.css
│           └── js/
│               └── demo.js
└── test/
    └── java/
        └── com/concurrency/multithreading/
            └── MultithreadingDemoApplicationTests.java
```

## 🔧 Configuration

### Application Properties
The application uses default Spring Boot configuration. Key settings:

- **Server Port**: 8080 (default)
- **Context Path**: / (root)
- **Logging Level**: INFO (configurable in `application.properties`)

### Customization
You can modify demo parameters by editing the service classes or updating the UI controls.

## 🧪 Testing

### Running Tests
```bash
mvn test
```

### Manual Testing
1. Start the application
2. Navigate to `http://localhost:8080`
3. Try each demo with different parameters
4. Verify logs in the console and UI

## 📊 Sample Output

### Console Logs
```
2024-01-15 10:30:15.123 INFO  --- [main] c.c.m.controller.DemoController : Loading home page
2024-01-15 10:30:20.456 INFO  --- [main] c.c.m.controller.DemoController : Running Odd/Even demo with max number: 20
2024-01-15 10:30:20.457 INFO  --- [Thread-1] c.c.m.service.OddEvenService : Odd thread started
2024-01-15 10:30:20.458 INFO  --- [Thread-2] c.c.m.service.OddEvenService : Even thread started
2024-01-15 10:30:20.459 INFO  --- [Thread-1] c.c.m.service.OddEvenService : Odd: 1
2024-01-15 10:30:20.460 INFO  --- [Thread-2] c.c.m.service.OddEvenService : Even: 2
...
```

### UI Results
The web interface displays:
- Status messages (Success, Error, Info)
- Detailed execution logs with timestamps
- Thread names and execution flow
- Synchronization points and wait states

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/multithreading-demo-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```bash
# Build Docker image
docker build -t multithreading-demo .

# Run container
docker run -p 8080:8080 multithreading-demo
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📚 Learning Resources

### Java Concurrency Concepts
- [Oracle Java Concurrency Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Java Concurrency in Practice](https://jcip.net/) by Brian Goetz
- [Effective Java](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/) by Joshua Bloch

### Spring Boot Resources
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Boot Guides](https://spring.io/guides)

## 🐛 Troubleshooting

### Common Issues

#### Application Won't Start
- **Check Java Version**: Ensure Java 17+ is installed
- **Check Port**: Ensure port 8080 is available
- **Check Dependencies**: Run `mvn clean install`

#### Demos Not Working
- **Check Logs**: Look at console output for errors
- **Check Browser Console**: Look for JavaScript errors
- **Check Network**: Ensure API calls are reaching the server

#### Performance Issues
- **Reduce Thread Count**: Lower the number of threads in demos
- **Check System Resources**: Ensure adequate CPU and memory

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Java community for concurrency best practices
- Bootstrap team for the UI framework
- All contributors and testers

## 📞 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Search existing issues in the repository
3. Create a new issue with detailed information
4. Contact the maintainers

---

**Happy Learning! 🎓**

This project is designed to help developers understand Java concurrency concepts through hands-on experience. Each demo provides real-world examples of common concurrency patterns and synchronization mechanisms.
