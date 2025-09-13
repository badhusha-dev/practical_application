# 🚀 Practical Projects Collection

This repository contains a collection of practical, production-ready applications demonstrating modern web development technologies and best practices.

## 📁 Projects Overview

| Project | Technology Stack | Description | Status |
|---------|------------------|-------------|--------|
| **🛒 E-commerce Frontend** | React 18, Vite, Redux Toolkit | Full-stack e-commerce application with user and admin interfaces | ✅ Complete |
| **👥 Thymeleaf MongoDB App** | Spring Boot, MongoDB, Thymeleaf | Modern web application with server-side rendering and NoSQL database | ✅ Complete |

---

## 🛒 E-commerce Frontend

A comprehensive monorepo containing both the User App and Admin Panel for a modern e-commerce application.

### 📁 Project Structure

```
frontend/
├── apps/
│   ├── user-app/          # User-facing React application
│   └── admin-app/         # Admin dashboard React application
├── packages/
│   └── shared/            # Shared components and utilities
├── package.json           # Root package.json with workspace configuration
└── README.md
```

### 🚀 Quick Start

#### Prerequisites
- ☕ Node.js >= 18.0.0
- 📦 npm >= 9.0.0

#### Installation & Running
```bash
# Install all dependencies
npm install

# Start development servers
npm run dev:user    # User app on http://localhost:5173
npm run dev:admin   # Admin app on http://localhost:5174
```

### 📜 Available Scripts

| Command | Description |
|---------|-------------|
| `npm run dev:user` | Start user app development server |
| `npm run dev:admin` | Start admin app development server |
| `npm run build:user` | Build user app for production |
| `npm run build:admin` | Build admin app for production |
| `npm run build` | Build both apps |
| `npm run lint` | Run ESLint on all workspaces |
| `npm run lint:fix` | Fix ESLint issues |
| `npm run format` | Format code with Prettier |
| `npm run format:check` | Check code formatting |

### 🛠️ Technology Stack

#### 👤 User App
| Technology | Purpose |
|------------|---------|
| React 18 + Vite | Frontend framework and build tool |
| React Router | Client-side routing |
| Redux Toolkit + RTK Query | State management and API calls |
| Bootstrap 5 | CSS framework |
| Formik + Yup | Form handling and validation |
| Stripe React SDK | Payment processing |
| Firebase Web SDK | Authentication and real-time features |
| Socket.IO Client | Real-time communication |
| Framer Motion | Animations |

#### 👨‍💼 Admin App
| Technology | Purpose |
|------------|---------|
| React 18 + Vite | Frontend framework and build tool |
| React Admin | Admin interface framework |
| Ant Design | UI component library |
| Recharts | Data visualization |
| Three.js | 3D graphics |
| Redux Toolkit + RTK Query | State management and API calls |
| Socket.IO Client | Real-time communication |

### 🔧 Development

Each app can be developed independently:

```bash
# Work on user app
cd apps/user-app
npm run dev

# Work on admin app
cd apps/admin-app
npm run dev
```

### 🔗 API Integration

Both apps connect to the microservices backend through the API Gateway:
- **Base URL**: `http://localhost:8080`
- **Authentication**: JWT tokens
- **Real-time updates**: Socket.IO

---

## 👥 Thymeleaf MongoDB App

A complete **production-ready Spring Boot application** demonstrating modern web development with Thymeleaf, MongoDB, and Mongock migrations.

### 📁 Project Structure

```
thymeleaf-mongo-flyway-app/
├── src/main/java/com/example/thymeleafmongoflywayapp/
│   ├── config/                    # Application configuration
│   ├── controller/                # REST controllers
│   ├── entity/                    # MongoDB entities
│   ├── migration/                 # Database migrations
│   ├── repository/                # Data repositories
│   ├── service/                   # Business logic
│   └── ThymeleafMongoFlywayAppApplication.java
├── src/main/resources/
│   ├── static/css/                # Custom CSS
│   ├── static/js/                 # Custom JavaScript
│   ├── templates/                 # Thymeleaf templates
│   └── application.yml            # Configuration
└── pom.xml                        # Maven dependencies
```

### 🚀 Quick Start

#### Prerequisites
- ☕ Java 17+
- 🔨 Maven 3.6+
- 🍃 MongoDB 4.4+

#### Installation & Running
```bash
# Navigate to project directory
cd thymeleaf-mongo-flyway-app

# Install dependencies
mvn clean install

# Start MongoDB (if not running)
mongod

# Run application
mvn spring-boot:run
```

