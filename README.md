# Video Sharing Platform

A modern, distributed video sharing platform built with React.js frontend and Spring Cloud microservices backend.

## 🌟 System Overview

This project consists of two main components:
- **Frontend**: A modern React.js application providing the user interface
- **Backend**: A distributed microservices architecture built with Spring Cloud

### Key Features

- Video upload, playback, and processing
- Multi-quality video transcoding (480p, 720p, 1080p)
- Real-time video recommendations
- User authentication and profiles
- Social features (likes, comments, sharing)
- Responsive design
- Search functionality
- Real-time view count tracking
- HLS (HTTP Live Streaming) support

## 🏗️ Architecture

### Frontend (wiliwili)

#### Technologies
- React.js 18
- Redux Toolkit
- Material-UI
- React Router v6
- Axios
- React Player

#### Structure
```
frontend/
├── src/
│   ├── components/     # Reusable components
│   ├── services/       # API services
│   ├── store/         # Redux store
│   ├── utils/         # Utilities
│   ├── hooks/         # Custom hooks
│   ├── context/       # React context
│   ├── assets/        # Static assets
│   └── pages/         # Page components
```

### Backend (Microservices)

#### Core Services
- **vrs-video**: Video processing and management
- **vrs-user-interaction**: User interactions and social features
- **ruoyi-resource**: Resource and CDN management

#### Technology Stack
- Spring Cloud & Spring Boot
- MySQL with MyBatis-Plus
- Redis with Redisson
- Apache Kafka
- Nacos Service Discovery
- FFmpeg for video processing

## 🚀 Getting Started

### Frontend Setup

1. Clone the repository and navigate to frontend:
```bash
git clone <repository-url>
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment:
```bash
REACT_APP_API_BASE_URL=your_api_url
```

4. Start development server:
```bash
npm start
```

### Backend Setup

1. Configure your development environment:
- JDK 17
- Maven
- MySQL
- Redis
- FFmpeg

2. Configure application properties for each service

3. Start the required services:
- Nacos
- MySQL
- Redis
- Kafka

4. Launch the microservices in order:
- Gateway
- Auth Service
- Video Service
- User Interaction Service

## 🔧 Configuration

### Frontend
- ESLint for code linting
- Prettier for code formatting
- Husky for pre-commit hooks

### Backend
- Application properties for each service
- FFmpeg settings for video processing
- Kafka consumer group settings
- Redis caching strategies

## 🔒 Security

- Distributed session management
- API authentication and authorization
- Resource access control
- Rate limiting

## 📊 Monitoring

- Log aggregation
- Performance metrics
- Health checks
- Error tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Open a pull request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- K.C 

## 🙏 Acknowledgments

- YouTube for inspiration
- React.js community
- Spring Cloud community
- RuoYi framework team 