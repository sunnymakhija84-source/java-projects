Infra module for local development for ai

This module provides a ready-to-run local infrastructure using Docker Compose with:
- OpenSearch (single-node, security disabled)
- OpenSearch Dashboards (UI)
- Kafka (single-node KRaft)
- Kafka UI (web console)
- PostgreSQL 16 (database)
- Redis 7 (for caching where used)
- Application services (containerized):
  - api-gateway (8080)
  - document-ingestion-service (8081)
  - embedding-service (8082)
  - query-service (8083)
  - ai-governance-service (8084)

Prerequisites
- Docker Desktop 4.0+ (or Docker Engine + Docker Compose v2)
- Java 21+
- Maven 3.8+

Quick start (run full stack in Docker)
1) Build the project JARs so images can copy them:
   mvn -DskipTests package

2) Start everything (infra + app services):
   cd infra
   docker compose up -d --build

3) Verify infra and apps:
   - OpenSearch API: curl http://localhost:9200
   - OpenSearch Dashboards: http://localhost:5601
   - Kafka UI: http://localhost:8085
   - Kafka broker (host access): localhost:29092
   - PostgreSQL: psql -h localhost -p 5432 -U ai -d ai_platform (password: ai_password)
   - API Gateway health: curl -i http://localhost:8080/actuator/health
   - Query Service health: curl -i http://localhost:8083/actuator/health

Configuration and connectivity
- Inside Docker, services resolve each other by service name on network ai-net.
- Kafka for in-Docker clients: kafka:9092 (compose sets KAFKA_SERVERS=kafka:9092).
- PostgreSQL for in-Docker clients: jdbc:postgresql://postgres:5432/ai_platform (DB_USER=ai, DB_PASS=ai_password).
- OpenSearch for in-Docker clients: http://opensearch:9200. The shared common-lib now reads OPENSEARCH_HOST/PORT.
- Redis host in Docker: redis:6379.
- Eureka is disabled in containers via SPRING_APPLICATION_JSON to simplify local runs.

Environment overrides
- You can override defaults by editing infra/docker-compose.yml or setting env vars.
  Common ones per service:
  - DB_URL, DB_USER, DB_PASS
  - KAFKA_SERVERS
  - OPENSEARCH_HOST, OPENSEARCH_PORT
  - REDIS_HOST, REDIS_PORT (where applicable)
  - EMBEDDING_SERVICE_URL for query-service (defaults to http://embedding-service:8082)

Alternative: run services on host
- You can still run services via mvn spring-boot:run and use the infra started by compose.
- Suggested Spring properties for host processes:
  - spring.kafka.bootstrap-servers=localhost:29092
  - spring.datasource.url=jdbc:postgresql://localhost:5432/ai_platform
  - spring.datasource.username=ai
  - spring.datasource.password=ai_password

End-to-end smoke test
1) Ensure stack is running: docker compose ps
2) Test OpenSearch directly:
   curl -s http://localhost:9200
3) Produce/consume sample messages using Kafka UI at http://localhost:8085 (optional).
4) Call API Gateway health:
   curl -i http://localhost:8080/actuator/health
5) Query-service should reach embedding-service at http://embedding-service:8082 inside the network.

Stopping and cleanup
- Stop: cd infra && docker compose down
- Remove with volumes: docker compose down -v

Troubleshooting
- Ports in use: Adjust published ports in docker-compose.yml.
- OpenSearch startup time: Dependent services may fail on first try; compose will keep them running—retry after healthy.
- If images fail to start due to missing JARs, ensure you ran: mvn -DskipTests package at repository root before compose build.
