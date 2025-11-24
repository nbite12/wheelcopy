# WheelCopy Build Guide

This guide explains how to build and run the WheelCopy application on a new machine.

## Prerequisites

To build this project, you need the following tools installed:

1.  **Java Development Kit (JDK) 17**
    *   Download: [OpenJDK 17](https://jdk.java.net/archive/) or [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17)
    *   Ensure `JAVA_HOME` environment variable is set to your JDK 17 installation.
    *   Ensure `java` is in your system `PATH`.

2.  **Apache Maven 3.9.x** (or newer)
    *   Download: [Apache Maven](https://maven.apache.org/download.cgi)
    *   Ensure `mvn` is in your system `PATH`.

## Building the Application

1.  Open a terminal (Command Prompt or PowerShell).
2.  Navigate to the project root directory (where `pom.xml` is located).
3.  Run the following command to clean and build the project:

    ```powershell
    mvn clean package -DskipTests
    ```

    *   This command compiles the code, downloads dependencies, and packages the application.
    *   The `-DskipTests` flag is optional but recommended if you want to skip running unit tests during the build.

## Running the Application

### Option 1: Run from Source (Maven)

You can run the application directly using the JavaFX Maven plugin:

```powershell
mvn javafx:run
```

### Option 2: Run the Executable

After a successful build (Step 2), an executable file is generated in the `target` directory:

*   **Location:** `target/WheelCopy.exe`

You can double-click this file or run it from the terminal:

```powershell
.\target\WheelCopy.exe
```

## Troubleshooting

*   **"mvn is not recognized..."**: Ensure Maven is added to your system `PATH`.
*   **"java is not recognized..."**: Ensure Java is added to your system `PATH`.
*   **Classpath errors**: Verify that you are using JDK 17. Newer versions (like JDK 21+) might require additional flags or configuration adjustments.
