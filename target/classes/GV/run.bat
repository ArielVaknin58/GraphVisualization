@echo off

:: 1. Set path to JavaFX module JARs
set PATH_TO_FX_JARS=.\lib

:: 2. Set path to JavaFX native libraries (DLLs)
set PATH_TO_FX_BIN=.\bin

:: 3. Set the name of your project's JAR file
set JAR_FILE=GraphVisualizer.jar

:: 4. Set your project's main class
set MAIN_CLASS=GraphVisualizer.GraphVisualizer

echo "Launching %JAR_FILE%..."

:: 5. Run the Java command
:: -Djava.library.path points to the native DLLs
:: --module-path points to the JavaFX JARs
"%JAVA_HOME%\bin\java.exe" -Djava.library.path="%PATH_TO_FX_BIN%" --module-path "%PATH_TO_FX_JARS%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "%JAR_FILE%" %MAIN_CLASS%

:: Optional: Add 'pause' on the next line if you want to see
:: any error messages in the console before it closes.
pause