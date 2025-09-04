# Docker Compose Guide

This project includes multiple Docker Compose configurations for different purposes. Each configuration serves a specific use case and has different characteristics.

## Available Configurations

### 1. Production Environment (`docker-compose.yml`)

**Purpose**: Run the complete DreamRoute application with database for production or development.

**Command**:
```bash
docker compose up --build
```

**What it does**:
- Builds the SpringBoot application from `Dockerfile`
- Starts a MySQL 8.0 database with persistent storage
- Exposes the application on port 8080
- Exposes the database on port 3306 for external access
- Uses `restart: unless-stopped` policy for high availability
- Includes health checks for both services
- Creates a custom network for service communication

**Key differences**:
- Full production-ready setup with persistent data volumes
- Includes port mappings for external access
- Automatic restart policies
- Health checks and service dependencies

### 2. Testing with Custom Dockerfile (`docker-compose-test-with-dockerfile.yml`)

**Purpose**: Run tests using a custom test-specific Dockerfile.

**Command**:
```bash
docker compose --file docker-compose-test-with-dockerfile.yml run --rm dreamroute-test
```

**What it does**:
- Builds a custom test image from `Dockerfile-test`
- Starts a test database (no external port exposure)
- Runs the test suite within the custom container
- Automatically removes containers after execution

**Key differences**:
- Uses custom `Dockerfile-test` for test environment
- Allows for test-specific dependencies and configurations
- More control over the test environment
- `restart: never` policy (single-run containers)
- No external database port exposure

### 3. Testing with Maven Image (`docker-compose-test.yml`)

**Purpose**: Run tests using the official Maven Docker image without building a custom image.

**Command**:
```bash
docker compose --file docker-compose-test.yml run --rm dreamroute-test
```

**What it does**:
- Uses the official `maven:3.9.11-eclipse-temurin-24` image
- Mounts the source code as a read-only volume
- Runs `mvn -Ptest clean verify` with test profile to execute tests
- Uses test profile to avoid building/writing JAR files to source directory
- Starts a test database (no external port exposure)
- Automatically removes containers after execution (`--rm` flag)

**Key differences**:
- No custom Docker image building required
- Faster startup (uses pre-built Maven image)
- Read-only source code mounting (safe because test profile doesn't write JAR)
- Test profile prevents artifact generation in source directory
- `restart: never` policy (single-run containers)
- No external database port exposure
- Ideal for CI/CD pipelines

## Quick Reference

| Configuration | Use Case | Database Port | App Port | Restart Policy | Image Source |
|---------------|----------|---------------|----------|----------------|--------------|
| `docker-compose.yml` | Production/Development | 3306 | 8080 | unless-stopped | Custom Dockerfile |
| `docker-compose-test-with-dockerfile.yml` | Testing (Custom) | None | None | never | Custom Dockerfile-test |
| `docker-compose-test.yml` | Testing (Maven) | None | None | never | Maven official image |

## Environment Variables

All configurations use the same environment variables:
- `SPRING_PROFILES_ACTIVE=docker`
- `SERVER_PORT=8080`
- `DB_URL=jdbc:mysql://[service-name]:3306/dreamroute`
- `DB_USERNAME=dreamroute`
- `DB_PASSWORD=dreamroute123`

## Notes

- Test configurations use `--rm` flag to automatically clean up containers
- Production configuration includes persistent MySQL data volume
- All configurations use the same database credentials for consistency
- Health checks ensure services are ready before dependent services start
