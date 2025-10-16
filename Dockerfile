# 1. Start from a base image that has Java 21 and includes 'apt-get'
FROM eclipse-temurin:21-jdk-jammy

# 2. Install the tools for the virtual screen (Xvfb) and VNC server
RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    x11vnc \
    && rm -rf /var/lib/apt/lists/*

# 3. Set up the display environment for the virtual screen
ENV DISPLAY=:99

# 4. Create a directory for the application
WORKDIR /app

# 5. Copy your application's JAR file into the container
COPY out/artifacts/GraphVisualizer_jar/GraphVisualizer.jar .

# 6. Copy the JavaFX SDK's 'lib' folder into the container
#    This assumes your 'lib' folder is in your project's root.
COPY lib/javafx-sdk-22.0.2/lib ./jfx-libs/

# 7. Expose the VNC port so you can connect from your computer
EXPOSE 5900

# 8. The command to run everything, using your successful local command structure
CMD ["/bin/bash", "-c", \
     "Xvfb :99 -screen 0 1280x800x24 & \
      x11vnc -display :99 -forever -nopw & \
      java --module-path ./jfx-libs --add-modules javafx.controls,javafx.fxml -jar GraphVisualizer.jar"]