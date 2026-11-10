# Книга відгуків (Spring Boot MVC / JPA Hibernate ORM / Spring Security / Flyway / Thymeleaf SSR & FreeMarker E-mail)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг, користувачами та додавання/перегляду відгуків із повноцінною системою автентифікації та рольової авторизації. Архітектура побудована за гексагональним принципом (Ports & Adapters) у Maven-модулях (`core`, `persistence`, `web`).

У цій версії впроваджено Spring Security із двома незалежними ланцюжками фільтрів (SecurityFilterChain), власний UserDetailsService для завантаження користувачів із бази даних H2, BCrypt-хешування паролів, розмежування прав доступу за ролями (USER, ADMIN), захист REST API (повернення коду 401 Unauthorized), власну сторінку 403 Forbidden, аудит подій безпеки, а також збережено підтримку автоматичного формування та надсилання HTML-листів (FreeMarker + Gmail SMTP) та мультимовності (i18n).

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.16
- **Фреймворк:** Spring Boot 3.3.4
  - `spring-boot-starter-web` (Spring MVC)
  - `spring-boot-starter-security` (Spring Security & Role-Based Access Control)
  - `spring-boot-starter-data-jpa` (Spring Data JPA & Hibernate 6.5.3)
  - `spring-boot-starter-thymeleaf` (HTML5 SSR вебсторінки)
  - `thymeleaf-extras-springsecurity6` (теги `sec:authorize` у шаблонах)
  - `spring-boot-starter-freemarker` (генерація HTML-листів)
  - `spring-boot-starter-mail` (JavaMailSender)
- **Міграція БД:** Flyway (`flyway-core` 10.x)
- **СУБД:** H2 Database (файловий режим `./data/booksdb`) + вебконсоль H2 Console
- **Безпека секретів:** `spring-dotenv` (зчитування конфігурацій із `.env`)
- **Шифрування паролів:** `BCryptPasswordEncoder`
- **Поштовий протокол:** JavaMailSender (Gmail SMTP, порт 587 з TLS)
- **Вебсервер:** Вбудований Apache Tomcat (порт `8080`)
- **Тестування:** JUnit 5, ArchUnit
- **Архітектура:** Трирівнева портивно-адаптерна архітектура (`core` / `persistence` / `web`).

## Структура проєкту

Проєкт розділено на три незалежні модулі для забезпечення Separation of Concerns:

```text
MPF_Labs/
├── pom.xml                  # Батьківський POM (управління версіями, BOM та jackson-bom)
├── core/                    # Доменна модель, порти, кастомний UserDetailsService та бізнес-логіка
│   └── src/main/java/sumdu/edu/ua/core/
│       ├── domain/          # BookEntity, UserEntity, CommentEntity, Page, PageRequest
│       ├── port/            # CatalogRepositoryPort, CommentRepositoryPort, UserRepositoryPort
│       └── service/         # AppUserDetailsService, UserService, CommentService (@Transactional)
├── persistence/             # Spring Data JPA репозиторії, JPA-адаптери та Flyway-міграції
│   └── src/main/java/sumdu/edu/ua/persistence/
│       ├── adapter/         # JpaCatalogRepositoryAdapter, JpaCommentRepositoryAdapter, JpaUserRepositoryAdapter
│       ├── repository/      # BookRepository, UserRepository, CommentRepository (JpaRepository)
│       └── resources/
│           └── db/migration/# Flyway-скрипти (V1__init.sql, V3__signup.sql)
└── web/                     # SecurityConfig, MVC-контролери, Thymeleaf-шаблони, FreeMarker поштовий сервіс
    ├── .env                 # Локальні змінні середовища (SMTP Credentials - у .gitignore)
    ├── src/main/java/sumdu/edu/ua/
    │   ├── config/          # SecurityConfig, EmailFreemarkerConfig, LanguageConfig, WebConfig
    │   ├── service/         # EmailTemplateProcessor, MailService, SecurityAuditLogger
    │   └── web/
    │       ├── AppInit.java # Точка входу Spring Boot (@EnableJpaRepositories, @EntityScan)
    │       └── controller/
    │           ├── AuthController.java     # Форми /login, /register та підтвердження акаунту
    │           ├── BooksController.java    # Каталог книг та надсилання пошти (ADMIN / USER)
    │           ├── CommentsController.java # Управління коментарями (ADMIN / USER)
    │           ├── UserController.java     # Перегляд коментарів користувача
    │           └── ErrorController.java    # Кастомні сторінки помилок (/403, /404)
    └── src/main/resources/
        ├── application.properties # Конфігурації JPA, Flyway, H2 Console, Mail, Security
        ├── mail-templates/
        │   └── new_book.ftl       # HTML-шаблон листа про нову книгу
        ├── messages*.properties   # Ресурсні бандли локалізації (uk, en, fr)
        └── templates/
            ├── books.html         # Каталог книг (із динамічними кнопками за ролями)
            ├── book-form.html     # Форма додавання книги (ADMIN)
            ├── book-comments.html # Перегляд та додавання відгуків
            ├── user-comments.html # Коментарі конкретного користувача
            ├── login.html         # Форма входу Spring Security (/login)
            ├── register.html      # Форма реєстрації нового користувача (/register)
            ├── 403.html           # Сторінка доступ заборонено (403 Forbidden)
            └── fragments/
                └── header.html    # Спільна шапка з відображенням ім'я користувача та кнопкою Logout
```

## Архітектура, JPA/Hibernate, Spring Security, Flyway та i18n

