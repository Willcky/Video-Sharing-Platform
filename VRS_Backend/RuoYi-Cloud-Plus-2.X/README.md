# Video Sharing Platform

A distributed video sharing platform built with Spring Cloud and modern technologies.

## Architecture Overview

### Microservices
- **vrs-video**: Core video service for video processing and management
- **vrs-user-interaction**: User interaction service for likes, comments, and social features
- **ruoyi-resource**: Resource management service for file storage and CDN
- Other supporting microservices from RuoYi-Cloud framework

### Key Features

#### Video Processing
- Video upload and transcoding using FFmpeg
- Multi-quality video transcoding (480p, 720p, 1080p)
- HLS (HTTP Live Streaming) support
- Asynchronous video processing pipeline
- Video thumbnail generation

#### Storage & CDN
- Local storage with configurable paths
- Redis caching for hot data
- Support for multiple storage backends (extensible)

#### Real-time Features
- Real-time view count tracking with Kafka
- Distributed message processing
- Deduplication using Redis
- Thread-safe concurrent processing

#### User Interaction
- Like/Dislike functionality
- Comment system
- User action tracking
- Social features

### Technology Stack

#### Backend
- **Framework**: Spring Cloud, Spring Boot
- **Database**: MySQL with MyBatis-Plus
- **Cache**: Redis with Redisson
- **Message Queue**: Apache Kafka
- **Service Discovery**: Nacos
- **Video Processing**: FFmpeg
- **Build Tool**: Maven

#### Storage
- Redis for caching and real-time data
- MySQL for persistent storage
- Local file system for video storage

#### Message Processing
- Kafka for event streaming
- Redis Streams for video processing events
- Distributed message deduplication

### Key Components

#### Video Processing Pipeline
```
Upload → Transcoding → Storage → CDN Distribution
```

#### View Count System
- Real-time view count updates using Kafka
- In-memory aggregation with thread-safe implementation
- Periodic database synchronization
- Redis-based deduplication

#### Configuration Highlights
- Multi-quality video transcoding
- Configurable FFmpeg settings
- Kafka consumer group settings
- Redis caching strategies

### Development

#### Prerequisites
- JDK 17+
- Maven 3.8+
- Redis 6+
- MySQL 8+
- Kafka 3.6+
- FFmpeg 7.1+

#### Configuration Files
- `application.yml`: Main application configuration
- `bootstrap.yml`: Spring Cloud configuration
- Nacos for distributed configuration

### Deployment

#### Environment Requirements
- Minimum 4GB RAM
- FFmpeg installation
- Kafka cluster setup
- Redis cluster setup
- MySQL database setup

#### Key Configurations
```yaml
# FFmpeg Configuration
ffmpeg:
  ffmpeg-path: /path/to/ffmpeg
  ffprobe-path: /path/to/ffprobe
  output-path: /path/to/output
  hls-time: 10
  video-bitrates: [800, 1400, 2800]
  video-resolutions: [480, 720, 1080]

# Kafka Configuration
spring:
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: video-view-count-group
      enable-auto-commit: false
    producer:
      retries: 3
      acks: all
```

### Performance Optimizations
- Thread-safe view count aggregation
- Redis-based caching
- Message deduplication
- Batch database updates
- Distributed locking for concurrent operations

### Security Features
- Distributed session management
- API authentication and authorization
- Resource access control
- Rate limiting

### Monitoring & Maintenance
- Log aggregation
- Performance metrics
- Health checks
- Error tracking

## License
This project is licensed under the MIT License. 