# E-commerce Frontend

This is a monorepo containing both the User App and Admin Panel for the e-commerce application.

## Structure

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

## Getting Started

### Prerequisites
- Node.js >= 18.0.0
- npm >= 9.0.0

### Installation

```bash
# Install all dependencies
npm install

# Start development servers
npm run dev:user    # User app on http://localhost:5173
npm run dev:admin   # Admin app on http://localhost:5174
```

### Available Scripts

- `npm run dev:user` - Start user app development server
- `npm run dev:admin` - Start admin app development server
- `npm run build:user` - Build user app for production
- `npm run build:admin` - Build admin app for production
- `npm run build` - Build both apps
- `npm run lint` - Run ESLint on all workspaces
- `npm run lint:fix` - Fix ESLint issues
- `npm run format` - Format code with Prettier
- `npm run format:check` - Check code formatting

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
