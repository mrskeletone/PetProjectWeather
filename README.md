# PetProjectWeather

Сервис для получения погоды с OpenWeatherMap, JWT-аутентификацией и логированием запросов в ClickHouse.

## 🧰 Стек

- **Java 26** / **Spring Boot 4.0.6**
- **Spring MVC** — REST API
- **Spring Security** — аутентификация и доступ
- **JWT (jjwt 0.13.0)** — access + refresh токены
- **PostgreSQL** — основные данные (JPA/Hibernate)
- **ClickHouse** — аналитическое логирование запросов
- **Redis** (зависимость добавлена) — кэш координат городов (готовится)
- **Jackson 3.1.2** — JSON-сериализация
- **Docker Compose** — контейнеры для PostgreSQL и ClickHouse
- **Testcontainers** — тестирование с реальными БД

## 🚀 Быстрый старт

### 1. Требования
- JDK 26
- Docker и Docker Compose
- Maven (или `./mvnw`)

### 2. Ключ погоды
Зарегистрируйтесь на [OpenWeatherMap](https://openweathermap.org/api) и укажите ключ в `application.yml`:
```yaml
weather:
  api-key: ваш_api_ключ
```

### 3. Базы данных
```bash
docker-compose up -d
```
- **PostgreSQL**: `localhost:5432`, БД `petWeather`, пользователь `postgres`, пароль `postgres`
- **ClickHouse**: HTTP `http://localhost:8123`, БД `pet_weather`, `pet_user` / `qwerty`

### 4. Запуск приложения
```bash
./mvnw spring-boot:run
```
JPA создаст таблицы в PostgreSQL.  
Таблица логов `weather_request_logs` в ClickHouse создаётся автоматически.

## 🔐 Аутентификация и JWT

Используется связка **access** (короткий) и **refresh** (долгий, в httpOnly cookie).

| Endpoint | Метод | Описание |
|----------|-------|----------|
| `/auth/register` | POST | Регистрация нового пользователя |
| `/auth/login` | POST | Вход, возвращает access-токен в JSON и refresh-токен в куке |
| `/auth/refresh` | POST | Обновить токены (требуется кука `refresh_token`) |

### Регистрация
```http
POST /auth/register
Content-Type: application/json

{
  "username": "user",
  "password": "secret"
}
```

### Вход
```http
POST /auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "secret"
}
```
**Ответ**:
```json
{ "accessToken": "eyJhbGci..." }
```
+ **httpOnly cookie** `refresh_token` (используется для `/auth/refresh`).

### Защищённые эндпоинты
Передавайте access-токен в заголовке:
```
Authorization: Bearer <accessToken>
```

## 🌤️ Получение погоды

```http
GET /api/weather/{city}
Authorization: Bearer <accessToken>
```
Пример:
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/weather/London
```
Запрос логируется в ClickHouse (город, тело ответа, статус, длительность).

## 📊 Логирование в ClickHouse

Просмотр логов:
```bash
# HTTP-запрос
curl "http://localhost:8123/?query=SELECT+*+FROM+pet_weather.weather_request_logs+FORMAT+JSONEachRow"

# Или через клиент внутри контейнера
docker exec -it pet_weather_clickhouse clickhouse-client -u pet_user --password qwerty -d pet_weather
```

## ⚙️ Конфигурация безопасности

`SecurityConfiguration` настраивает:
- Публичные эндпоинты: `/auth/login`, `/auth/register`, `/auth/refresh`
- Все остальные запросы требуют аутентификации
- Stateless-сессии (без кук JSESSIONID)
- Подключение кастомного `JwtFilter` перед `UsernamePasswordAuthenticationFilter`
- `BCryptPasswordEncoder(12)` для паролей

JWT-фильтр извлекает токен из заголовка `Authorization`, валидирует и устанавливает `SecurityContext`.

## 🧱 Структура проекта

```
src/main/java/org/example/petprojectweather/
├── config/
│   ├── SecurityConfiguration.java   — Spring Security + цепочка фильтров
│   └── ClickHouseConfig.java        — DataSource для ClickHouse
├── controller/
│   ├── LoginController.java         — /auth/**
│   └── WeatherController.java       — /api/weather/**
├── dto/                             — LoginDto, RegisterUser, TokenResponse, UserResponseDto…
├── jwt/
│   ├── JwtFilter.java               — фильтр access-токенов
│   └── JwtService.java              — создание и проверка JWT
├── model/                           — JPA-сущности (User…)
├── service/
│   ├── UserService.java             — регистрация, логин, управление refresh-токенами
│   ├── WeatherService.java          — обращение к OpenWeatherMap
│   └── RequestLogService.java       — запись логов в ClickHouse
└── PetProjectWeatherApplication.java
```

## 🧪 Тестирование

```bash
./mvnw test
```
Использует Testcontainers для PostgreSQL и (при необходимости) ClickHouse.

## 📈 Дальнейшее развитие

- **Redis-кэш** координат городов (зависимость уже есть)
- **Асинхронная запись** логов через Kafka
- **Refresh Token Rotation** и отзыв токенов
- **Мониторинг** через Grafana + ClickHouse

---

Проект создан как учебный полигон для современных Spring-приложений с разными типами хранилищ.
