# GitHub Actions Workflows

This directory contains the CI/CD workflows for the FF5 DreamRoute project. These workflows automate testing, building, and releasing the application using Docker containers.

## Overview

The project uses three main workflows to ensure code quality and automate deployments:

### 🧪 [test.yml](./test.yml) - Pull Request Testing
- **Trigger**: Pull requests targeting the `main` branch
- **Purpose**: Ensures code quality by running automated tests before merging
- **Actions**: 
  - Runs test suite in Docker containers using `docker-compose-test.yml`
  - Validates that new changes don't break existing functionality
  - Automatically cleans up Docker resources after testing

### 🏗️ [build.yml](./build.yml) - Continuous Integration
- **Trigger**: Pushes to the `main` branch
- **Purpose**: Automatically builds and publishes development Docker images
- **Actions**:
  - Builds Docker image from latest main branch code
  - Pushes images to Docker Hub with branch and SHA tags
  - Uses GitHub Actions cache for faster subsequent builds

### 🚀 [release.yml](./release.yml) - Production Releases
- **Trigger**: Version tags pushed to repository (format: `v*`)
- **Purpose**: Creates production releases with proper versioning
- **Actions**:
  - Runs full test suite to ensure release quality
  - Builds and pushes Docker images with version tags
  - Tags image as `latest` for easy access
  - Includes commented section for GitHub release creation

## Workflow Dependencies

### Required Secrets
These secrets must be configured in the GitHub repository settings:

- `DOCKER_USERNAME`: Docker Hub username for image publishing
- `DOCKER_PASSWORD`: Docker Hub password or access token

### Required Files
- `Dockerfile`: Container definition for the application
- `docker-compose-test.yml`: Test environment configuration

## Docker Image Tags

The workflows create different image tags based on the trigger:

| Workflow | Tags Created | Example |
|----------|-------------|---------|
| **Build** | `main`, `sha-<commit>` | `ff5-dreamroute:main`, `ff5-dreamroute:sha-abc123` |
| **Release** | `<version>`, `latest` | `ff5-dreamroute:v1.0.0`, `ff5-dreamroute:latest` |

## Usage Examples

### Creating a Release
1. Tag your commit with a version number:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. The release workflow will automatically:
   - Run tests
   - Build production image
   - Push to Docker Hub with version tag

### Running Tests Locally
To run the same tests that execute in CI:
```bash
docker compose -f docker-compose-test.yml up --abort-on-container-exit
```

### Pulling Images
```bash
# Latest development build
docker pull <username>/ff5-dreamroute:main

# Specific release
docker pull <username>/ff5-dreamroute:v1.0.0

# Latest stable release
docker pull <username>/ff5-dreamroute:latest
```

## Workflow Features

### 🚀 Performance Optimizations
- **GitHub Actions Cache**: Speeds up Docker builds by caching layers
- **Quiet Pull**: Reduces log noise during image downloads
- **Parallel Execution**: Steps run concurrently where possible

### 🛡️ Safety Features
- **Test Gating**: Release workflow fails if tests don't pass
- **Resource Cleanup**: Always cleans up Docker resources, even on failure
- **Exit Code Propagation**: Proper error handling and status reporting

### 🔧 Maintenance
- **Modern Actions**: Uses latest versions of GitHub Actions
- **Docker Buildx**: Enables advanced Docker features and multi-platform builds
- **Metadata Extraction**: Automatic tag and label generation

## Troubleshooting

### Common Issues

**Workflow fails with authentication error:**
- Verify `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets are correctly set
- Ensure Docker Hub credentials have push permissions

**Tests fail in CI but pass locally:**
- Check that `docker-compose-test.yml` matches your local test setup
- Verify all required environment variables are available in CI

**Build fails with cache issues:**
- GitHub Actions cache may be corrupted; clear cache and retry
- Check that Dockerfile and build context are correct

### Monitoring
- View workflow runs in the GitHub Actions tab
- Check Docker Hub for published images
- Monitor resource usage and build times for optimization opportunities