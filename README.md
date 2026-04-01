# laboratory-TCP

## 1. Цели проекта и технологии (в 3 разделе общая архитектура)
**Laboratory-TCP** — это информационная система для автоматизации ветеринарной клиники, построенная на базе современных enterprise-стандартов.

### Основные цели:
*   **Демонстрация и тренировка работы со Spring Security и сохранением ролей в JWT:** Реализация надежной системы аутентификации и авторизации (RBAC) с использованием Stateless-токенов и механизма Blacklist для выхода из системы.
*   **Гибридный Frontend (Thymeleaf + React):** Использование классического серверного рендеринга для структуры сайта и внедрение реактивных SPA-модулей (React + Vite) для сложных интерфейсов управления данными.
*   **Управление данными и миграции:** Организация персистентного слоя с использованием Spring Data JPA и сохранение структуры БД через Liquibase.
*   **Контейнеризация:** Полная изоляция приложения и окружения через Docker для упрощения развертывания на разных ПК и версиях JDK, maven и других инструментов Spring Boot.

### Технологический стек:
*   **Backend:** Java 21, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, Liquibase, PostgreSQL.
*   **Frontend:** React 18, Vite, Thymeleaf, CSS3.
*   **DevOps:** Docker, Docker Compose.
## 2. Запуск проекта в контейнере
 - клонируйте репозиторий: https://github.com/HyperKa/laboratory-TCP.git
 - убедитесь, что вы в корне проекта и соберите контейнеры: docker-compose up --build -d
 - доступ по localhost:8080; для доступа к БД желательно использовать pgAdmin4, логин - postgres, пароль - 1234 
 - При запуске автоматически создаются тестовые сущности в таблицах Admin (логин - admin, пароль - admin), Doctor (doctor1, 1234) и Client (client1, 1234)

## 3. Архитектура приложения: 

### Схема базы данных (ER-диаграмма)
Используется postgresql. Ниже представлена связь основных сущностей системы:

```mermaid
erDiagram
    CLIENT ||--o{ APPOINTMENT_RECORD : "записывается"
    CLIENT ||--o{ ANALYSIS_RESULT : "получает"
    DOCTOR ||--o{ APPOINTMENT_RECORD : "принимает"
    DOCTOR ||--o{ DISEASE_HISTORY : "ведет"
    DISEASE_HISTORY ||--o{ APPOINTMENT_RECORD : "состоит из"

    CLIENT {
        int id PK
        string login "unique"
        string password "BCrypt"
        string firstName
        string lastName
        int age
        string gender
        string address
        string passport
        string role "CLIENT"
    }

    DOCTOR {
        long id PK
        string login "unique"
        string firstName
        string lastName
        string specialization
        string experience
        string role "DOCTOR"
    }

    APPOINTMENT_RECORD {
        int record_id PK
        int client_id FK
        int doctor_id FK
        int disease_history_id FK
        date appointment_date
        time appointment_time
        string service_name
    }

    DISEASE_HISTORY {
        int record_id PK
        int client_id FK
        long doctor_id FK
        string disease "Диагноз"
        timestamp start_date
        timestamp end_date
        string profession "Специальность врача"
    }

    ANALYSIS_RESULT {
        long record_id PK
        int client_id FK
        string research_file "Путь к файлу"
        date analysis_date
    }


```

### Общая структура бекенда представлена на диаграмме классов:

```mermaid
classDiagram
    direction TB
    class SecurityModule {
        +JwtAuthenticationFilter
        +JwtTokenService
        +UserDetailsServiceImpl
    }
    class AppointmentModule {
        +AppointmentController
        +AppointmentService
        +AppointmentRepository
    }
    class HealthRecordModule {
        +DiseaseHistoryService
        +AnalysisResultService
    }
    
    SecurityModule ..> AppointmentModule : "Protect API"
    AppointmentModule --> HealthRecordModule : "Update History"
```
### Диаграмма последовательностей (регистрация клиента)
```mermaid
sequenceDiagram
    participant C as Client (Browser)
    participant AC as AuthController
    participant US as UserDetailsServiceImpl
    participant R as ClientRepository
    participant JWT as JwtTokenService

    C->>AC: POST /auth/register/client (DTO)
    AC->>US: registerClient(dto)
    US->>US: BCrypt.encode(password)
    US->>R: save(Client)
    AC->>JWT: generateToken(login)
    JWT-->>AC: Token String
    AC-->>C: Set-Cookie: jwt=token (HttpOnly)
    C->>C: Redirect to /dashboard

```

### Диаграмма последовательностей (запись на прием)
```mermaid
sequenceDiagram
participant C as Client (React)
participant AR as AppointmentController
participant AS as AppointmentService
participant DH as DiseaseHistoryRepository
participant DB as Database

    C->>AR: POST /api/v1/appointment-records (JSON)
    AR->>AS: createRecordFromDTO(dto, username)
    AS->>DB: Find Client by login
    AS->>DH: Find Active History (by Client & Service)
    alt History not found
        AS->>DH: Create new DiseaseHistory entry
    end
    AS->>DB: Save AppointmentRecord
    AS-->>AR: AppointmentRecordDTO
    AR-->>C: 201 Created
```

### Фронтенд состоит из React-блоков, внедренных в Thymeleaf контейнеры на html:
- PatientManager: Просмотр и редактирование данных владельцев животных (Доступ: ADMIN/DOCTOR).
- AppointmentRecordManager: Управление очередью записей в реальном времени.
- DiseaseHistoryManager: Интерактивная лента болезни с фильтрацией по ролям.
- AnalysisResultManager: Модуль загрузки и просмотра лабораторных исследований.