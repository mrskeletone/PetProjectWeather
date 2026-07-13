# PetProjectWeather

Сервис для получения текущей погоды (Open-Meteo), управления списком городов, JWT-аутентификацией и логированием запросов в ClickHouse.

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
JPA создаст таблицы в PostgreSQL (включая `City`).  
Таблица `weather_request_logs` в ClickHouse создаётся автоматически.

## 🔐 Аутентификация и JWT

| Endpoint | Метод | Описание |
|----------|-------|----------|
| `/auth/register` | POST | Регистрация нового пользователя |
| `/auth/login` | POST | Вход, возвращает access-токен в JSON и refresh-токен в httpOnly cookie |
| `/auth/refresh` | POST | Обновление токенов (требуется кука `refresh_token`) |

Все остальные эндпоинты требуют заголовок:  
`Authorization: Bearer <accessToken>`

## 🌤️ Погода и города

Все операции под `/weather` защищены аутентификацией.

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/weather/{city}` | Получить погоду для города (ищет координаты в БД) |
| GET | `/weather/cities` | Получить список всех сохранённых городов |
| POST | `/weather` | Добавить новый город в справочник |
| DELETE | `/weather/{city}` | Удалить город по названию |

### Примеры

**Добавить город:**
```http
POST /weather
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Moscow",
  "latitude": 55.75,
  "longitude": 37.62
}
```

**Получить погоду:**
```http
GET /weather/Moscow
Authorization: Bearer <token>
```
Ответ – JSON с температурой и скоростью ветра.

**Список городов:**
```http
GET /weather/cities
Authorization: Bearer <token>
```

**Удалить город:**
```http
DELETE /weather/Moscow
Authorization: Bearer <token>
```

## 🗂️ Логирование в ClickHouse

Каждый вызов погоды сохраняется в `weather_request_logs` (название города, ответ API, длительность).

Просмотр логов:
```bash
curl "http://localhost:8123/?query=SELECT+*+FROM+pet_weather.weather_request_logs+FORMAT+JSONEachRow"
```
или через контейнер:
```bash
docker exec -it pet_weather_clickhouse clickhouse-client -u pet_user --password qwerty -d pet_weather
```

## ⚙️ Безопасность

`SecurityConfiguration`:
- Публичные: `/auth/**`
- Остальные требуют аутентификации
- Stateless-сессии
- `JwtFilter` проверяет access-токен
- `BCryptPasswordEncoder(12)`

## 🧱 Структура проекта

```
src/main/java/org/example/petprojectweather/
├── config/                     – SecurityConfiguration, ClickHouseConfig
├── controller/                 – LoginController, WeatherController
├── dto/                        – LoginDto, RegisterUser, TokenResponse, CityDto, WeatherCity…
├── entity/                     – User, City
├── handler/                    – глобальные обработчики исключений
├── jwt/                        – JwtFilter, JwtService
├── mapper/                     – мапперы DTO ↔ Entity
├── repository/                 – UserRepository, CityRepository
├── service/
│   ├── UserService
│   ├── WeatherAPI              – вызов Open-Meteo
│   ├── CityService             – управление городами
│   └── RequestLogService       – логирование в ClickHouse
├── Utils.java                  – URL Open-Meteo
└── PetProjectWeatherApplication.java
```

## 🧪 Тестирование

```bash
./mvnw test
```

## 📈 Планы

- **Redis** – кэширование координат городов
- **Асинхронная запись логов** – Kafka
- **Refresh Token Rotation**
- **Мониторинг** – Grafana + ClickHouse
