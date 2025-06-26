x# Alerta360
## Integrantes
- Carrillo Daza, Barbara Rubi
- Diaz Portilla, Carlo Rodrigo
- Mamani Cañari, Gabriel Antony
- Ticona Hareth, Anthony Joaquin



# Trabajo en clase 26/06/2025

## 1. Creación de entidad y DAO (Carrillo Daza, Barbara Rubi)

Para representar cada incidente en la base de datos local:

1. **Definición de la entidad**  
   - Se creó una clase de dominio `Incident` anotada con `@Entity`.  
   - Incluye propiedades como `id`, `title`, `description`, `incidentType`, `ubication`, `geolocation`, `evidences` (lista), `district`, `userId`, `createdAt`, `updatedAt`, `version` y `synced` para identificar el estado del registro.
2. **Convertidores de tipos complejos**  
   - Dado que Room sólo sabe manejar tipos primitivos, se implementaron convertidores (`TypeConverter`) para transformar listas de cadenas a JSON y fechas (por ejemplo `Instant`) a `Long`.
3. **Interfaz DAO**  
   - Se definió `IncidentDao` con operaciones esenciales:
     - Un método para **observar** todos los incidentes (devolviendo un `Flow<List<Incident>>` ordenados por fecha de actualización).
     - Un método para **insertar/reemplazar** lotes de incidentes.
     - Métodos auxiliares para **consultar y guardar** el token de sincronización (`ETag`) en una tabla de estado.

---

## 2. Guardar en local los incidentes (Diaz Portilla, Carlo Rodrigo)

La persistencia local se organizó en dos capas:

1. **Repositorio local**  
   - Envuelve al DAO y expone funciones como `getAllIncidents()` (retorna un flujo reactivo) y `saveIncidents(List<Incident>)`.
   - También maneja la obtención y almacenamiento del `ETag` para sincronización incremental.
2. **Casos de uso (Use Cases)**  
   - **GetAllIncidentsUseCase**: devuelve el flujo de incidentes para que la capa de presentación lo consuma.  
   - **CreateIncidentUseCase**: gestiona la creación de un nuevo incidente contra la API remota y, en caso de éxito, lo guarda en la base de datos local.

De esta manera, cualquier cambio en la base de datos (inserción o actualización) se propaga automáticamente a la UI.

---

## 3. Sincronización con WorkManager (Mamani Cañari, Gabriel Antony)

Para mantener los datos actualizados sin intervención del usuario:

1. **Definición del Worker**  
   - Se implementó un `CoroutineWorker` (`SyncIncidentsWorker`) que:
     - Lee el `ETag` actual desde la base de datos.
     - Llama al endpoint remoto para solicitar nuevos incidentes (pasando el `ETag` para evitar datos duplicados).
     - Mapea los DTO recibidos a entidades de dominio.
     - Inserta los incidentes en la base de datos y actualiza el `ETag` si hay uno nuevo.
2. **Programación periódica**  
   - Se configuró un trabajo periódico con un intervalo mínimo (15 minutos) usando `PeriodicWorkRequest`.
   - Se encola como trabajo único con una política de reemplazo para evitar duplicar instancias.
3. **Manejo de errores**  
   - En caso de fallo de red o de base de datos, el Worker devuelve `Result.retry()` para que WorkManager reintente automáticamente.

---

## 4. Conexión con UI e instalación de Room (Ticona Hareth, Anthony Joaquin)

Para exponer los datos en pantalla usando Jetpack Compose y MVVM:

1. **Instalación de Room**  
   - Se añadió la dependencia de Room Runtime y Room KTX al `build.gradle`.  
   - Se habilitó el procesador de anotaciones (KSP/KAPT) para generar el código de acceso.
2. **ViewModel**  
   - Inyecta el caso de uso `GetAllIncidentsUseCase` y expone un `StateFlow<List<Incident>>`.  
   - Utiliza `stateIn` y `viewModelScope` para mantener los datos activos mientras la UI esté suscrita.
3. **Composable**  
   - La pantalla principal suscribe el flujo con `collectAsState()` y muestra los incidentes en un `LazyColumn`.  
   - Cada elemento renderiza título, descripción y otros campos relevantes de forma declarativa.

## 5. Pregunta (Todos)

**¿Cómo se podría sincronizar cuando un dato se modificó en el backend? Por ejemplo, tenemos una app para compra de pasajes de bus, el usuario selecciona un asiento y mientras llena algunos datos, otro usuario compro el asiento?**
A través de la verificación de disponibilidad en tiempo real justo antes de confirmar la transacción. Cada recurso (por ejemplo, asiento) tendría asignado un identificador de versión o timestamp que se envía al cliente cuando este selecciona el asiento. Al intentar reservarlo, el cliente incluye esa versión en su petición, entonces, el servidor compara la versión recibida con la actual; si coinciden, se confirma la reserva; si no, se rechaza y se informa al usuario que el asiento ya no está disponible.

---
