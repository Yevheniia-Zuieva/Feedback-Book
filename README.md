# Книга відгуків (Spring Boot MVC / JPA Hibernate ORM / Spring Security / AOP / Centralized Exception Handling / Flyway / Thymeleaf SSR & FreeMarker E-mail)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг, користувачами та додавання/видалення відгуків із повноцінною системою автентифікації, рольової авторизації, централізованого опрацювання винятків та прозорого логування через AOP. Архітектура побудована за гексагональним принципом (Ports & Adapters) у Maven-модулях (`core`, `persistence`, `web`).

У цій версії реалізовано:

- **Обробку доменних винятків у `core`:** Власні runtime-винятки (`InvalidCommentDeleteException`, `CommentTooOldException`, `BookNotFoundException`, `InvalidCommentTextException`) для забезпечення чистоти бізнес-правил.
- **Глобальну обробку помилок на рівні `web`:** REST-обробник `@RestControllerAdvice` (`GlobalApiExceptionHandler`) для перетворення винятків у стандартизовані JSON-відповіді (HTTP 400/404) та MVC-обробник `@ControllerAdvice` (`GlobalMvcExceptionHandler`) для дружніх редіректів у веб-інтерфейсі.
- **Аспектно-орієнтоване програмування (AOP):** Прозоре логування викликів, параметрів, помилок та вимірювання часу виконання сервісних методів через аспективні поради `@Around` (`CommentServiceLoggingAspect`, `ServiceExecutionTimeAspect`).
- **Автоматизоване тестування:** Модульні юніт-тести для `core` (JUnit 5 + Mockito) та інтеграційні тести веб-рівня (Spring Boot Test + MockMvc).

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.x
- **Фреймворк:** Spring Boot 3.3.4
  - `spring-boot-starter-web` (Spring MVC & REST Controllers)
  - `spring-boot-starter-aop` (Spring AOP / AspectJ)
  - `spring-boot-starter-security` (Spring Security & Role-Based Access Control)
  - `spring-boot-starter-data-jpa` (Spring Data JPA & Hibernate 6.5.3)
  - `spring-boot-starter-thymeleaf` (HTML5 SSR вебсторінки)
  - `thymeleaf-extras-springsecurity6` (теги `sec:authorize` у шаблонах)
  - `spring-boot-starter-freemarker` (генерація HTML-листів)
  - `spring-boot-starter-mail` (JavaMailSender)
  - `spring-boot-starter-test` (JUnit 5, Mockito, MockMvc, AssertJ)
- **Міграція БД:** Flyway (`flyway-core` 10.x)
- **СУБД:** H2 Database (файловий режим `./data/booksdb`) + вебконсоль H2 Console
- **Безпека секретів:** `spring-dotenv` (зчитування конфігурацій із `.env`)
- **Шифрування паролів:** `BCryptPasswordEncoder`
- **Вебсервер:** Вбудований Apache Tomcat (порт `8080`)
- **Тестування:** JUnit 5, Mockito, MockMvc, ArchUnit
- **Архітектура:** Трирівнева портивно-адаптерна архітектура (`core` / `persistence` / `web`).

## Структура проєкту

Проєкт розділено на три незалежні модулі для забезпечення відокремлення відповідальностей (Separation of Concerns):

```text
MPF_Labs/
├── pom.xml                  # Батьківський POM (управління версіями та модульними залежностями)
├── core/                    # Доменна модель, порти, бізнес-винятки та сервіси
│   └── src/
│       ├── main/java/sumdu/edu/ua/core/
│       │   ├── domain/      # BookEntity, UserEntity, CommentEntity, Page, PageRequest
│       │   ├── dto/         # RegisterUserDto
│       │   ├── exceptions/  # BookNotFoundException, CommentTooOldException, InvalidCommentDeleteException, InvalidCommentTextException
│       │   ├── port/        # CatalogRepositoryPort, CommentRepositoryPort, UserRepositoryPort, MailPort
│       │   └── service/     # UserService, CommentService (@Transactional)
│       └── test/java/sumdu/edu/ua/
│           ├── architecture/ # CoreArchitectureTest (ArchUnit)
│           └── core/service/ # CommentServiceTest (JUnit 5 + Mockito юніт-тести)
├── persistence/             # Spring Data JPA репозиторії, JPA-адаптери та Flyway-міграції
│   └── src/main/java/sumdu/edu/ua/persistence/
│       ├── adapter/         # JpaCatalogRepositoryAdapter, JpaCommentRepositoryAdapter, JpaUserRepositoryAdapter
│       ├── repository/      # BookRepository, UserRepository, CommentRepository (JpaRepository)
│       └── resources/
│           └── db/migration/ # Flyway-скрипти (V1__init.sql, V3__signup.sql, V4_..., V5_...)
└── web/                     # SecurityConfig, AOP-аспекти, ControllerAdvice, MVC-контролери, Thymeleaf-шаблони
    ├── .env                 # Локальні змінні середовища (SMTP Credentials)
    ├── src/main/java/sumdu/edu/ua/
    │   ├── config/          # SecurityConfig, ServiceConfig, EmailFreemarkerConfig, LanguageConfig, WebConfig
    │   ├── service/         # AppUserDetailsService, EmailTemplateProcessor, MailService, SecurityAuditLogger
    │   └── web/
    │       ├── aop/         # CommentServiceLoggingAspect, ServiceExecutionTimeAspect (Spring AOP)
    │       ├── controller/  # AuthController, BooksController, CommentsController, UserController, CustomErrorController
    │       ├── exception/   # GlobalApiExceptionHandler (@RestControllerAdvice), GlobalMvcExceptionHandler (@ControllerAdvice)
    │       └── AppInit.java # Точка входу Spring Boot (@EnableJpaRepositories, @EntityScan)
    └── src/test/java/sumdu/edu/ua/
        ├── architecture/   # ControllerLocationTest, WebArchitectureTest (ArchUnit)
        └── web/controller/ # CommentDeleteExceptionTest (Spring Boot Test + MockMvc)
```

