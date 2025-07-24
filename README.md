# Alerta360
## Integrantes
- Carrillo Daza, Barbara Rubi
- Diaz Portilla, Carlo Rodrigo
- Mamani Cañari, Gabriel Antony
- Ticona Hareth, Anthony Joaquin


## Funcionalidad

Alerta360 es una aplicación móvil diseñada para la seguridad ciudadana. Permite a los usuarios reportar y visualizar incidentes en tiempo real, fomentando la colaboración y la conciencia comunitaria.

### Características Principales

**Autenticación de Usuarios:**
*   Registro e inicio de sesión en la aplicación.
*   Gestión de la cuenta de usuario.

**Gestión de Incidentes:**
*   **Creación de Incidentes:** Los usuarios pueden reportar nuevos incidentes, proporcionando detalles como título, descripción, tipo de incidente y evidencias fotográficas.
*   **Visualización de Incidentes:** Los incidentes reportados se muestran en una lista en la pantalla principal.
*   **Mapa de Calor (Heatmap):** Visualización de incidentes en un mapa, permitiendo a los usuarios identificar zonas de mayor incidencia, así como ver directamente el incidente desde la vista de marcadores.

**Chat en Tiempo Real:**
*   Comunicación directa entre usuarios a través de un sistema de chat integrado.
*   Lista de conversaciones activas.

**Notificaciones Push:**
*   Recepción de notificaciones en tiempo real sobre nuevos incidentes y otras alertas relevantes.
*   Las notificaciones pueden abrir directamente la pantalla del incidente correspondiente.

**Servicios de Localización:**
*   Uso de la geolocalización para reportar la ubicación exacta de los incidentes.


### Arquitectura
<img width="989" height="635" alt="arquitectura" src="https://github.com/user-attachments/assets/a77e865b-9a7c-441e-82d9-9e2a8e9dc801" />

**Frontend (Android - Jetpack Compose + MVVM)**

* Utiliza Dagger-Hilt para inyección de dependencias
  Implementa el patrón MVVM con capas bien separadas (Presentation, Domain, Data)
  Maneja la interfaz de usuario para reportes de incidentes, mapas de calor y chat

**Backend (Node.js + Express)**

* API REST para operaciones CRUD de incidentes y gestión de usuarios
  Integra Socket.io para comunicación bidireccional en tiempo real (chat y notificaciones push)

**Base de Datos (MongoDB Atlas)**

* Almacena información de usuarios, incidentes reportados y conversaciones de chat
  Permite consultas geoespaciales para el mapa de calor

**Servicios Externos**

* Firebase Cloud Messaging: Maneja las notificaciones push cuando ocurren nuevos incidentes
* Firebase Authentication: Gestiona el registro y autenticación de usuarios
* Google Maps API: Proporciona funcionalidad de mapas y geolocalización para reportar ubicaciones exactas

La arquitectura está optimizada para tiempo real, donde los incidentes reportados se propagan inmediatamente a todos los usuarios conectados, y el sistema de chat permite comunicación instantánea entre la comunidad para mejorar la respuesta colaborativa ante situaciones de seguridad.

## Notas
* Después de descargar el proyecto del repositorio GitHub y antes de ejecutarlo se debe descargar el archivo que se encuentra en el siguiente enlace:
  https://drive.google.com/file/d/17uzcdNJXBAqlpBp2b8YW4XLR8DpmzLzO/view?usp=sharing
* El archivo “google-services.json” una vez descargado debe ser colocado en la altura de “app/” como se muestra en la imagen:
  <img width="405" height="395" alt="json" src="https://github.com/user-attachments/assets/6f3931f3-e713-478b-b0ee-6d38a49a31dc" />

* Actualmente, el registro de usuarios se encuentra funcionando pero si no desea crear un nuevo usuario puede usar el siguiente para ingresar:
    * Correo: aticonaha@unsa.edu.pe
    * Contraseña: 1234567890
* Antes de ejecutar verificar que el servicio backend esté ejecutándose
  https://backend-alerta360.onrender.com/
  <img width="759" height="560" alt="image" src="https://github.com/user-attachments/assets/8045bc06-706e-4764-8daa-d8fefa238254" />

