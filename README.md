# Econexion Lab – REST API (Users + Weather + JWT)
**(EN / ES)**

Spring Boot REST API for lab practice. It now includes:
- In‑memory CRUD for *users* (profile `lab`).
- Weather endpoint (`/v1/weather/{city}`) with JWT protection for POST.
- Basic login (`/api/auth/login`) issuing JWT tokens.

---

## 1) Overview / Resumen

**EN**: This lab project demonstrates Spring Boot REST with profiles, in‑memory persistence, and authentication using JWT.  
**ES**: Este proyecto de laboratorio demuestra REST con Spring Boot, perfiles, persistencia en memoria y autenticación con JWT.

---

## 2) Requirements / Requisitos
- Java **21**
- Maven **3.9+**
- Internet for dependencies / Internet para dependencias

---

## 🚀 Ejecución del Proyecto (Econexia)

Antes de ejecutar el backend, **asegúrate de tener una base de datos PostgreSQL en funcionamiento**.  
Puedes levantarla fácilmente con Docker o usar una instalación local.

---

### 🐘 1) Crear base de datos PostgreSQL

#### Opción A — Usando Docker
Ejecuta este comando para levantar un contenedor de PostgreSQL:

```bash
docker run -d \
  --name postgres-econexion \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=12345 \
  -e POSTGRES_DB=econexion \
  -p 5432:5432 \
  postgres:15
  ```
#### Opción B — Usando una instalación local
Si ya tienes PostgreSQL instalado, simplemente crea la base:

```bash
CREATE DATABASE econexion;

  ```


### ⚙️ 2) Construir y ejecutar la aplicación

#### Opción A — Usando Docker
Primero asegúrate de haber construido la imagen (si no existe):

```bash
docker build -t econexion-lab .

  ```
  Luego ejecuta el contenedor:

  ```
  docker run -d -p 35000:35000 --name econexion econexion-lab

  ```
  La aplicación se ejecutará en:
🟢 http://localhost:35000

#### Opción B — Ejecutar el .jar directamente
Si prefieres hacerlo sin Docker:

  ```
java -jar target/econexion-1.0-SNAPSHOT.jar

  ```
La app también quedará disponible en http://localhost:35000


### 📖 3) Swagger (documentación de la API)

Una vez la aplicación esté corriendo, abre en el navegador:

http://localhost:35000/swagger-ui/index.html#/


Ahí podrás visualizar y probar todos los endpoints de la API.

## ⚠️ Nota importante

Si el backend no logra conectarse al Postgres, revisa:

- Que el contenedor postgres-econexion esté en ejecución (docker ps)

- Que el puerto 5432 no esté ocupado

- Que las credenciales coincidan con las del application.yml
---

## 4) Endpoints (Users) / Endpoints (Usuarios)

Base path: `/lab/users`

| Method | Path                     | Description (EN)     | Descripción (ES)          |
|--------|--------------------------|----------------------|---------------------------|
| GET    | `/lab/users/allUsers`    | List all users       | Lista todos los usuarios  |
| GET    | `/lab/users/{id}`        | Get user by id       | Obtener usuario por id    |
| POST   | `/lab/users/addUser`     | Create user          | Crear usuario             |
| PUT    | `/lab/users/update/{id}` | Update user          | Actualizar usuario        |
| DELETE | `/lab/users/delete/{id}` | Delete user          | Eliminar usuario          |

**POST JSON** / **Ejemplo POST**
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



## 6) Auth / Autenticación

**Login:**  
`POST /api/auth/login`  
Body:
```json
{ "username": "ada", "password": "school" }
```
Response:
```json
{ "token": "<JWT_TOKEN>" }
```

**Register:**  
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
Response:
```json
{
    "id":"UUID generado aleatoriamente",
    "enterpriseName": "karenCorp",
    "username": "karen Amaya",
    "nit": "1000474431",
    "email": "karen@demo.test",
    "password": <hash>,
    "rol": "seller"
}
```


Use the token in requests:  
Usar el token en peticiones:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 7) Config / Configuración

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

## 8) Quick cURL

**Login**
```bash
curl -X POST http://localhost:35000/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"ada","password":"school"}'
```

**Weather POST (with token)**
```bash
curl -X POST http://localhost:8080/v1/weather/BOG   -H "Authorization: Bearer <TOKEN>"   -H "Content-Type: application/json"   -d '{"weather":{"temp":17.5,"pressure":994.71,"humidity":61}}'
```

---

## 9) Project Layout / Estructura del proyecto

```
src/main/java/io/econexion/
  ├─ lab/users/...
  ├─ weather/...
  ├─ security/
  │    ├─ SecurityConfig.java
  │    ├─ JwtUtil.java
  │    └─ ...
  └─ EconexionLabApplication.java
```

---

## 10) Notes / Notas
- Users CRUD works only in `lab` profile.  
- Weather POST requires JWT.  
- Login endpoint issues JWT using in‑memory user (`ada` / `school`).

---

## 11) License / Licencia
MIT (or your repo license).

— Updated 2025‑09‑14
