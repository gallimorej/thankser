# Use the official Clojure image as the base image
FROM clojure:openjdk-11-lein

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . /app

# Install dependencies and compile the project
RUN lein uberjar

# Expose the port the app runs on
EXPOSE 3000

# Command to run the application
CMD ["java", "-cp", "target/thankser.jar", "clojure.main", "-m", "thankser.web"]