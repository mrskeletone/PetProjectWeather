```markdown
# PetProjectWeather

Сервис для получения текущей погоды (Open-Meteo), JWT-аутентификация и логирование запросов в ClickHouse.

## 🧰 Стек

- **Java 26** / **Spring Boot 4.0.6**
- **Spring MVC** – REST API
- **Spring Security** – аутентификация и авторизация
- **JWT (jjwt 0.13.0)** – access + refresh токены
- **PostgreSQL** – основные данные (JPA/Hibernate)
- **ClickHouse** – аналитическое логирование запросов
- **Redis** – кэш (зависимость добавлена, готовится к использованию)
- **Jackson 3.1.2** – JSON-сериализация
- **Docker Compose** – контейнеры для PostgreSQL и ClickHouse
- **Testcontainers** – тестирование с реальными БД

## 🚀 Быстрый старт

### 1. Требования
- JDK 26
- Docker и Docker Compose
- Maven (или `./mvnw`)

### 2. API погоды
Используется **Open-Meteo** – бесплатный, без API-ключа.  
URL и параметры заданы в классе `Utils`:

```java
public static String URL =
    "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m,wind_speed_10m";
```

Для работы достаточно передать координаты города.

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
Таблица `weather_request_logs` в ClickHouse создаётся автоматически.

## 🔐 Аутентификация и JWT

| Endpoint | Метод | Описание |
|----------|-------|----------|
| `/auth/register` | POST | Регистрация нового пользователя |
| `/auth/login` | POST | Вход, возвращает access-токен в JSON и refresh-токен в httpOnly cookie |
| `/auth/refresh` | POST | Обновление токенов (требуется кука `refresh_token`) |

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
+ **httpOnly cookie** `refresh_token`.

### Защищённые эндпоинты
Передавайте access-токен в заголовке:
```
Authorization: Bearer <accessToken>
```

## 🌤️ Получение погоды

Эндпоинт принимает координаты (или город, если реализован геокодер).  
Пример для прямых координат:

```http
GET /api/weather?lat=55.75&lon=37.62
Authorization: Bearer <accessToken>
```

Ответ – JSON с текущей температурой и скоростью ветра.

Каждый запрос логируется в ClickHouse (город/координаты, тело ответа, статус, длительность).

## 🗂️ Логирование в ClickHouse

Просмотр логов:
```bash
curl "http://localhost:8123/?query=SELECT+*+FROM+pet_weather.weather_request_logs+FORMAT+JSONEachRow"
```
или через контейнер:
```bash
docker exec -it pet_weather_clickhouse clickhouse-client -u pet_user --password qwerty -d pet_weather
```

## ⚙️ Конфигурация безопасности

`SecurityConfiguration`:
- Публичные эндпоинты: `/auth/**`
- Все остальные запросы требуют аутентификации
- Stateless-сессии
- `JwtFilter` проверяет access-токен и устанавливает `SecurityContext`
- `BCryptPasswordEncoder(12)` для паролей

## 🧱 Структура проекта

```
src/main/java/org/example/petprojectweather/
├── config/                     – SecurityConfiguration, ClickHouseConfig
├── controller/                 – LoginController, WeatherController
├── dto/                        – LoginDto, RegisterUser, TokenResponse, UserResponseDto…
├── entity/                     – JPA-сущности (User…)
├── handler/                    – обработчики (например, глобальный exception handler)
├── jwt/                        – JwtFilter, JwtService
├── mapper/                     – мапперы между DTO и сущностями
├── repository/                 – Spring Data JPA репозитории
├── service/                    – UserService, WeatherService, RequestLogService
├── Utils.java                  – константы (URL API)
└── PetProjectWeatherApplication.java
```

## 🧪 Тестирование

```bash
./mvnw test
```
Использует Testcontainers для временных экземпляров PostgreSQL и ClickHouse.

## 📈 Дальнейшие планы

- **Redis** – кэширование координат городов
- **Асинхронная запись логов** – очередь Kafka
- **Refresh Token Rotation**
- **Мониторинг** – Grafana + ClickHouse
```
