# Econexion - Marketplace para materiales reciclables

Una API REST moderna construida con Spring Boot que proporciona autenticación JWT, gestión de usuarios y un sistema de chat en tiempo real para conexiones de e-commerce.

Econexion API es una solución backend diseñada para facilitar la comunicación entre usuarios en plataformas de e-commerce. Ofrece un sistema que incluye: 
- autenticación segura con tokens JWT que permiten acceso controleado a los recursos. 
- La gestión de usuarios ofrece operaciones CRUD completas con diferentes rolees como vendedor y comprador. 
- Para facilitar la comunicación, se implementó un sistema de chat que organiza las conversaciones por ofertas comerciales, manteniendo un historial estructurado.

La aplicación está construida con Spring Boot 3.2+ y Java 21, siguiendo las mejores prácticas de desarrolelo de APIs RESTful. Soporta múltiples configuraciones para adaptarse a diferentes entornos, desde desarrolelo rápido hasta producción.

También se incluye documentación interactiva mediante Swagger/OpenAPI y soporte completo para Docker.

## Requisitos técnicos
Para ejecutar esta aplicación necesitarás Java 21 o superior y Maven 3.9+. En configuración de producción se requiere PostgreSQL 15+. Opcionalmente, Docker y Docker Compose facilitan el despliegue en contenedores.

## Configuración y despliegue
### Configuración con Docker (recomendado)
Clona el repositorio y ejecuta:

```bash
git clone https://github.com/Eco-nexion/Econexion-back.git
cd Econexion-back
docker-compose up --build
```
La aplicación estará disponible en http://localhost:35001, con la interfaz Swagger en http://localhost:35001/swagger-ui/index.html.

### Configuración tradicional
Si prefieres una instalación tradicional, primero configura PostgreSQL. Puedes usar Docker para esto:

#### Para Windows PowerShell
```powershell
docker run -d --name postgres-econexion `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=12345 `
  -e POSTGRES_DB=econexion `
  -p 5432:5432 `
  postgres:15
```

O en una sola línea (más seguro):

```powershell
docker run -d --name postgres-econexion -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=econexion -p 5432:5432 postgres:15
```

#### Para Bash (Linux/macOS/Git Bash)
```bash
docker run -d --name postgres-econexion \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=12345 \
  -e POSTGRES_DB=econexion \
  -p 5432:5432 \
  postgres:15
```

#### O en una sola línea:

```bash
docker run -d --name postgres-econexion -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=econexion -p 5432:5432 postgres:15
```

Luego compila y ejecuta la aplicación:

```bash
mvn clean package -DskipTests

java -jar target/econexion-1.0-SNAPSHOT.jar --server.port=35001 --spring.profiles.active=default
```
---

#### ¿Qué hacer si no funciona?
Si encuentras errores:

- Verifica que Docker Desktop esté corriendo (icono en bandeja)

- Ejecuta PowerShell como administrador

- O usa Git Bash que tiene entorno más similar a Linux


## Autenticación

El sistema utiliza autenticación JWT. Para registrarse, envía una solicitud POST a /api/auth/register con los datos del usuario. Para iniciar sesión, usa POST a /api/auth/login con nombre de usuario y contraseña. La respuesta incluirá un token JWT que debes incluir en el encabezado Authorization: Bearer <token> de las solicitudes subsiguientes.

---

## 7) Users Endpoints / Usuarios

Base path: `/lab/users`

| Method | Path                     | Description (EN)     | Descripción (ES)          |
|--------|--------------------------|----------------------|---------------------------|
| GET    | `/lab/users/allUsers`    | List all users       | Lista todos los usuarios  |
| GET    | `/lab/users/{id}`        | Get user by id       | Obtener usuario por id    |
| POST   | `/lab/users/addUser`     | Create user          | Crear usuario             |
| PUT    | `/lab/users/update/{id}` | Update user          | Actualizar usuario        |
| DELETE | `/lab/users/delete/{id}` | Delete user          | Eliminar usuario          |

**POST JSON (example)**
```json
{
  "enterpriseName": "karenCorp",
  "username": "karen Amaya",
  "nit": "1000474431",
  "email": "karen@demo.test",
  "password": "giovann1",
  "role": "seller"
}
```

---

## 8) NEW — Chat Between Users / Chat entre Usuarios

### 8.1 Data Model / Modelo de datos (simplificado)
- **Tables / Tablas**:
  - `chat_conversations` (PK `id` BIGINT IDENTITY, `offer_id` BIGINT, `participant1_id` BIGINT, `participant2_id` BIGINT, `created_at`, `updated_at`)  
    - **Unique**: (`offer_id`, `participant1_id`, `participant2_id`)
  - `chat_messages` (PK `id` BIGINT IDENTITY, `conversation_id` BIGINT, `sender_id` BIGINT, `text` VARCHAR(5000), `created_at`)
    - FK → `chat_conversations(id)`

