Infra module for local development

This module provides a ready-to-run local infrastructure using Docker Compose with:
- OpenSearch (single-node, security disabled)
- OpenSearch Dashboards (UI)
- Kafka (single-node KRaft)
- Kafka UI (web console)
- PostgreSQL 16 (database)

Prerequisites
- Docker Desktop 4.0+ (or Docker Engine + Docker Compose v2)
- Java 17+
- Maven 3.8+

Quick start
1) Build the project JARs (optional for infra; required if you plan to run services from packaged JARs):
   mvn -DskipTests package

2) Start infra only (OpenSearch + Kafka + UIs):
   cd infra
   docker compose up -d

3) Verify infra is healthy:
   - OpenSearch API: curl http://localhost:9200
   - OpenSearch Dashboards: http://localhost:5601
   - Kafka UI: http://localhost:8085
   - Kafka broker (host access): localhost:29092
   - PostgreSQL: psql -h localhost -p 5432 -U ai -d ai_platform (password: ai_password)

How application services connect
- OpenSearch is exposed on localhost:9200 for the host machine.
- Kafka is exposed on localhost:29092 for the host machine.
- PostgreSQL is exposed on localhost:5432 for the host machine.

Suggested Spring properties (adjust per service):
- spring.kafka.bootstrap-servers=localhost:29092
- spring.datasource.url=jdbc:postgresql://localhost:5432/ai_platform
- spring.datasource.username=ai
- spring.datasource.password=ai_password
- spring.jpa.hibernate.ddl-auto=update (optional for JPA apps)

Current application code (common-lib OpenSearchConfig) connects to "http://localhost:9200". To keep changes minimal, run the Spring Boot services on your host machine (not in containers), so they can reach OpenSearch, Kafka, and Postgres via the published host ports.


Run all services (host processes)
Open separate terminals and launch each service. If needed, give them unique ports with -Dserver.port.

1) API Gateway (default 8080)
   cd api-gateway
   mvn spring-boot:run
   # or choose a port: mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

2) Query Service (example 8081)
   cd query-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

3) Embedding Service (example 8082)
   cd embedding-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

4) Document Ingestion Service (example 8083)
   cd document-ingestion-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083

5) AI Governance Service (example 8084)
   cd ai-governance-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8084

Environment hints (if your services expect them)
- Kafka bootstrap servers (from host): localhost:29092
  As Spring property: --spring.kafka.bootstrap-servers=localhost:29092
- OpenSearch (already hard-coded as localhost:9200 in common-lib at time of writing)

End-to-end smoke test
1) Ensure infra is running: docker compose ps
2) Start at least the Embedding Service and Query Service (ports 8082 and 8081 in the examples above).
3) Test OpenSearch directly:
   curl -s http://localhost:9200 | jq .
   # You should see cluster_name and other info.
4) Use Kafka UI at http://localhost:8085 to create a topic (e.g., platform.events) if your services expect one.
5) Hit the API Gateway (adjust port if you changed it). Example placeholder:
   curl -i http://localhost:8080/actuator/health
   # Expect HTTP 200 from Spring Boot actuator if enabled.

Stopping and cleanup
- Stop infra: cd infra && docker compose down
- Stop and remove with volumes (if volumes are added later): docker compose down -v

Troubleshooting
- Ports in use: Change host ports in docker-compose.yml or service ports via --server.port.
- OpenSearch takes time to start: Services that depend on it might fail on the first attempt; just retry after opensearch is healthy.
- Kafka client connection from host must use localhost:29092 (not 9092). Inside-Docker clients should use kafka:9092.

Notes
- The compose file intentionally does not run application services in containers to avoid changing code that currently assumes localhost for OpenSearch. If you want full containerized services later, update the app configs to use service DNS names (opensearch, kafka) instead of localhost and add services to docker-compose.