#### Access Points
| Page | URL | Description |
|------|-----|-------------|
| 🏠 **Home** | http://localhost:8080 | Main dashboard |
| 📊 **Dashboard** | http://localhost:8080/dashboard | Statistics and overview |
| 👥 **Users** | http://localhost:8080/users | User management |
| ➕ **Add User** | http://localhost:8080/users/new | Create new user |

### 🛠️ Technology Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Backend** | Spring Boot | 3.2.0 | Application framework |
| | Spring Data MongoDB | 3.2.0 | Database operations |
| | Spring Web MVC | 3.2.0 | Web layer |
| **Frontend** | Thymeleaf | 3.1+ | Server-side templating |
| | Bootstrap 5 | 5.3.0 | CSS framework |
| | Tailwind CSS | Latest | Utility-first CSS |
| | Bootstrap Icons | 1.10.0 | Icon library |
| **Database** | MongoDB | 4.4+ | NoSQL database |
| **Migrations** | Mongock | 5.3.2 | Database versioning |
| **Build Tool** | Maven | 3.6+ | Dependency management |
| **Validation** | Hibernate Validator | 8.0+ | Input validation |
| **Utilities** | Lombok | 1.18.30 | Boilerplate reduction |

### ✨ Key Features

- 🎯 **Modern UI**: Bootstrap 5 + Tailwind CSS with responsive design
- 🔍 **Advanced Search**: Search by name or email with pagination
- ✅ **Form Validation**: Client and server-side validation
- 📊 **Dashboard**: Statistics and system overview
- 🗄️ **Database Migrations**: Mongock for schema management
- 📱 **Mobile Responsive**: Works on all devices
- 🚀 **Production Ready**: Comprehensive error handling and logging

### 🎯 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Dashboard home |
| `GET` | `/dashboard` | Dashboard with stats |
| `GET` | `/users` | List users with pagination/search |
| `GET` | `/users/new` | Show add user form |
| `POST` | `/users/save` | Save new user |
| `GET` | `/users/edit/{id}` | Show edit user form |
| `POST` | `/users/update/{id}` | Update existing user |
| `GET` | `/users/delete/{id}` | Delete user |
| `GET` | `/users/view/{id}` | View user details |

---

## 🎯 Project Comparison

| Feature | E-commerce Frontend | Thymeleaf MongoDB App |
|---------|-------------------|----------------------|
| **Architecture** | Client-side SPA | Server-side rendering |
| **Frontend** | React 18 + Vite | Thymeleaf + Bootstrap |
| **State Management** | Redux Toolkit | Server-side sessions |
| **Database** | Microservices backend | MongoDB direct |
| **Real-time** | Socket.IO | Traditional HTTP |
| **Deployment** | Static hosting | JAR deployment |
| **Use Case** | Complex SPA | Traditional web app |

---

## 🚀 Getting Started with Any Project

### 1. Choose Your Project
- **Frontend Development**: Start with the E-commerce Frontend
- **Backend Development**: Start with the Thymeleaf MongoDB App
- **Full-stack Learning**: Explore both projects

### 2. Prerequisites
- **For E-commerce**: Node.js 18+, npm 9+
- **For Thymeleaf App**: Java 17+, Maven 3.6+, MongoDB 4.4+

### 3. Quick Setup
```bash
# Clone the repository
git clone <repository-url>
cd practical

# For E-commerce Frontend
cd frontend
npm install
npm run dev:user  # or npm run dev:admin

# For Thymeleaf MongoDB App
cd thymeleaf-mongo-flyway-app
mvn clean install
mvn spring-boot:run
```

---

## 📚 Learning Resources

### 🛒 E-commerce Frontend
- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
- [Bootstrap Documentation](https://getbootstrap.com/docs/)

### 👥 Thymeleaf MongoDB App
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Maven Documentation](https://maven.apache.org/guides/)

---

## 🤝 Contributing

We welcome contributions to any project! Here's how you can help:

1. **🍴 Fork** the repository
2. **🌿 Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **💾 Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **📤 Push** to the branch (`git push origin feature/amazing-feature`)
5. **🔄 Open** a Pull Request

### 📋 Contribution Guidelines
- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass
- Use meaningful commit messages

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**🚀 Built with ❤️ using modern web technologies**

[![Star](https://img.shields.io/github/stars/username/practical?style=social)](https://github.com/username/practical)
[![Fork](https://img.shields.io/github/forks/username/practical?style=social)](https://github.com/username/practical/fork)
[![Issues](https://img.shields.io/github/issues/username/practical)](https://github.com/username/practical/issues)

</div>
