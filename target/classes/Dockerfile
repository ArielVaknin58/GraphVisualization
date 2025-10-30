#
# STAGE 1: Build the Application
#
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /project

# Install wget and unzip to download the JavaFX SDK
RUN apt-get update && apt-get install -y wget unzip

# Download and unzip the correct JavaFX SDK for Linux x64
ENV FX_VERSION 22.0.2
ENV FX_URL https://download2.gluonhq.com/openjfx/${FX_VERSION}/openjfx-${FX_VERSION}_linux-x64_bin-sdk.zip

RUN wget ${FX_URL} -O javafx-sdk.zip && \
    unzip javafx-sdk.zip && \
    rm javafx-sdk.zip

# Copy your project's source code
COPY src ./src

# Find all .java files and write them to a file
RUN find src -name "*.java" > sources.txt

# Create the output directory
RUN mkdir out

# Compile the Java code, referencing the DOWNLOADED Linux JavaFX modules
RUN javac -d out --module-path javafx-sdk-${FX_VERSION}/lib \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web \
    @sources.txt

# Copy non-Java resource files (e.g., .fxml, .css, .png) to the output directory
RUN cd src && find . -type f -not -name "*.java" -exec cp --parents {} ../out/ \;

#
# STAGE 2: Create the Final Runtime Image
#
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Install all the Linux GUI libraries we've identified
RUN apt-get update && apt-get install -y --no-install-recommends \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgtk-3-0 \
    libgtk-3-dev \
    libgtk2.0-0 \
    libxt-dev \
    fonts-liberation \
    libfontconfig1 \
    libgl1 \
    libgdk-pixbuf-2.0-0 \
    libglu1-mesa \
    libxslt1.1 \
    && rm -rf /var/lib/apt/lists/*

# Copy the compiled classes and resources from the builder stage
COPY --from=builder /project/out .

# Copy the entire DOWNLOADED Linux JavaFX SDK from the builder stage
# This includes the all-important .so native libraries
COPY --from=builder /project/javafx-sdk-22.0.2 ./javafx-sdk-22.0.2

# Set a default display
ENV DISPLAY=:0

# Run the application, forcing software rendering and pointing to the correct SDK
CMD ["java", \
     "-Dprism.order=sw", \
     "--module-path", "javafx-sdk-22.0.2/lib", \
     "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web", \
     "GraphVisualizer.GraphVisualizer"]