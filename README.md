# Proyecto de Cliente de Correo Electrónico

## Resumen
Este proyecto es una aplicación de cliente de correo electrónico que permite a los usuarios enviar, recibir y organizar correos electrónicos. La aplicación implementa funcionalidades básicas de correo y cuenta con características como registro de usuarios, sistema de inicio de sesión y organización de correos en carpetas.

## Características

### 1. Envío y Recepción Básica de Correos
- **Redacción y envío de correos**: Los usuarios pueden redactar un correo con destinatario, asunto y cuerpo, y enviarlo a otro usuario.
- **Recepción de correos**: Los usuarios pueden recibir correos y visualizarlos en su bandeja de entrada.
- **Vista de la bandeja de entrada**: Muestra el remitente, asunto y marca de tiempo de los correos recibidos.

### 2. Sistema de Registro e Inicio de Sesión de Usuarios
- **Registro de usuarios**: Los nuevos usuarios pueden registrarse proporcionando su nombre, apellido, dirección de correo única y una contraseña.
- **Inicio de sesión**: Los usuarios pueden iniciar sesión de manera segura utilizando su correo electrónico y contraseña.
- **Gestión de sesiones**: Autenticación basada en tokens para mantener las sesiones seguras y gestionar el estado del usuario.

### 3. Organización de Correos
- **Carpetas personalizadas**: Los usuarios pueden crear carpetas personalizadas para organizar sus correos.
- **Gestión de correos**: Los correos pueden moverse entre la bandeja de entrada/enviados y las carpetas personalizadas.
- **Eliminación**: Los correos pueden eliminarse de cualquier carpeta, incluida la bandeja de entrada.

## Tecnologías Utilizadas

### Backend
- **Java**: Lenguaje principal para implementar la lógica del backend.
- **Spring Boot**: Framework para construir APIs RESTful y gestionar dependencias.
- **JWT (JSON Web Tokens)**: Utilizado para autenticación segura y gestión de sesiones.
- **Base de datos**: Gestiona las credenciales de los usuarios, los correos y los datos de las carpetas, cuya implementación está dada en Postgres.
- **Docker**: Permite ejecutar la aplicación y la base de datos de manera sencilla y rápida a través de contenedores.

### Frontend
- **HTML**: Estructura de la aplicación web.
- **CSS**: Estilo y diseño.
- **JavaScript**: Agrega interactividad y maneja la lógica del frontend (se hace un uso puro del lenguaje es decir, cero frameworks).

### APIs
El backend proporciona una API RESTful con los siguientes endpoints:

| Endpoint                                              | Método | Descripción                                                                                           |
|-------------------------------------------------------|--------|-------------------------------------------------------------------------------------------------------|
| `/auth/login`                                         | GET    | Obtiene la vista web del login.                                                                       |
| `/auth/registro`                                      | GET    | Obtiene la vista web del registro.                                                                    |
| `/bandeja`                                            | GET    | Obtiene la vista web de la bandeja de entrada.                                                        |
| `/auth/registro`                                      | POST   | Registra un nuevo usuario.                                                                            |
| `/auth/login`                                         | POST   | Autentica a un usuario y proporciona un token JWT.                                                    |
| `/carpetas`                                           | GET    | Recupera todas las carpetas del usuario.                                                              |
| `/carpetas`                                           | POST   | Crea una nueva carpeta.                                                                               |
| `/carpetas/{idCarpeta}`                               | PUT    | Actualiza el nombre de la carpeta.                                                                    |
| `/carpetas/{folderId}`                                | DELETE | Elimina la carpeta especificada.                                                                      |
| `/carpetas/nombre/{nombreCarpeta}`                    | GET    | Recupera el id de la carpeta.                                                                         |
| `/carpetas/{idCarpeta}/mensaje/{idMensaje}`           | DELETE | Elimina el mensaje de la carpeta especificada.                                                        |
| `/mensajes`                                           | POST   | Envia un mensaje al destinatario indicado.                                                            |
| `/mensajes/{idMensaje}/carpeta`                       | PUT    | Cambia el mensaje especificado de una carpeta origen a una destino.                                   |
| `/mensajes/{mensajeId}/es-entrada-o-enviados`         | GET    | Determina si el mensaje en cuestión pertenece o no a la bandeja de entrada o enviados.                |
| `/mensajes/{idMensaje}`                               | DELETE | Elimina el mensaje especificado                                                                       |
| `/mensajes-propietarios/carpetas/{idCarpeta}`         | GET    | Obtener el mensaje especificado con información adicional, como por ejemplo si ha sido revisado o no. |
| `/mensajes-propietarios//mensajes/{idMensaje}/estado` | PUT    | Marca un mensaje como revisado.                                                                       |
| `/usuarios`                                           | GET    | Obtiene la información pertinente del usuario.                                                        |

## Instrucciones de Configuración

### Requisitos Previos
- **Java 17 o superior**
- **Docker**: Nos permite manejar la ejecución de la aplicación web junto con la base de datos de una manera mucho más fácil a través de contenedores

### Configuración del Backend
1. Clona el repositorio:
   ```bash
   git clone <repository-url>
   cd <repository-name>
   ```
2. Crea un archivo `.env` para configurar las variables de entorno que utilizará Docker:
    ```bash
   touch .env
   ```
3. Configura las variables de entorno dentro del archivo `.env`:
   ```properties
   DB_NAME=nombre-de-tu-preferencia
   DB_USERNAME=username-de-tu-preferencia
   DB_PASSWORD=password-de-tu-prferencia
   ```
4. Crea un archivo `env.properties` para configurar las variables globales que usará java:
    ```properties
    DB_HOST=java_db
    DB_PORT=5432
    DB_NAME=mismo-nombre-de-.env
    DB_USERNAME=mismo-username-que-.env
    DB_PASSWORD=mismo-password-que-.env
    LLAVE_ENCRIPTAR=debes-elegir-una-frase-lo-suficientemente-larga-como-para-que-su-cantidad-de-bits-sea->=256
   ```
5. Compila la aplicación haciendo uso de maven:
   ```bash
   ./mvnw clean install -D skipTests
   ```
6. Ejecuta la aplicación haciendo uso de docker:
   ```bash
   docker compose up
   ```

### Ejecución de la Aplicación
1. Accede a la aplicación en tu navegador en `http://localhost:8080/auth/login`.
2. Regístrate con una nueva cuenta o inicia sesión con una cuenta existente.
3. Utiliza la interfaz para enviar, recibir y organizar correos de manera local.

