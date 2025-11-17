# Econexion Lab – REST API (Users + Weather + JWT + Chat)
**(EN / ES)**

Spring Boot REST API for lab practice. It now includes:
- In-memory CRUD for *users* (profile `lab`).
- Weather endpoint (`/v1/weather/{city}`) with JWT protection for POST.
- Basic login (`/api/auth/login`) issuing JWT tokens.
- **NEW: User-to-User Chat (REST)** — create conversations by offer and exchange messages.

---

## 1) Overview / Resumen

**EN**: This lab project demonstrates Spring Boot REST with profiles, in-memory persistence, authentication using JWT, and a simple user-to-user chat stored in a relational DB (PostgreSQL or H2 for dev).  
**ES**: Este proyecto de laboratorio demuestra REST con Spring Boot, perfiles, autenticación con JWT y un chat entre usuarios almacenado en BD relacional (PostgreSQL o H2 para desarrollo).

---

## 2) Requirements / Requisitos
- Java **21**
- Maven **3.9+**
- Internet for dependencies / Internet para dependencias
- PostgreSQL **15+** (prod/dev normal) or H2 (dev profile `chat-h2`) / PostgreSQL **15+** (prod/dev normal) o H2 (perfil dev `chat-h2`)

---

## 3) Profiles / Perfiles

- `lab`: ejemplo de CRUD en memoria para *users* y login simple con JWT.
- `default` (prod/dev con Postgres): usa PostgreSQL real para persistencia.
- `chat-h2` (dev): levanta **H2 en memoria** y habilita el chat para pruebas rápidas (incluye consola H2 opcional).

> Opcional (solo dev): si tu seguridad lo requiere, puedes permitir el chat sin JWT con  
> `econexion.security.permit-chat=true` (añade bajo el perfil que estés usando).

---

## 🚀 How to run Econexion-back locally

This project uses **Spring Boot**, **PostgreSQL via Docker**, and **Maven**.

### 1️⃣ Requirements

- Java 21+
- Maven 3.9+
- Docker Desktop
- Git

Verify installation:

```
java -version
mvn -version
docker -v
```

---

### 2️⃣ Start PostgreSQL in Docker

Stop and remove any previous container:

```
docker stop postgres-econexion || true
docker rm postgres-econexion || true
```

Start PostgreSQL on port **5433**:

```
docker run -d --name postgres-econexion   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=12345   -e POSTGRES_DB=econexion   -p 5433:5432   postgres:15
```

Check if running:

```
docker ps
```

---

### 3️⃣ application.yml configuration

```
server:
  port: 35000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/econexion
    username: postgres
    password: 12345
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when_authorized

jwt:
  secret: unSuperSecretoDePrueba1234567890
  expiration-minutes: 120
```

---

### 4️⃣ Run the backend

From the project root:

```
mvn spring-boot:run
```

API available at:

```
http://localhost:35000
```

Health check:

```
curl http://localhost:35000/actuator/health
```

<img width="1919" height="1007" alt="imagen" src="https://github.com/user-attachments/assets/19321cc4-f87b-49bb-88dc-b242bd8a0d73" />
<img width="889" height="351" alt="imagen" src="https://github.com/user-attachments/assets/5348bbf2-a405-4991-9356-54a30b04fa6e" />
<img width="768" height="362" alt="imagen" src="https://github.com/user-attachments/assets/7d252d46-df64-4efe-a246-60ca84ada034" />
<img width="724" height="149" alt="imagen" src="https://github.com/user-attachments/assets/5f059e07-6677-45d1-bcdf-95b48dd61c4f" />

---

### 5️⃣ Run unit tests

```
mvn test
```

Generate Jacoco report:

```
mvn jacoco:report
```

Report output:

```
target/site/jacoco/index.html
```

---

### 6️⃣ Connect manually to PostgreSQL

```
docker exec -it postgres-econexion psql -U postgres
```

Change password if needed:

```
ALTER USER postgres WITH PASSWORD '12345';
```

---

## 📖 5) Swagger / OpenAPI

`http://localhost:35000/swagger-ui/index.html#/` (o `35001` si usas `chat-h2`).

---

## 6) Auth / Autenticación

**Login**  
`POST /api/auth/login`  
Body:
```json
{ "username": "ada", "password": "school" }
```
Response:
```json
{ "token": "<JWT_TOKEN>" }
```

**Register**  
`POST /api/auth/register`  
Body:
```json
{
  "enterpriseName": "karenCorp",
  "username": "karen Amaya",
  "nit": "1000474431",
  "email": "karen@demo.test",
  "password": "giovann1",
  "rol": "seller"
}
```
Response (example):
```json
{
  "id": "UUID",
  "enterpriseName": "karenCorp",
  "username": "karen Amaya",
  "nit": "1000474431",
  "email": "karen@demo.test",
  "password": "<hash>",
  "rol": "seller"
}
```

