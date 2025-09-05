# Análisis Detallado de la Aplicación Carusel

Este documento proporciona un análisis exhaustivo de la aplicación, describiendo su arquitectura, tecnologías, funcionalidades y la lógica subyacente.

## 1. Resumen Ejecutivo

La aplicación, denominada "Carusel", es una utilidad de escritorio para sistemas operativos de escritorio (Windows, macOS, Linux) desarrollada en **Java**. Su función principal es actuar como un gestor de portapapeles avanzado, presentando el historial de elementos copiados en una interfaz gráfica innovadora en forma de "rueda" o "carrusel". Esta interfaz permite al usuario acceder rápidamente a elementos copiados previamente sin tener que buscarlos manualmente.

La aplicación está diseñada para funcionar en segundo plano y se activa mediante atajos de teclado globales, lo que la hace accesible desde cualquier otra aplicación. Incluye un sistema de licenciamiento, lo que indica que es un producto de software comercial o shareware.

## 2. Arquitectura del Software

La aplicación sigue un patrón de diseño basado en **Managers (Gestores)**, donde clases singleton centralizan la lógica de diferentes dominios de la aplicación. Esta arquitectura, aunque facilita el acceso a componentes clave desde cualquier punto del código, puede generar un alto acoplamiento si no se gestiona con cuidado.

Los principales componentes arquitectónicos son:

*   **`AppManager`**: Es el orquestador principal. Inicializa y coordina a todos los demás gestores, sirviendo como el punto de entrada para la lógica de la aplicación.
*   **`StageManager`**: Gestiona la interfaz de usuario (UI) construida con **JavaFX**. Es responsable de crear, mostrar, ocultar y animar la ventana principal (la "rueda").
*   **`ClipboardManager`**: Interactúa directamente con el portapapeles del sistema operativo. Captura los datos que el usuario copia (texto, imágenes, etc.) y los almacena para su uso en la rueda.
*   **`KeyboardManager`**: Implementa listeners de eventos globales para el teclado y el ratón. Esto permite a la aplicación detectar combinaciones de teclas (hotkeys) y movimientos de la rueda del ratón incluso cuando la aplicación no está en primer plano. Es la pieza clave para la activación global de la rueda.
*   **`DatabaseManager`**: Gestiona la persistencia de datos. Almacena la configuración del usuario (como los atajos de teclado) y posiblemente el historial del portapapeles en una base de datos local.
*   **`LicenseManager`**: Controla la lógica de licenciamiento y activación del producto. Se comunica con un servidor remoto para validar las licencias.
*   **`RetrofitManager`**: Gestiona la comunicación con la API externa a través de la librería **Retrofit**.

## 3. Tecnologías y Librerías

*   **Lenguaje de Programación**: **Java 8** (inferido por el uso de lambdas y la sintaxis general).
*   **Framework de UI**: **JavaFX**, el framework moderno de Java para la creación de interfaces gráficas de escritorio. Se utilizan archivos `.fxml` para definir la estructura de las vistas y `.css` para el estilo.
*   **Gestión de Dependencias**: No se utiliza un sistema de construcción estándar como **Maven** o **Gradle**. Las librerías están incluidas directamente como código fuente dentro del proyecto, en el paquete `com.carusel.app.lib`. Este es un enfoque muy poco convencional y puede dificultar la actualización de las dependencias.
    *   **`clipboardfx`**: Una librería para facilitar la interacción con el portapapeles en JavaFX.
    *   **`controlfx`**: Proporciona controles de UI adicionales para JavaFX.
    *   **`fxtrayicon`**: Permite crear un ícono en la bandeja del sistema para la aplicación.
*   **Comunicación HTTP**: **Retrofit 2**, una librería para realizar llamadas a APIs REST de forma sencilla y declarativa.
*   **Programación Reactiva**: **RxJava 2**, utilizada en conjunto con Retrofit para manejar las respuestas de la API de forma asíncrona.
*   **Serialización/Deserialización JSON**: **Gson**, para convertir objetos Java a formato JSON y viceversa.

## 4. Funcionalidad Detallada

### 4.1. Interfaz de Rueda (Wheel)

La funcionalidad central es la "rueda". Cuando el usuario presiona un atajo de teclado predefinido, aparece una interfaz superpuesta en la pantalla. Esta interfaz muestra una serie de elementos (los últimos N elementos copiados al portapapeles) dispuestos en un círculo. El usuario puede usar la rueda del ratón para "girar" el carrusel y seleccionar un elemento. Al hacer clic, el elemento seleccionado se vuelve a copiar en el portapapeles, listo para ser pegado.

### 4.2. Flujo de Activación y Licenciamiento

La aplicación requiere activación para su uso completo. El flujo es el siguiente:

1.  **Inicio de la Aplicación**: El `LicenseManager` se inicia y comprueba el estado de la licencia.
2.  **Llamada a la API**: Para activar el producto, la aplicación se comunica con un servidor externo.
    *   **API Base URL**: `http://nmnaufaldo.com/carusel-api/v1/`
3.  **Endpoints de la API**: La interfaz `CaruselAPI.java` define los siguientes puntos de comunicación:
    *   `POST GetRequestCode.php`: La aplicación realiza una petición a este endpoint para obtener un "código de solicitud" (`request_code`). Este código es probablemente un identificador único para la sesión de activación.
    *   `POST SerialNumberRegistration.php`: El usuario introduce un número de serie o código de activación en la UI. La aplicación envía este código junto con el `request_code` obtenido en el paso anterior a este endpoint.
4.  **Respuesta del Servidor**: El servidor valida los códigos. Si son correctos, devuelve una respuesta de activación (`ActivationSchema`), que desbloquea la funcionalidad completa de la aplicación. Esta respuesta probablemente se almacena localmente a través del `DatabaseManager`.

## 5. Lógica del Programa

1.  **Inicio**: `Launcher.java` es el punto de entrada. Comprueba si ya hay una instancia de la aplicación en ejecución para evitar duplicados. Si no, llama a `App.java`.
2.  **Inicialización de JavaFX**: `App.java` extiende `Application` de JavaFX. En su método `start()`, inicializa el `AppManager` y el `StageManager`.
3.  **Registro de Hotkeys**: `AppManager` instruye a `KeyboardManager` para que comience a escuchar los eventos globales del teclado y el ratón. La combinación de teclas para abrir/cerrar la rueda y la acción de la rueda del ratón se registran con sus respectivas funciones de callback.
4.  **Funcionamiento en Segundo Plano**: La aplicación permanece en ejecución, principalmente en segundo plano. El `ClipboardManager` monitorea continuamente el portapapeles. Cuando detecta un nuevo elemento copiado, lo procesa y lo añade a su historial.
5.  **Activación de la Rueda**: Cuando el usuario presiona el atajo de teclado registrado, el `KeyboardManager` lo detecta y ejecuta el callback correspondiente. Este callback llama al `StageManager` para que muestre y anime la interfaz de la rueda.
6.  **Interacción con la Rueda**: Mientras la rueda es visible, el `KeyboardManager` (o un listener local) detecta los movimientos de la rueda del ratón para navegar por los elementos y los clics para seleccionar uno.

## 6. Conclusión

"Carusel" es una aplicación de escritorio bien definida con una propuesta de valor clara: mejorar la gestión del portapapeles a través de una interfaz de usuario única. La arquitectura, aunque no convencional en su manejo de dependencias, es modular a través del uso de gestores. El código revela una funcionalidad completa que incluye persistencia local, comunicación con una API externa para licenciamiento y un sistema de control global de entrada del usuario.