> A conversation is unique by *offer + the two participants* (order-independent).  
> Una conversación es única por *oferta + los dos participantes* (independiente del orden).

### 8.2 Endpoints

#### A) Create or reuse conversation / Crear o reutilizar conversación
`POST /api/chat/conversations`

**Request (JSON)**
```json
{
  "offerId": 1001,
  "senderId": 10,
  "receiverId": 22,
  "text": "Hola, me interesa tu oferta. ¿Seguimos por aquí?"
}
```
- Creates the conversation if it doesn’t exist (by unique tuple), and optionally saves the first message (`text`).  
- Crea la conversación si no existe (tupla única) y opcionalmente persiste el primer mensaje (`text`).

**Response (201/200)**
```json
{
  "conversationId": 123, 
  "offerId": 1001,
  "participant1Id": 10,
  "participant2Id": 22,
  "created": true,
  "lastMessageAt": "2025-10-25T21:45:11.599Z"
}
```

**Errors**  
- `400` invalid payload / JSON inválido o campos faltantes.  
- `401/403` if JWT/permissions required.  
- `409` if uniqueness violated unexpectedly.

---

#### B) List conversations by user / Listar conversaciones por usuario
`GET /api/chat/conversations?userId=22`

**Response (200)**
```json
[
  {
    "conversationId": 123,
    "offerId": 1001,
    "participant1Id": 10,
    "participant2Id": 22,
    "lastMessagePreview": "¿Seguimos por aquí?",
    "updatedAt": "2025-10-25T21:45:11.599Z"
  }
]
```

---

#### C) Send a message / Enviar mensaje
`POST /api/chat/conversations/{conversationId}/messages`

**Request (JSON)**
```json
{
  "senderId": 22,
  "text": "¡Perfecto! ¿Cuándo coordinamos?"
}
```

**Response (201)**
```json
{
  "messageId": 987,
  "conversationId": 123,
  "senderId": 22,
  "text": "¡Perfecto! ¿Cuándo coordinamos?",
  "createdAt": "2025-10-25T21:46:33.100Z"
}
```

**Errors**
- `400` invalid message (e.g., empty or >5000 chars)
- `404` conversation not found

---

#### D) List messages / Listar mensajes
`GET /api/chat/conversations/{conversationId}/messages`

**Response (200)**
```json
[
  { "id": 1, "senderId": 10, "text": "Hola, me interesa tu oferta. ¿Seguimos por aquí?", "createdAt": "2025-10-25T21:45:11.700Z" },
  { "id": 2, "senderId": 22, "text": "¡Perfecto! ¿Cuándo coordinamos?", "createdAt": "2025-10-25T21:46:33.100Z" }
]
```

---

### 8.3 Quick tests (curl / PowerShell)

<img width="1443" height="495" alt="imagen" src="https://github.com/user-attachments/assets/bc83cb05-6f3b-4b65-877a-2b6f06909957" />
<img width="1116" height="343" alt="imagen" src="https://github.com/user-attachments/assets/cee60a47-d718-46c0-bbb4-71b985a5a8cd" />
<img width="1839" height="933" alt="imagen" src="https://github.com/user-attachments/assets/70b90dea-2499-412b-95f7-da4bca7ceb02" />

> **Encoding matters**: always send `Content-Type: application/json; charset=utf-8`.  
> **Importante**: enviar siempre `Content-Type: application/json; charset=utf-8`.

**curl (Linux/macOS or Git Bash on Windows)**
```bash
BASE=http://localhost:35001

# Create/reuse conversation
curl -s -X POST "$BASE/api/chat/conversations"   -H "Content-Type: application/json; charset=utf-8"   -d '{"offerId":1001,"senderId":10,"receiverId":22,"text":"Hola, me interesa tu oferta. ¿Seguimos por aquí?"}'

# Send message
curl -s -X POST "$BASE/api/chat/conversations/123/messages"   -H "Content-Type: application/json; charset=utf-8"   -d '{"senderId":22,"text":"¡Perfecto! ¿Cuándo coordinamos?"}'

# List conversations
curl -s "$BASE/api/chat/conversations?userId=22"

# List messages
curl -s "$BASE/api/chat/conversations/123/messages"
```

