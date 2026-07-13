# PetProjectWeather

Сервис для получения текущей погоды (Open-Meteo) с JWT-аутентификацией, кэшированием в Redis, асинхронным логированием в ClickHouse и управлением справочником городов.

## 🧰 Стек

- **Java 26** / **Spring Boot 4.0.6**
- **Spring MVC** – REST API
- **Spring Security** – аутентификация, авторизация
- **JWT (jjwt 0.13.0)** – access + refresh токены
- **PostgreSQL** – основные данные (пользователи, города) через JPA/Hibernate
- **ClickHouse** – логирование запросов погоды (асинхронная запись)
- **Redis** – кэширование ответов погоды на 5 минут
- **Jackson 3** – JSON-сериализация, в т.ч. для Redis
- **Docker Compose** – контейнеры для PostgreSQL и ClickHouse
- **Testcontainers** – тестирование с реальными БД

## 🚀 Быстрый старт

### 1. Требования
- JDK 26
- Docker и Docker Compose
- Maven (или `./mvnw`)

### 2. API погоды
Используется **Open-Meteo** – бесплатный, без API-ключа. URL задан в `Utils.URL`:
```java
public static String URL = 
    "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m,wind_speed_10m";
```

### 3. Базы данных и Redis
```bash
docker-compose up -d
```
- **PostgreSQL**: `localhost:5432`, БД `petWeather`, `postgres` / `postgres`
- **ClickHouse**: HTTP `http://localhost:8123`, БД `pet_weather`, `pet_user` / `qwerty`
- **Redis**: должен быть запущен на `localhost:6379` (добавьте контейнер при необходимости)

### 4. Запуск приложения
```bash
./mvnw spring-boot:run
```
JPA создаст таблицы PostgreSQL. Таблица `weather_request_logs` в ClickHouse создаётся автоматически.  
При каждом старте **Redis полностью очищается** (режим разработки).

## 🔐 Аутентификация и JWT

| Эндпоинт | Метод | Описание |
|----------|-------|----------|
| `/auth/register` | POST | Регистрация |
| `/auth/login` | POST | Вход → access-токен (JSON) + refresh-токен (httpOnly cookie) |
| `/auth/refresh` | POST | Обновление пары токенов |

Все остальные эндпоинты требуют заголовок `Authorization: Bearer <accessToken>`.

## 🌤️ Погода и города

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| GET | `/weather/{city}` | Погода для города (поиск координат в БД, кэш Redis 5 мин) |
| GET | `/weather/cities` | Список всех городов |
| POST | `/weather` | Добавить город |
| DELETE | `/weather/{city}` | Удалить город |

### Пример: добавить город и получить погоду

```http
POST /weather
Authorization: Bearer <token>
Content-Type: application/json

{ "name": "Moscow", "latitude": 55.75, "longitude": 37.62 }
```

```http
GET /weather/Moscow
Authorization: Bearer <token>
```

Ответ — JSON с температурой и скоростью ветра (структура `WeatherCity`).

## 🧠 Кэширование в Redis

- **Ключ**: `city:{название_города}`
- **TTL**: 5 минут
- **Сериализация**: `GenericJacksonJsonRedisSerializer` с включённым `DefaultTyping` для полиморфной десериализации `WeatherCity`.
- При старте Redis очищается (`flushDb`) через `ApplicationRunner` (только для разработки).
- Если данные есть в кэше – вызов Open-Meteo не производится, логирование не происходит.

## 📊 Асинхронное логирование в ClickHouse

Сервис `WeatherLogService` аннотирован `@Async` – запись лога выполняется в отдельном потоке.  
Таблица `weather_request_logs` содержит:
- время запроса
- город
- данные ответа (JSON)
- статус успеха

Просмотр логов:
```bash
curl "http://localhost:8123/?query=SELECT+*+FROM+pet_weather.weather_request_logs+FORMAT+JSONEachRow"
```

## ⚙️ Конфигурация безопасности

`SecurityConfiguration`:
- Публичные: `/auth/**`
- Stateless, без сессий
- `JwtFilter` извлекает access-токен и устанавливает `SecurityContext`
- `BCryptPasswordEncoder(12)`

## 🧱 Структура проекта

```
src/main/java/org/example/petprojectweather/
├── config/
│   ├── SecurityConfiguration.java   – настройка Spring Security
│   ├── ClickHouseConfig.java        – DataSource для ClickHouse
│   └── RedisConfiguration.java      – RedisTemplate, очистка при старте
├── controller/
│   ├── LoginController.java         – /auth/**
│   └── WeatherController.java       – /weather/**
├── dto/                             – DTO (LoginDto, RegisterUser, TokenResponse, CityDto,
│                                       WeatherCity, WeatherResponseAPIOpenMeteo, WeatherLog…)
├── entity/                          – JPA-сущности (User, City, Role)
├── handler/                         – глобальный обработчик ошибок
├── jwt/
│   ├── JwtFilter.java               – фильтр access-токенов
│   └── JwtHelper.java               – создание и проверка JWT
├── mapper/                          – мапперы (CityMapper)
├── repository/                      – UserRepository, CityRepository, WeatherLogRepository
├── service/
│   ├── UserService.java
│   ├── WeatherAPI.java              – интерфейс получения погоды
│   ├── WeatherOpenMeteoServiceImp.java – реализация с RestClient + Redis кэш
│   ├── CityService.java             – управление городами
│   └── WeatherLogService.java       – асинхронное сохранение логов
├── Utils.java                       – константы (URL API)
└── PetProjectWeatherApplication.java
```

## 🧪 Тестирование

```bash
./mvnw test
```
Используются Testcontainers для PostgreSQL и ClickHouse.

## 📈 Планы

- Graceful-очистка Redis при остановке
- Метрики запросов к API и кэшу
- Асинхронная очередь логов (Kafka) для больших нагрузок
