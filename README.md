[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aAsnFjED)
# API REST - Juego Hangman (Ahorcado)

API REST desarrollada en Spring Boot para gestionar el juego Hangman (Ahorcado), incluyendo gestión de jugadores, partidas, puntajes y palabras.

## 📋 Descripción

Esta aplicación permite gestionar un juego de Hangman completo con las siguientes funcionalidades:

- **Gestión de Jugadores**: Alta, baja y modificación de jugadores
- **Gestión de Partidas**: Registro de partidas jugadas con resultados y puntajes
- **Sistema de Puntajes**: Cálculo automático de puntajes según las reglas del juego
- **Grilla de Puntajes**: Consulta de estadísticas y puntajes por jugador
- **Gestión de Palabras**: Lista de palabras disponibles y control de uso


### Inicialización de Datos

El archivo `data.sql` carga automáticamente 20 palabras en español (de al menos 10 caracteres) al iniciar la aplicación.

## 🚀 Ejecución

#### Acceso a la aplicación
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306
  - Usuario: `root`
  - Contraseña: `root`
  - Base de datos: `demobase`


## 🎮 Reglas del Juego

### Cálculo de Puntajes

- **Palabra completa adivinada**: 20 puntos
- **Palabra no completada**: 1 punto por cada letra correcta encontrada
- **Intentos disponibles**: 7 intentos por partida

### Gestión de Palabras

- Las palabras se marcan automáticamente como "utilizadas" cuando se inicia una partida
- Solo se seleccionan palabras no utilizadas para nuevas partidas
- Las palabras tienen al menos 10 caracteres

## 📡 Endpoints de la API

### 1. Gestión de Jugadores

#### 1.1 Obtener todos los jugadores
```http
GET /api/players
```

**Descripción:** Obtiene la lista completa de todos los jugadores registrados en el sistema.

**Requisitos:**
- No requiere parámetros
- No requiere autenticación

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/players \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "fecha": "2025-01-15"
  }
]
```

#### 1.2 Obtener jugador por ID
```http
GET /api/players/{id}
```

**Descripción:** Obtiene la información de un jugador específico por su identificador único.

**Requisitos:**
- `id` (path parameter): ID del jugador (Long, requerido)
- El jugador debe existir en el sistema

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/players/1 \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "fecha": "2025-01-15"
}
```

#### 1.3 Crear nuevo jugador
```http
POST /api/players
```

**Descripción:** Crea un nuevo jugador en el sistema. Si no se proporciona la fecha, se asigna automáticamente la fecha actual.

**Requisitos:**
- `nombre` (String, requerido): Nombre del jugador
- `fecha` (LocalDate, opcional): Fecha de registro. Si no se proporciona, se usa la fecha actual

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María García",
    "fecha": "2025-01-20"
  }'
```

**Respuesta:**
```json
{
  "id": 2,
  "nombre": "María García",
  "fecha": "2025-01-20"
}
```

#### 1.4 Actualizar jugador
```http
PUT /api/players/{id}
```

**Descripción:** Actualiza la información de un jugador existente. Solo se actualizan los campos proporcionados.

**Requisitos:**
- `id` (path parameter): ID del jugador a actualizar (Long, requerido)
- `nombre` (String, requerido): Nuevo nombre del jugador
- `fecha` (LocalDate, opcional): Nueva fecha. Si no se proporciona, mantiene la fecha actual

**Ejemplo con curl:**
```bash
curl -X PUT http://localhost:8080/api/players/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez Actualizado",
    "fecha": "2025-01-15"
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez Actualizado",
  "fecha": "2025-01-15"
}
```

#### 1.5 Eliminar jugador
```http
DELETE /api/players/{id}
```

**Descripción:** Elimina un jugador del sistema. Esta operación no se puede deshacer.

**Requisitos:**
- `id` (path parameter): ID del jugador a eliminar (Long, requerido)
- El jugador debe existir en el sistema

**Ejemplo con curl:**
```bash
curl -X DELETE http://localhost:8080/api/players/1 \
  -H "Content-Type: application/json"