- **Двошарова конфігурація Spring Security (SecurityConfig):**
  - **`@Order(1) API Security Filter Chain (`/api/**`):** Налаштовано спеціально для REST API. Будь-які неавторизовані запити безперешкодно повертають код відповіді 401 Unauthorized (без перенаправлення на сторінку логіну). Доступ до `/api/**` дозволено виключно користувачам з роллю ADMIN.
  - **`@Order(2) Web Security Filter Chain`:** Захищає сторінки UI. Дозволяє безперешкодний доступ (`permitAll`) до статичних ресурсів (`/css/**`, `/js/**`, `/images/**`), сторінок логіну (`/login`), реєстрації (`/register`) та вебконсолі H2 (`/h2-console/**`). Усі неавторизовані спроби відкрити захищені HTML-сторінки перенаправляються на `/login`.
- **Власний UserDetailsService (`AppUserDetailsService`):** Сервіс із модуля `core` завантажує `UserEntity` за електронною поштою (`email`) та перетворює її на об'єкт `UserDetails` із виставленням відповідної ролі (`ROLE_USER` чи `ROLE_ADMIN`) та перевіркою прапорця `verified` (блокування неактивованих акаунтів).
- **BCrypt Парольний Енкодер:** Паролі користувачів зберігаються у базі даних виключно у вигляді хешу `BCryptPasswordEncoder`.
- **ORM & JPA (Hibernate 6):** Доменні моделі перетворено на JPA-сутності з анотаціями `@Entity`, `@Table`, `@Id`, `@GeneratedValue`.
- **Міжтабличні зв'язки:**
  - `BookEntity` (1) ⟷ (N) `CommentEntity`: `@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`.
  - `UserEntity` (1) ⟷ (N) `CommentEntity`: `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`.
  - `CommentEntity` (N) ⟷ (1) `BookEntity` / `UserEntity`: `@ManyToOne(fetch = FetchType.LAZY)`.
- **Автоматична міграція (Flyway):** Версіонування схеми БД здійснюється через Flyway-скрипти `db/migration/V1__init.sql` та `V3__signup.sql`. Скрипт V3 розширює таблицю `users` колонками `email`, `password`, `nickname`, `role`, `verified` та `verification_token`, а також ініціалізує початкові акаунти із захешованими паролями.
- **Серверний рендеринг (Thymeleaf SSR) & Security Tags:** У шаблонах використовуються теги `sec:authorize="hasRole('ADMIN')"` для приховування адміністраторських дій та `sec:authorize="isAuthenticated()"` для відображення профілю та кнопки виходу. Мультимовність підтримується через `CookieLocaleResolver` та `LocaleChangeInterceptor` (`?lang=en|fr|uk`).
- **Генерація листів (FreeMarker Engine) & SMTP:** При додаванні нової книги `MailService` рендерить FTL-шаблон `new_book.ftl` і надсилає HTML-лист адміністратору через `JavaMailSender` (Gmail SMTP).

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

## Бізнес-правила

- **Реєстрація та активація:** При реєстрації новий користувач отримує роль USER за замовчуванням. Пароль автоматично хешується через BCrypt перед збереженням у БД.
- **Декларативні транзакції:** Усі сервісні методи в модулі `core` покриті анотацією `@Transactional(readOnly = true/false)` для забезпечення цілісності даних.
- **Аудит безпеки:** Події входу та виходу з системи фіксуються в логах за допомогою `SecurityAuditLogger`.
- **Автоматичне прив'язування авторів:** При додаванні коментаря через UI застосунок прив'язує його до поточного авторизованого користувача (`UserEntity`).
- **Поштові сповіщення:** При успішному додаванні нової книги адміністратор отримує стилізований HTML-лист. У разі відсутності SMTP-з'єднання помилка фіксується в логах, а книга все одно успішно зберігається в БД.

## Команди запуску

1. **Перевірка налаштувань `.env` у модулі `web/`:**

   Фрагмент кода

   ```env
   MAIL_USERNAME=your_gmail@gmail.com
   MAIL_PASSWORD=your_16_char_app_password
   MAIL_ADMIN=recipient_email@gmail.com
   ```

2. **Повна збірка проєкту та виконання тестів (з кореня):**

   Bash

   ```bash
   mvn clean install
   ```

3. **Запуск Spring Boot застосунку:**
   - **Через Maven CLI:**

     Bash

     ```bash
     mvn spring-boot:run -pl web
     ```

   - **Або через IDE:** Запустити метод `main()` у класі `web/src/main/java/sumdu/edu/ua/web/AppInit.java`.

Після запуску застосунок доступний за адресою: `http://localhost:8080/books` (або `http://localhost:8080/login`).

## Перевірка REST API (401 Unauthorized)

Для перевірки захисту API без авторизації виконайте команду в терміналі:

PowerShell

```powershell
curl.exe -i http://localhost:8080/api/books
```

Очікувана відповідь сервера: **`HTTP/1.1 401`** (`401 Unauthorized`).

## Архітектурні тести (ArchUnit)

У проєкті реалізовано автоматичну перевірку ізоляції шарів за допомогою ArchUnit:

- Перевіряється, що модуль `core` (`sumdu.edu.ua.core..`) не залежить від вищих шарів `web` та `persistence`.
- Перевіряється, що Spring MVC Контролери знаходяться виключно у пакеті `sumdu.edu.ua.web..`.

Запуск тестів з кореневої директорії:

Bash

```bash
mvn test
```