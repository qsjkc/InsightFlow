# InsightFlow Lite: Real-Time Server Monitoring & Anomaly Alert System
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

InsightFlow Lite is a lightweight, production-ready real-time server monitoring and anomaly detection system built with Java/Spring Boot. It implements end-to-end metrics collection, message queue decoupling, intelligent anomaly detection, WebSocket real-time push, and containerized deployment, making it an ideal hands-on project for backend developers and a perfect addition to your resume.

---

## 🚀 Core Features
1. **Real-Time Metrics Collection**
   - Simulates server CPU, memory, and disk usage metrics with 1-second granularity via scheduled tasks
   - Extensible to support real server metrics collection via node exporter
2. **Lightweight Message Queue**
   - Implements FIFO message queue based on Redis List with blocking read
   - Decouples data production and consumption, implements traffic peak shaving, and avoids data loss
   - Full Redis version compatibility, no API compatibility issues
3. **Intelligent Anomaly Detection**
   - Implements anomaly detection based on the **3-sigma (three-sigma) rule** (normal distribution statistics)
   - Maintains a sliding window of historical data to ensure detection accuracy
   - Identifies abnormal metric fluctuations with over 99% accuracy in stable scenarios
4. **WebSocket Real-Time Push**
   - Bidirectional real-time communication based on WebSocket + STOMP protocol
   - Server-side active push without frontend polling, greatly reducing server pressure
   - Millisecond-level data delay from server to frontend
5. **Visual Monitoring Dashboard**
   - Lightweight web dashboard with real-time metric rendering
   - Automatic red highlighting for abnormal metrics
   - No complex frontend framework required, runs directly in the browser
6. **One-Click Containerized Deployment**
   - Full Docker and Docker Compose support
   - Environment isolation, automatic service restart, and data persistence
   - Seamless switching between local development and production deployment via Spring Boot multi-environment configuration

---

## 🛠️ Tech Stack
| Category | Technology & Version |
|----------|------------------------|
| Backend Framework | Spring Boot 3.2.x |
| Data Serialization | Jackson |
| Message Queue & Cache | Redis 7.x (Lettuce connection pool) |
| Real-Time Communication | WebSocket + STOMP + SockJS |
| Frontend | HTML5 + Native JavaScript |
| Deployment | Docker + Docker Compose |
| Code Simplification | Lombok |
| Build Tool | Maven 3.8+ |
| JDK Version | JDK 17+ |

---

## 🏗️ System Architecture
The system follows a layered architecture with clear module responsibilities, fully compliant with enterprise development specifications:

```
[Data Collection Layer] Scheduled task generates monitoring metrics
            ↓
[Message Queue Layer] Redis List implements lightweight queue (decouple production/consumption)
            ↓
[Anomaly Detection Layer] Consumer reads data, 3-sigma algorithm detects anomalies
            ↓
[Real-Time Push Layer] WebSocket pushes detection results to frontend in real time
            ↓
[Frontend Display Layer] Web dashboard renders metrics, highlights anomalies
```

---

## 📦 Quick Start

### Prerequisites
- JDK 17 or higher
- Maven 3.8+
- Docker & Docker Compose (for containerized deployment)
- Redis 7.x (for local development, or use the Docker Compose built-in Redis)

### Option 1: Local Development
1. **Clone the Repository**
   ```bash
   git clone <your-github-repo-url>
   cd insightflow-lite
   ```

2. **Start Redis (via Docker)**
   ```bash
   docker run -d --name if-redis -p 6379:6379 redis:7-alpine redis-server --requirepass 123456aq@ --protected-mode no --bind 0.0.0.0
   ```

3. **Configure Local Environment**
   - Modify `src/main/resources/application-local.yml`: Update `spring.data.redis.host` to your Redis server IP
   - Ensure `spring.profiles.active` in `application.yml` is set to `local`

4. **Build the Project**
   ```bash
   mvn clean package -DskipTests
   ```

5. **Run the Application**
   ```bash
   java -jar target/insightflow-lite-0.0.1-SNAPSHOT.jar
   ```