```

**Respuesta:** `204 No Content`

---

### 2. Gestión de Partidas

#### 2.1 Iniciar nueva partida
```http
POST /api/games/start/{playerId}
```

**Descripción:** Inicia una nueva partida del juego Hangman para un jugador. Selecciona automáticamente una palabra aleatoria no utilizada y crea el estado inicial de la partida.

**Requisitos:**
- `playerId` (path parameter): ID del jugador (Long, requerido)
- El jugador debe existir en el sistema
- Debe haber palabras disponibles no utilizadas en el sistema
- Si el jugador ya tiene una partida en curso con la misma palabra, retorna el estado existente

**Comportamiento:**
- Selecciona una palabra aleatoria de las no utilizadas
- Marca la palabra como utilizada
- Crea un registro de partida en curso
- Inicializa con 7 intentos disponibles
- Retorna la palabra oculta con todos los caracteres como "_"

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/games/start/1 \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
{
  "palabraOculta": "___________",
  "letrasIntentadas": [],
  "intentosRestantes": 7,
  "palabraCompleta": false,
  "puntajeAcumulado": 0
}
```

#### 2.2 Realizar un intento de adivinar letra
```http
POST /api/games/guess
```

**Descripción:** Permite realizar un intento de adivinar una letra en la partida en curso. El sistema mantiene automáticamente el estado completo de la partida, incluyendo las letras ya intentadas y los intentos restantes. **No es necesario enviar la palabra**, el sistema identifica automáticamente la partida en curso más reciente del jugador.

**Requisitos:**
- `idJugador` (Long, requerido): ID del jugador que realiza el intento
- `letra` (Character, requerido): Letra a intentar adivinar (puede ser mayúscula o minúscula)
- El jugador debe tener una partida en curso activa (iniciada con `/api/games/start/{playerId}`)
- Si el jugador no tiene partida en curso, se retornará un error

**Comportamiento:**
- Busca automáticamente la partida en curso más reciente del jugador
- Si la letra ya fue intentada, retorna el estado actual sin cambios (no descuenta intentos)
- Si la letra es correcta, la revela en la palabra oculta
- Si la letra es incorrecta, descuenta un intento
- Cuando la palabra se completa o se agotan los intentos, guarda automáticamente la partida en el historial
- Retorna el estado actualizado del juego

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/games/guess \
  -H "Content-Type: application/json" \
  -d '{
    "idJugador": 1,
    "letra": "A"
  }'
```

**Respuesta (estado del juego):**
```json
{
  "palabraOculta": "P__G__MAD_R",
  "letrasIntentadas": ["A", "P", "R", "G", "M", "D"],
  "intentosRestantes": 5,
  "palabraCompleta": false,
  "puntajeAcumulado": 0
}
```

**Respuesta (si el juego terminó - palabra completa):**
```json
{
  "palabraOculta": "PROGRAMADOR",
  "letrasIntentadas": ["P", "R", "O", "G", "A", "M", "D"],
  "intentosRestantes": 3,
  "palabraCompleta": true,
  "puntajeAcumulado": 20
}
```

**Notas importantes:**
- **No es necesario enviar la palabra**: El sistema identifica automáticamente la partida en curso del jugador
- Debes iniciar una partida con `/api/games/start/{playerId}` antes de usar este endpoint
- El sistema mantiene automáticamente el estado de la partida en curso
- Si intentas una letra que ya fue intentada, no se descuenta un intento
- Los intentos solo se descuentan cuando la letra es incorrecta
- Cuando el juego termina (palabra completa o sin intentos), se guarda automáticamente en el historial de partidas
- Si el jugador no tiene partida en curso, se retornará un error

#### 2.3 Obtener todas las partidas
```http
GET /api/games
```

**Descripción:** Obtiene el historial completo de todas las partidas finalizadas (ganadas o perdidas) en el sistema. No incluye partidas en curso.

**Requisitos:**
- No requiere parámetros
- No requiere autenticación

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/games \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "idJugador": 1,
    "nombreJugador": "Juan Pérez",
    "resultado": "GANADO",
    "puntaje": 20,
    "fechaPartida": "2025-01-20T10:30:00",
    "palabra": "PROGRAMADOR"
  }
]
```