**PowerShell (Windows) — UTF-8 safe**
```powershell
$BASE = "http://localhost:35001"

# Create/reuse conversation
$convObj = @{
  offerId    = 1001
  senderId   = 10
  receiverId = 22
  text       = "Hola, me interesa tu oferta. ¿Seguimos por aquí?"
}
$convJson = $convObj | ConvertTo-Json -Depth 5
$bytes    = [System.Text.Encoding]::UTF8.GetBytes($convJson)
$convResp = Invoke-RestMethod "$BASE/api/chat/conversations" -Method POST -Headers @{ "Content-Type"="application/json; charset=utf-8" } -Body $bytes

$CONV_ID = $convResp.conversationId

# Send message
$msgObj = @{ senderId = 22; text = "¡Perfecto! ¿Cuándo coordinamos?" }
$msgJson = $msgObj | ConvertTo-Json
$msgBytes = [System.Text.Encoding]::UTF8.GetBytes($msgJson)
Invoke-RestMethod "$BASE/api/chat/conversations/$CONV_ID/messages" -Method POST -Headers @{ "Content-Type"="application/json; charset=utf-8" } -Body $msgBytes

# List messages
Invoke-RestMethod "$BASE/api/chat/conversations/$CONV_ID/messages" -Method GET | ConvertTo-Json
```

> **Nota sobre `curl` en PowerShell**: `curl` es un alias de `Invoke-WebRequest` y **no** soporta opciones como `--data-binary`. Usa **`Invoke-RestMethod`** o el binario de Git Bash **`curl.exe`**.

**Run all tests / Ejecutar todas**  
```bash
mvn test
```

**Run only non-chat tests we added / Solo las pruebas nuevas**  
```bash
# Linux/macOS/Git Bash
mvn "-Dtest=SmokeTest,UserServiceTest,OfferServiceTest,PostServiceTest,JwtUtilTest,UserControllerStandaloneTest,UserControllerListStandaloneTest" test

# Windows PowerShell (nota las comillas)
mvn "-Dtest=SmokeTest,UserServiceTest,OfferServiceTest,PostServiceTest,JwtUtilTest,UserControllerStandaloneTest,UserControllerListStandaloneTest" test
```

**Notes**  
- En PowerShell, el parámetro `-Dtest` debe ir entre **comillas** si pasas una lista separada por comas.  
- Los tests de Controller están hechos en modo **standalone** (no levantan todo el contexto).

---

## 9) Troubleshooting / Solución de problemas

- **`Cannot load driver class: org.h2.Driver`**  
  Asegúrate de ejecutar con el *perfil* `chat-h2` o incluir la dependencia H2 en el classpath.  
  Ejemplo: `mvn spring-boot:run -Dspring-boot.run.profiles=chat-h2`

- **Puerto en uso / Port already in use**  
  Cambia `--server.port` o libera el puerto:  
  PowerShell:
  ```powershell
  Get-NetTCPConnection -LocalPort 35001 -State Listen
  taskkill /PID <PID> /F
  ```

- **`JSON parse error: Invalid UTF-8 start byte 0xbf`**  
  Envía JSON como **UTF-8** (ver ejemplos PowerShell con bytes en UTF-8).

- **`Unknown lifecycle phase ".run.profiles=..."` en Maven**  
  Ejecuta el goal primero: `mvn spring-boot:run -Dspring-boot.run.profiles=chat-h2 -DskipTests`

- **Health check**
  ```bash
  curl -s http://localhost:35001/actuator/health
  # → {"status":"UP"}
  ```

---

## 10) Configuration / Configuración (snippet)

`application-lab.yml` highlights:  
```yaml
server:
  port: 35001

spring:
  main:
    allow-bean-definition-overriding: true

jwt:
  secret: ${JWT_SECRET:super-secreto-cambiame-por-env-32chars-min}
  expiration-minutes: 120
```

---

## 11) Project Layout / Estructura del proyecto

```
src/main/java/io/econexion/
  ├─ lab/users/...
  ├─ weather/...
  ├─ chat/
  │    ├─ Controller/ (ChatController, endpoints REST)
  │    ├─ service/    (ChatService, lógica de negocio)
  │    ├─ repository/ (ChatConversationRepository, ChatMessageRepository)
  │    └─ model/      (ChatConversation, ChatMessage)
  ├─ security/
  │    ├─ SecurityConfig.java
  │    ├─ JwtUtil.java
  │    └─ ...
  └─ EconexionLabApplication.java
```

---

## 12) Quick cURL (Auth & Weather)

<img width="1718" height="935" alt="Captura de pantalla 2025-10-25 170134" src="https://github.com/user-attachments/assets/fc98edbd-cdb7-46c2-9aa9-6c8fa2efd311" />

**Login**
```bash
curl -X POST http://localhost:35001/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"ada","password":"school"}'
```

**Weather POST (with token)**
```bash
curl -X POST http://localhost:35001/v1/weather/BOG   -H "Authorization: Bearer <TOKEN>"   -H "Content-Type: application/json"   -d '{"weather":{"temp":17.5,"pressure":994.71,"humidity":61}}'
```

---

## 13) Notes / Notas
- Users CRUD works only in `lab` profile.  
- Weather POST requires JWT.  
- Login endpoint issues JWT using in-memory user (`ada` / `school`) for demo.
- **Chat REST** is synchronous (no WebSocket); messages are stored and listed via HTTP endpoints.

---

## 14) License / Licencia
MIT (or your repo license).

— Updated **2025-10-25**