## Архітектура, винятки, AOP, Security та тестування

### Централізована обробка винятків

- **`GlobalApiExceptionHandler` (`@RestControllerAdvice`):** Перехоплює доменні винятки з `core` та формує стандартизовані JSON-відповіді для REST API:
  - `InvalidCommentDeleteException` → HTTP 400 Bad Request + `{"error": "..."}`
  - `CommentTooOldException` → HTTP 400 Bad Request + `{"error": "..."}`
  - `BookNotFoundException` → HTTP 404 Not Found + `{"error": "..."}`
- **`GlobalMvcExceptionHandler` (`@ControllerAdvice`):** Перехоплює помилки під час виконання HTML-запитів та виконує редірект на сторінку помилки або назад до форми з передачею повідомлення у `RedirectAttributes`.

### Аспектно-орієнтоване програмування (Spring AOP)

- **`CommentServiceLoggingAspect`:** Перехоплює виклики `CommentService.delete(...)` через поінткат `@Around`, здійснює запис параметрів виклику (`bookId`, `commentId`), вимірює час виконання у мілісекундах та логує помилки через SLF4J без засмічення бізнес-коду.
- **`ServiceExecutionTimeAspect`:** Профілює час виконання всіх сервісних методів у пакеті `sumdu.edu.ua.core.service..*`.

### Spring Security та авторизація

- **Двошарова конфігурація:** `@Order(1)` для REST API (`/api/**` — повертає 401 Unauthorized без редіректу на форму) та `@Order(2)` для захисту веб-інтерфейсу (формування сесій, сторінки логіну/реєстрації).
- **Хешування паролів:** Через `BCryptPasswordEncoder`.

### Автоматизоване тестування

- **Юніт-тестування (`CommentServiceTest`):** Ізольована перевірка сервісу в модулі `core` з моками `CommentRepositoryPort` (Mockito). Перевіряється генерація винятків та відсутність звернень до БД (`verifyNoInteractions`) при невалідних ID, `createdAt == null` або застарілих коментарях (>24 год).
- **Інтеграційне тестування (`CommentDeleteExceptionTest`):** Перевірка веб-рівня через MockMvc та `@MockBean`. Переконується, що доменні винятки коректно трансформуються у відповіді зі статусом 400 Bad Request та очікуваним JSON-пейлоадом.

## Розмежування прав доступу (Ролі та Доступ)

| Маршрут / Ресурс | Гості (Unauthenticated) | Користувачі (USER) | Адміністратори (ADMIN) |
|---|---|---|---|
| `/login`, `/register`, `/confirm` | ✅ Дозволено | ✅ Дозволено | ✅ Дозволено |
| `/css/**`, `/js/**`, `/images/**` | ✅ Дозволено | ✅ Дозволено | ✅ Дозволено |
| `GET /books` | ❌ Редирект на `/login` | ✅ Перегляд каталогу | ✅ Перегляд каталогу |
| `GET /books/add`, `POST /books` | ❌ Редирект на `/login` | ⛔ 403 Forbidden | ✅ Додавання книг |
| `GET /comments`, `POST /comments` | ❌ Редирект на `/login` | ✅ Перегляд та додавання | ✅ Перегляд та додавання |
| `POST /comments/delete` | ❌ Редирект на `/login` | ⛔ 403 Forbidden | ✅ Видалення коментарів |
| `GET /api/**` | ⛔ 401 Unauthorized | ⛔ 403 Forbidden | ✅ Доступ до REST API |

## Бізнес-правила та валідація

- **Правило 24 годин для видалення коментарів:** Заборонено видаляти коментарі, створені більше ніж 24 години тому (`Instant.now().minus(24, ChronoUnit.HOURS)`). При порушенні виключається `CommentTooOldException`.
- **Валідація ідентифікаторів:** Значення `bookId` та `commentId` повинні бути більшими за 0, а параметр `createdAt` не може бути `null` (інакше кидається `InvalidCommentDeleteException`).
- **Валідація тексту коментаря:** Довжина тексту коментаря повинна бути від 3 до 500 символів та не містити забороняльних слів (наприклад, `"spam"`), інакше кидається `InvalidCommentTextException`.
- **Аудит та логування:** Кожен виклик сервісу видалення коментарів автоматично профілюється AOP-аспектом.

## Команди запуску та тестування

**Запуск усіх автоматизованих тестів проєкту (Unit, Integration, ArchUnit):**

```bash
mvn clean test
```

**Повна збірка проєкту (усі 4 модулі):**

```bash
mvn clean install
```

**Запуск Spring Boot застосунку:**

```bash
mvn spring-boot:run -pl web
```

Після запуску застосунок доступний за адресою: `http://localhost:8080/books` (або `http://localhost:8080/login`).