Use the token in requests / Usar el token en peticiones:
```
Authorization: Bearer <JWT_TOKEN>
```

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
  "rol": "seller"
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

 mvn clean test
 
<img width="1443" height="495" alt="imagen" src="https://github.com/user-attachments/assets/bc83cb05-6f3b-4b65-877a-2b6f06909957" />
<img width="1116" height="343" alt="imagen" src="https://github.com/user-attachments/assets/cee60a47-d718-46c0-bbb4-71b985a5a8cd" />
<img width="1839" height="933" alt="imagen" src="https://github.com/user-attachments/assets/70b90dea-2499-412b-95f7-da4bca7ceb02" />
<img width="1537" height="499" alt="imagen" src="https://github.com/user-attachments/assets/d1e97111-ec1d-4e88-bcfe-9fd365b606a7" />
<img width="1581" height="279" alt="imagen" src="https://github.com/user-attachments/assets/f21c069b-1fbe-4d28-b469-ccdd9be100f1" />
<img width="1580" height="280" alt="imagen" src="https://github.com/user-attachments/assets/c80ca4af-28fe-4f64-83d5-0a2fa68e36cb" />
<img width="1560" height="286" alt="imagen" src="https://github.com/user-attachments/assets/bbc637f9-ab66-444c-9da9-2eda87a357ce" />
<img width="1918" height="758" alt="imagen" src="https://github.com/user-attachments/assets/cf62132b-9446-4b26-85d2-7cddf21e70c3" />
<img width="1805" height="292" alt="imagen" src="https://github.com/user-attachments/assets/60ed06f3-6828-4bb3-81a6-68f8fe92bbd1" />
<img width="1610" height="387" alt="imagen" src="https://github.com/user-attachments/assets/3fa80aef-d275-47d9-93c1-a616e69eb47e" />
<img width="1786" height="435" alt="imagen" src="https://github.com/user-attachments/assets/67d237d7-51e7-4e46-9102-5ab1494e7c26" />
<img width="1711" height="586" alt="imagen" src="https://github.com/user-attachments/assets/1caa6e4a-f7fe-431e-9489-12b25cd3125b" />
<img width="1825" height="643" alt="imagen" src="https://github.com/user-attachments/assets/5f1d5021-1cda-4b74-90e0-dd942bc60cdb" />
<img width="1627" height="353" alt="imagen" src="https://github.com/user-attachments/assets/56bef2b8-a4f2-4413-8149-9b4f13d2bb32" />
<img width="1593" height="573" alt="imagen" src="https://github.com/user-attachments/assets/64a4a840-544d-4cf2-bb70-92156799b42c" />
<img width="1640" height="409" alt="imagen" src="https://github.com/user-attachments/assets/912d2269-45ce-425e-b8ed-83357922da49" />
<img width="1672" height="370" alt="imagen" src="https://github.com/user-attachments/assets/3c4f510c-eea6-492f-bb0f-6dbb8d490fdb" />
<img width="1643" height="699" alt="imagen" src="https://github.com/user-attachments/assets/3bb4461f-822d-43a4-8f60-caabe4e85da1" />
<img width="1919" height="898" alt="imagen" src="https://github.com/user-attachments/assets/5e828d9e-60c9-4083-94fd-eaa8f6c2dc8f" />
<img width="1624" height="601" alt="imagen" src="https://github.com/user-attachments/assets/7be4b23b-fc07-4f0d-9ac2-b2f70ca9b76c" />
<img width="1631" height="695" alt="imagen" src="https://github.com/user-attachments/assets/2319189d-64fd-47a5-bd5c-8584050b52f3" />
<img width="1610" height="602" alt="imagen" src="https://github.com/user-attachments/assets/01d2579e-4106-4024-a258-95adecbfd521" />

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
- Los tests de controller están hechos en modo **standalone** (no levantan todo el contexto).

---

## 9) Troubleshooting / Solución de problemas

- **`Cannot load driver class: org.h2.Driver`**  
  Asegúrate de ejecutar con el *perfil* `chat-h2` o incluir la dependencia H2 en el classpath.  
  Ejemplo: `mvn spring-boot:run -Dspring-boot.run.profiles=chat-h2`

- **Puerto en uso / Port already in use**  
  Cambia `--server.port` o libera el puerto:  
  PowerShell:
  ```powershell
  Get-NetTCPConnection -LocalPort 35000 -State Listen
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
  port: 35000

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
  │    ├─ controller/ (ChatController, endpoints REST)
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
curl -X POST http://localhost:35000/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"ada","password":"school"}'
```

**Weather POST (with token)**
```bash
curl -X POST http://localhost:35000/v1/weather/BOG   -H "Authorization: Bearer <TOKEN>"   -H "Content-Type: application/json"   -d '{"weather":{"temp":17.5,"pressure":994.71,"humidity":61}}'
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
