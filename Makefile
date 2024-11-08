# Include environment variables
ENV_FILE = $(PWD)/.env
include $(ENV_FILE)

# Check for required environment variables
.PHONY: check-env
check-env:
	@if [ -z "$(DOCKER_IMAGE)" ]; then echo "DOCKER_IMAGE is not set"; exit 1; fi
	@if [ -z "$(DOCKER_CONTAINER)" ]; then echo "DOCKER_CONTAINER is not set"; exit 1; fi
	@if [ -z "$(LOCAL_PORT)" ]; then echo "LOCAL_PORT is not set"; exit 1; fi
	@if [ -z "$(PROJECT_ID)" ]; then echo "PROJECT_ID is not set"; exit 1; fi
	@if [ -z "$(APP_NAME)" ]; then echo "APP_NAME is not set"; exit 1; fi
	@if [ -z "$(REGION)" ]; then echo "REGION is not set"; exit 1; fi
#	@if [ -z "$(SLACK_TOKEN)" ]; then echo "SLACK_TOKEN is not set"; exit 1; fi
	@if [ -z "$(SERVICE_ACCOUNT)" ]; then echo "SERVICE_ACCOUNT is not set"; exit 1; fi

# Variables
NODE_MODULES_DIR = node_modules
DIST_DIR = dist
DOCKERFILE = Dockerfile

# Default target
.PHONY: all
all: install run

# Install dependencies
.PHONY: install
install:
	npm install

# Run the application
.PHONY: run
run:
	node server.js

# Clean up node modules and other generated files
.PHONY: clean
clean:
	rm -rf $(NODE_MODULES_DIR)
	rm -rf $(DIST_DIR)

# Run the application in Docker
.PHONY: build-docker
build-docker: check-env
	docker build -t $(DOCKER_IMAGE) -f $(DOCKERFILE) .

.PHONY: run-docker
run-docker: build-docker
	docker run --env-file $(ENV_FILE) -p $(LOCAL_PORT):$(LOCAL_PORT) --name $(DOCKER_CONTAINER) $(DOCKER_IMAGE)

# Stop and remove the Docker container
.PHONY: clean-docker
clean-docker:
	docker stop $(DOCKER_CONTAINER) || true
	docker rm $(DOCKER_CONTAINER) || true

# Enable Cloud Build API
.PHONY: enable-cloudbuild-api
enable-cloudbuild-api:
	gcloud services enable cloudbuild.googleapis.com

# Grant Storage Object Viewer role to the service account
.PHONY: grant-storage-permissions
grant-storage-permissions:
	gsutil iam ch serviceAccount:$(SERVICE_ACCOUNT):roles/storage.objectViewer gs://$(PROJECT_ID)_cloudbuild

# Build the Docker image for Google Cloud Run
.PHONY: build-cloud
build-cloud: enable-cloudbuild-api grant-storage-permissions
	gcloud builds submit --tag gcr.io/$(PROJECT_ID)/$(APP_NAME)

# Deploy to Google Cloud Run
.PHONY: deploy-cloud
deploy-cloud:
	gcloud run deploy $(APP_NAME) --image gcr.io/$(PROJECT_ID)/$(APP_NAME) --platform managed --region $(REGION) --allow-unauthenticated --set-env-vars SLACK_TOKEN=$(SLACK_TOKEN)

# Retrieve the service URL
.PHONY: get-url
get-url:
	@echo "Service URL:"
	@gcloud run services describe $(APP_NAME) --platform managed --region $(REGION) --format "value(status.url)"

# Clean up local Docker images
.PHONY: clean-cloud
clean-cloud:
	docker rmi gcr.io/$(PROJECT_ID)/$(APP_NAME)