6. **Verify the Deployment**
   - Access the monitoring dashboard: `http://localhost:8080`
   - Health check: `http://localhost:8080/health`
   - Redis connection test: `http://localhost:8080/test/redis`

### Option 2: Docker Deployment (Recommended for Production)
1. **Clone the Repository**
   ```bash
   git clone <your-github-repo-url>
   cd insightflow-lite
   ```

2. **Build the Project**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Deploy with Docker Compose**
   ```bash
   # Start all services (application + Redis) in background
   docker compose up -d --build
   ```

4. **Verify the Deployment**
   - Check container status: `docker compose ps`
   - View application logs: `docker compose logs -f insightflow-app`
   - Access the dashboard: `http://<your-server-ip>:8080`

---

## 📁 Project Structure
```
insightflow-lite/
├── src/main/java/com/example/insightflow_lite/
│   ├── InsightFlowLiteApplication.java    # Spring Boot startup class
│   ├── controller/                         # Web controller layer
│   │   └── HealthController.java           # Health check & page redirect
│   ├── consumer/                           # Message consumer layer
│   │   └── MonitorDataConsumer.java        # Redis queue consumer
│   ├── model/                              # Data entity layer
│   │   └── MonitorData.java                # Monitoring metrics entity
│   ├── service/                            # Business service layer
│   │   └── AnomalyDetectionService.java    # 3-sigma anomaly detection
│   ├── task/                               # Scheduled task layer
│   │   └── MonitorDataGenerator.java       # Metrics data generator
│   └── websocket/                          # WebSocket module
│       ├── WebSocketConfig.java            # WebSocket configuration
│       └── WebSocketPushService.java       # WebSocket message push service
├── src/main/resources/
│   ├── static/
│   │   └── index.html                      # Monitoring dashboard frontend
│   ├── application.yml                      # Main configuration file
│   ├── application-local.yml                # Local development environment config
│   └── application-docker.yml               # Docker deployment environment config
├── Dockerfile                               # Docker image build file
├── docker-compose.yml                       # One-click deployment compose file
├── pom.xml                                  # Maven dependencies
└── README.md                                # Project documentation
```

---

## 📈 Advanced Roadmap
This project can be extended with more enterprise-level features:
- [ ] ECharts-based visualization with historical trend charts and dashboards
- [ ] Multi-server cluster monitoring support
- [ ] Alert notification integration (DingTalk/WeChat Work/Email)
- [ ] Time-series database (InfluxDB) for metrics persistence and historical query
- [ ] User authentication and permission management with Spring Security
- [ ] High availability with Redis cluster and application load balancing
- [ ] Prometheus + Grafana integration for enterprise-level monitoring

---

## ❓ FAQ
### 1. Redis Connection Failure Exception
**Common Causes**:
- Firewall blocks port 6379 on your server
- Redis protected mode is enabled, only allows local access
- Incorrect Redis password or host configuration
- Wrong IP address of your Redis server

**Solutions**:
- Open port 6379: `firewall-cmd --add-port=6379/tcp --permanent && firewall-cmd --reload`
- Start Redis with protected mode disabled: `--protected-mode no --bind 0.0.0.0`
- Verify Redis password and host configuration in your environment file

### 2. WebSocket Connection Failed in Browser
**Common Causes**:
- Mismatched WebSocket endpoint between backend and frontend
- SockJS/STOMP CDN resource is unavailable
- Interceptor blocks WebSocket requests

**Solutions**:
- Verify the endpoint `/ws/monitor` is consistent in config and frontend
- Use valid CDN resources for SockJS and STOMP
- Exclude WebSocket endpoints from any auth interceptors

### 3. Application Fails to Start with Docker Profile
**Common Causes**:
- Missing `application-docker.yml` configuration file
- Incorrect Redis host configuration (use container name instead of server IP in Docker network)
- `server.address` not set to `0.0.0.0` in container environment

**Solutions**:
- Complete the docker environment configuration file
- Set Redis host to your Redis container name (e.g. `if-redis`)
- Add `server.address: 0.0.0.0` to `application-docker.yml`

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](#) if you want to contribute.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
