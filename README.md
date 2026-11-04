# Книга відгуків (Spring Boot MVC / JPA Hibernate ORM / Flyway / Thymeleaf SSR & FreeMarker E-mail)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг, користувачами та додавання/перегляду відгуків. Архітектура побудована за гексагональним принципом (Ports & Adapters) у Maven-модулях (`core`, `persistence`, `web`).

У цій версії здійснено **повний перехід із JDBC на повноцінний ORM-шар (Spring Data JPA / Hibernate 6)**, інтегровано **Flyway** для автоматичного версіонування схеми бази даних, розширено доменну модель сутністю `UserEntity`, реалізовано UI-функціонал перегляду коментарів конкретного користувача, а також збережено підтримку **автоматичного формування та надсилання HTML-листів** (FreeMarker + Gmail SMTP) та мультимовності (i18n).

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.16
- **Фреймворк:** Spring Boot 3.3.4
  - `spring-boot-starter-web` (Spring MVC)
  - `spring-boot-starter-data-jpa` (Spring Data JPA & Hibernate 6.5.3)
  - `spring-boot-starter-thymeleaf` (HTML5 SSR вебсторінки)
  - `spring-boot-starter-freemarker` (генерація HTML-листів)
  - `spring-boot-starter-mail` (JavaMailSender)
- **Міграція БД:** Flyway (`flyway-core` 10.x)
- **СУБД:** H2 Database (файловий режим `./data/booksdb`) + вебконсоль `H2 Console`
- **Безпека секретів:** `spring-dotenv` (зчитування конфігурацій із `.env`)
- **Поштовий протокол:** JavaMailSender (Gmail SMTP, порт 587 з TLS)
- **Вебсервер:** Вбудований Apache Tomcat (порт `8080`)
- **Тестування:** JUnit 5, ArchUnit
- **Архітектура:** Трирівнева портивно-адаптерна архітектура (`core` / `persistence` / `web`).

## Структура проєкту

Проєкт розділено на три незалежні модулі для забезпечення Separation of Concerns:

```text
MPF_Labs/
├── pom.xml                  # Батьківський POM (управління версіями та BOM)
├── core/                    # Доменна модель (JPA-сутності), порти та сервіси бізнес-логіки
│   └── src/main/java/sumdu/edu/ua/core/
│       ├── domain/          # BookEntity, UserEntity, CommentEntity, Page, PageRequest
│       ├── port/            # CatalogRepositoryPort, CommentRepositoryPort, UserRepositoryPort
│       └── service/         # UserService, CommentService (@Transactional)
├── persistence/             # Spring Data JPA репозиторії, JPA-адаптери та Flyway-міграції
│   └── src/main/java/sumdu/edu/ua/persistence/
│       ├── adapter/         # JpaCatalogRepositoryAdapter, JpaCommentRepositoryAdapter, JpaUserRepositoryAdapter
│       ├── repository/      # BookRepository, UserRepository, CommentRepository (JpaRepository)
│       └── resources/
│           └── db/migration/# V1__init.sql (Flyway-скрипт створення таблиць та даних)
└── web/                     # MVC-контролери, Thymeleaf-шаблони, FreeMarker поштовий сервіс
    ├── .env                 # Локальні змінні середовища (SMTP Credentials - у .gitignore)
    ├── src/main/java/sumdu/edu/ua/
    │   ├── config/          # EmailFreemarkerConfig, LanguageConfig, WebConfig
    │   ├── service/         # EmailTemplateProcessor, MailService
    │   └── web/
    │       ├── AppInit.java # Точка входу Spring Boot (@EnableJpaRepositories, @EntityScan)
    │       └── controller/
    │           ├── BooksController.java    # Каталог книг та надсилання пошти
    │           ├── CommentsController.java # Управління коментарями книг
    │           └── UserController.java     # Сторінка коментарів конкретного користувача
    └── src/main/resources/
        ├── application.properties # Конфігурації JPA, Flyway, H2 Console, Mail
        ├── mail-templates/
        │   └── new_book.ftl       # HTML-шаблон листа про нову книгу
        ├── messages*.properties   # Ресурсні бандли локалізації (uk, en, fr)
        └── templates/
            ├── books.html         # Головний каталог книг
            ├── book-form.html     # Форма додавання книги
            ├── book-comments.html # Перегляд та додавання відгуків книги
            ├── user-comments.html # Перегляд коментарів конкретного користувача
            └── fragments/
                └── header.html    # Спільний фрагмент шапки
```

## Архітектура, JPA/Hibernate, Flyway та i18n

