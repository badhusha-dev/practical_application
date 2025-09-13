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

## Tech Stack

### User App
- React 18 + Vite
- React Router
- Redux Toolkit + RTK Query
- Bootstrap 5
- Formik + Yup
- Stripe React SDK
- Firebase Web SDK
- Socket.IO Client
- Framer Motion

### Admin App
- React 18 + Vite
- React Admin
- Ant Design
- Recharts
- Three.js
- Redux Toolkit + RTK Query
- Socket.IO Client

## Development

Each app can be developed independently:

```bash
# Work on user app
cd apps/user-app
npm run dev

# Work on admin app
cd apps/admin-app
npm run dev
```

## API Integration

Both apps connect to the microservices backend through the API Gateway:
- Base URL: `http://localhost:8080`
- Authentication: JWT tokens
- Real-time updates: Socket.IO