#### 2.4 Obtener partidas de un jugador
```http
GET /api/games/player/{playerId}
```

**Descripción:** Obtiene el historial de todas las partidas finalizadas de un jugador específico, ordenadas por fecha descendente (más recientes primero).

**Requisitos:**
- `playerId` (path parameter): ID del jugador (Long, requerido)
- El jugador debe existir en el sistema

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/games/player/1 \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "idJugador": 1,
    "nombreJugador": "Juan Pérez",
    "resultado": "GANADO",
    "puntaje": 20,
    "fechaPartida": "2025-01-20T10:30:00",
    "palabra": "PROGRAMADOR"
  }
]
```

---

### 3. Grilla de Puntajes

#### 3.1 Obtener grilla de puntajes de todos los jugadores
```http
GET /api/scoreboard
```

**Descripción:** Obtiene la grilla de puntajes de todos los jugadores, incluyendo estadísticas agregadas como puntaje total, partidas jugadas, ganadas y perdidas. Los resultados están ordenados por puntaje total descendente.

**Requisitos:**
- No requiere parámetros
- No requiere autenticación

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/scoreboard \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "idJugador": 1,
    "nombreJugador": "Juan Pérez",
    "puntajeTotal": 45,
    "partidasJugadas": 3,
    "partidasGanadas": 2,
    "partidasPerdidas": 1
  },
  {
    "idJugador": 2,
    "nombreJugador": "María García",
    "puntajeTotal": 20,
    "partidasJugadas": 1,
    "partidasGanadas": 1,
    "partidasPerdidas": 0
  }
]
```

#### 3.2 Obtener puntajes de un jugador específico
```http
GET /api/scoreboard/player/{playerId}
```

**Descripción:** Obtiene las estadísticas y puntajes de un jugador específico, incluyendo su puntaje total, número de partidas jugadas, ganadas y perdidas.

**Requisitos:**
- `playerId` (path parameter): ID del jugador (Long, requerido)
- El jugador debe existir en el sistema

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/scoreboard/player/1 \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
{
  "idJugador": 1,
  "nombreJugador": "Juan Pérez",
  "puntajeTotal": 45,
  "partidasJugadas": 3,
  "partidasGanadas": 2,
  "partidasPerdidas": 1
}
```

---

### 4. Gestión de Palabras

#### 4.1 Obtener lista de todas las palabras con su estado de uso
```http
GET /api/words
```

**Descripción:** Obtiene la lista completa de todas las palabras disponibles en el sistema, junto con su estado de uso (si ya fueron utilizadas en alguna partida o no).

**Requisitos:**
- No requiere parámetros
- No requiere autenticación

**Ejemplo con curl:**
```bash
curl -X GET http://localhost:8080/api/words \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "palabra": "PROGRAMADOR",
    "utilizada": true
  },
  {
    "id": 2,
    "palabra": "COMPUTADORA",
    "utilizada": false
  },
  {
    "id": 3,
    "palabra": "TECNOLOGIA",
    "utilizada": false
  }
]
```

---

## 🔄 Flujo de Uso Típico

### Ejemplo completo: Crear jugador y jugar una partida

```bash
# 1. Crear un nuevo jugador
curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos López",
    "fecha": "2025-01-20"
  }'

# Respuesta: {"id": 3, "nombre": "Carlos López", "fecha": "2025-01-20"}

# 2. Iniciar una nueva partida
curl -X POST http://localhost:8080/api/games/start/3 \
  -H "Content-Type: application/json"

# Respuesta incluye la palabra oculta y 7 intentos disponibles