- **ORM & JPA (Hibernate 6):** Відбулася повна відмова від ручного написання JDBC-коду (`JdbcBookRepository`, `schema.sql`). Доменні моделі перетворено на JPA-сутності з анотаціями `@Entity`, `@Table`, `@Id`, `@GeneratedValue`.
- **Міжтабличні зв'язки:**
  - `BookEntity` (1) ⟷ (N) `CommentEntity`: `@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`.
  - `UserEntity` (1) ⟷ (N) `CommentEntity`: `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`.
  - `CommentEntity` (N) ⟷ (1) `BookEntity` / `UserEntity`: `@ManyToOne(fetch = FetchType.LAZY)`.
- **Автоматична міграція (Flyway):** Версіонування схеми БД здійснюється за допомогою Flyway-скрипту `db/migration/V1__init.sql`, який автоматично створює таблиці `books`, `users`, `comments`, зовнішні ключі (`ON DELETE CASCADE` / `SET NULL`) та наповнює базу початковими тестовими даними.
- **Spring Data JPA Repositories:** Доступ до даних забезпечують інтерфейси `BookRepository`, `UserRepository` та `CommentRepository` (з кастомними методами `findByUserId`, `findByBookId`, `findByUsername`), реалізації яких автоматично генеруються Spring Data.
- **Серверний рендеринг (Thymeleaf SSR) & i18n:** MVC-контролери повертають назви HTML-шаблонів. Мультимовність підтримується через `CookieLocaleResolver` та `LocaleChangeInterceptor` (`?lang=en|fr|uk`).
- **Генерація листів (FreeMarker Engine) & SMTP:** При додаванні нової книги `MailService` рендерить FTL-шаблон `new_book.ftl` і надсилає HTML-лист адміністратору через `JavaMailSender` (Gmail SMTP).
- **H2 Console:** Доступна за адресою `/h2-console` для візуального аналізу таблиць та виконання SQL-запитів.

## Бізнес-правила

- **Декларативні транзакції:** Усі сервісні методи в модулі `core` покриті анотацією `@Transactional(readOnly = true/false)` для забезпечення цілісності даних.
- **Автоматичне прив'язування авторів:** При додаванні коментаря через UI застосунок автоматично знаходить існуючого користувача за введеним `username` або створює нового `UserEntity`, уникаючи порожніх полів `USER_ID` у БД.
- **Поштові сповіщення:** При успішному додаванні нової книги адміністратор отримує стилізований HTML-лист. У разі відсутності SMTP-з'єднання помилка фіксується в логах, а книга все одно успішно зберігається в БД.
- **Визначення раритетності:** У шаблоні `new_book.ftl` аналізується рік видання книги: якщо `year < 2000`, відображається плашка **«Раритетне видання!»**.

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

Після запуску застосунок доступний за адресою: `http://localhost:8080/books`

## Архітектурні тести (ArchUnit)

У проєкті реалізовано автоматичну перевірку ізоляції шарів за допомогою ArchUnit:

- Перевіряється, що модуль `core` (`sumdu.edu.ua.core..`) не залежить від вищих шарів `web` та `persistence`.
- Перевіряється, що Spring MVC Контролери знаходяться виключно у пакеті `sumdu.edu.ua.web..`.

Запуск тестів з кореневої директорії:

Bash

```bash
mvn test
```

## Доступні вебсторінки та маршрути

### Маршрути каталогу книг (`BooksController`)

- **`GET /books`** — відображення каталогу книг (`books.html`). Підтримує перемикання мов (`?lang=en`, `?lang=fr`, `?lang=uk`).
- **`GET /books/add`** — відображення форми додавання нової книги (`book-form.html`).
- **`POST /books`** (або `/books/add`) — обробка форми створення книги, збереження в JPA-репозиторії, відправка HTML-листа та перенаправлення (`redirect:/books`).

### Маршрути коментарів книг (`CommentsController`)

- **`GET /comments?bookId={id}`** — перегляд коментарів до книги (`book-comments.html`).
- **`POST /comments`** — додавання нового коментаря з автоматичною прив'язкою користувача.
- **`POST /comments/delete`** — видалення коментаря за `commentId` та `bookId`.

### Маршрути користувачів (`UserController`)

- **`GET /users/{id}/comments`** — окрема сторінка зі списком усіх коментарів конкретного користувача (`user-comments.html`).

### Адміністрування БД

- **`GET /h2-console`** — вебконсоль бази даних H2 (JDBC URL: `jdbc:h2:file:./data/booksdb`, User: `sa`, Password: *порожньо*).