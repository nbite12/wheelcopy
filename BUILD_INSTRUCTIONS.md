# Build and Run Instructions

This guide explains how to set up, build, and run the WheelCopy application on a new machine.

## Prerequisites

1.  **Java Development Kit (JDK) 17**
    *   You **MUST** have JDK 17 installed.
    *   Download it from: [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) or [Adoptium (Temurin) 17](https://adoptium.net/temurin/releases/?version=17).
    *   **Important:** Ensure `JAVA_HOME` environment variable is set to your JDK 17 installation path.
        *   Example: `C:\Program Files\Java\jdk-17`

## Project Structure

The repository includes a portable version of Maven to ensure compatibility.

*   `apache-maven-3.9.11/`: Portable Maven installation.
*   `pom.xml`: Project configuration.
*   `main/`: Source code.

## How to Build

1.  **Clone the repository:**
    ```powershell
    git clone <repository-url>
    cd wheelcopy
    ```

2.  **Set JAVA_HOME (if not already set globally):**
    ```powershell
    $env:JAVA_HOME = "C:\Path\To\Your\JDK-17"
    ```

3.  **Build the project using the included Maven:**
    ```powershell
    .\apache-maven-3.9.11\bin\mvn clean package
    ```
    *   This command will compile the code and create the executable JAR and EXE files in the `target/` directory.

## How to Run

### Option 1: Run the EXE (Windows)
After building, you can run the generated executable:
```powershell
.\target\WheelCopy.exe
```

### Option 2: Run the JAR
You can also run the JAR file directly using Java:
```powershell
& "$env:JAVA_HOME\bin\java.exe" -jar target\wheelcopy-1.0.0.jar
```

## Troubleshooting

*   **"javac is not recognized":** This means your `JAVA_HOME` is pointing to a JRE instead of a JDK, or it's not set correctly. Make sure you installed the **JDK**.
*   **"MojoExecutionException":** If `mvn clean` fails, it usually means the app is still running or a file is locked. Close the app and try again.