# 3. Realizar intentos de adivinar letras (el sistema mantiene el estado automáticamente)
# Nota: No es necesario enviar la palabra, solo el idJugador y la letra
curl -X POST http://localhost:8080/api/games/guess \
  -H "Content-Type: application/json" \
  -d '{
    "idJugador": 3,
    "letra": "P"
  }'

# Continuar con más intentos (el sistema recuerda las letras anteriores y la palabra)
curl -X POST http://localhost:8080/api/games/guess \
  -H "Content-Type: application/json" \
  -d '{
    "idJugador": 3,
    "letra": "R"
  }'

# Más intentos...
curl -X POST http://localhost:8080/api/games/guess \
  -H "Content-Type: application/json" \
  -d '{
    "idJugador": 3,
    "letra": "O"
  }'

# 4. Consultar las partidas del jugador
curl -X GET http://localhost:8080/api/games/player/3 \
  -H "Content-Type: application/json"

# 5. Consultar el puntaje del jugador
curl -X GET http://localhost:8080/api/scoreboard/player/3 \
  -H "Content-Type: application/json"

# 6. Ver todas las palabras y su estado
curl -X GET http://localhost:8080/api/words \
  -H "Content-Type: application/json"
```

---

## 📊 Modelo de Datos

### Entidad: Player
- `id` (Long): Identificador único
- `nombre` (String): Nombre del jugador
- `fecha` (LocalDate): Fecha de registro

### Entidad: Game
- `id` (Long): Identificador único
- `jugador` (Player): Referencia al jugador
- `resultado` (String): "GANADO" o "PERDIDO"
- `puntaje` (Integer): Puntaje obtenido
- `fechaPartida` (LocalDateTime): Fecha y hora de la partida
- `palabra` (Word): Palabra utilizada en la partida

### Entidad: Word
- `id` (Long): Identificador único
- `palabra` (String): La palabra (mínimo 10 caracteres)
- `utilizada` (Boolean): Indica si la palabra ya fue usada

### Entidad: GameInProgress
- `id` (Long): Identificador único
- `jugador` (Player): Referencia al jugador
- `palabra` (Word): Palabra de la partida en curso
- `letrasIntentadas` (String): Letras intentadas almacenadas como String separado por comas
- `intentosRestantes` (Integer): Número de intentos restantes
- `fechaInicio` (LocalDateTime): Fecha y hora de inicio de la partida

---

## 📝 Notas Adicionales

- La base de datos H2 se reinicia cada vez que se inicia la aplicación
- Para persistencia permanente, configura MySQL
- Las palabras se cargan automáticamente desde `data.sql` al iniciar
- Una vez que una palabra es utilizada, no se volverá a seleccionar para nuevas partidas
- El sistema calcula automáticamente los puntajes según las reglas establecidas
- **Gestión de estado**: El endpoint `/api/games/guess` mantiene automáticamente el estado de las partidas en curso:
  - Guarda las letras ya intentadas
  - Mantiene el contador de intentos restantes
  - Solo descuenta intentos cuando la letra es incorrecta
  - Si intentas una letra ya usada, retorna el estado actual sin cambios
  - Al terminar la partida, se guarda automáticamente en el historial y se elimina de las partidas en curso

---

## 🔗 Enlaces Útiles

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api-docs
- **H2 Console** (si está habilitada): http://localhost:8080/h2-console

---


## 🧪 REQUERIMIENTOS A COMPLETAR

| Categoría | Requerimiento | Puntos |
|-----------|---------------|--------|
| **Services** | GameService.startGame | 25 |
| **Services** | GameService.makeGuess | 25 |
| **Docker** | Completar Dockerfile | 5 |
| **Docker** | Completar Docker-Compose | 15 |
| **Test Unitarios** | GameServiceTest | 10 |
| **Test Unitarios** | ScoreboardServiceTest | 10 |
| **Test Unitarios** | WordServiceTest | 10 |
| | **TOTAL** | **100** |

