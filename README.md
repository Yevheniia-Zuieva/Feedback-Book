# Книга відгуків (Spring Boot MVC / Thymeleaf SSR & FreeMarker E-mail SMTP)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг та додавання/перегляду відгуків. Архітектура побудована за багатомодульним принципом Maven (`core`, `persistence`, `web`).

У цій версії застосунок розширено підтримкою **автоматичного формування та надсилання HTML-листів** на базі шаблонізатора **FreeMarker** та протоколу **Gmail SMTP**. Веб-інтерфейс працює на **Thymeleaf SSR** з підтримкою мультимовності (i18n), а збереження приватних конфігурацій (SMTP credentials) реалізовано через змінні середовища у файлі `.env`.

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.16
- **Фреймворк:** Spring Boot 3.3.4 (`spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-freemarker`, `spring-boot-starter-mail`)
- **Шаблонізатори:**
  - **Thymeleaf** (HTML5 SSR для вебсторінок)
  - **Apache FreeMarker** (генерація HTML-листів)
- **Безпека секретів:** `spring-dotenv` (зчитування конфігурацій із `.env`)
- **Поштовий протокол:** JavaMailSender (Gmail SMTP, порт 587 з TLS)
- **Вебсервер:** Вбудований Apache Tomcat (порт `8080`)
- **СУБД:** H2 (вбудована, файловий режим)
- **Тестування:** JUnit 5, ArchUnit
- **Архітектура:** Багатомодульний проєкт із розділенням на Domain/Ports/Adapters.

## Структура проєкту

Проєкт розділено на три незалежні модулі для чіткого розділення відповідальності (Separation of Concerns):

```text
MPF_Labs/
├── pom.xml          # Батьківський POM (управління версіями та модулями)
├── core/            # Доменна модель, порти та сервіси бізнес-логіки (@Service)
│   └── src/main/java/sumdu/edu/ua/core/
├── persistence/     # Реалізація портів доступу до БД H2 (@Repository)
│   └── src/main/java/sumdu/edu/ua/persistence/
└── web/             # MVC-контролери, Thymeleaf шаблони, FreeMarker поштовий сервіс
    ├── .env                          # Локальні змінні середовища (SMTP Credentials - у .gitignore)
    ├── src/main/java/sumdu/edu/ua/
    │   ├── config/
    │   │   ├── EmailFreemarkerConfig.java   # Конфігурація окремого бандлу FreeMarker для листів
    │   │   ├── LanguageConfig.java          # Конфігурація i18n (LocaleResolver, Interceptor)
    │   │   └── WebConfig.java               # Налаштування ресурсів та MVC
    │   ├── service/
    │   │   ├── EmailTemplateProcessor.java  # Злиття FTL-шаблонів із моделлю даних
    │   │   └── MailService.java             # Формування та надсилання MIME HTML-листів
    │   └── web/
    │       ├── AppInit.java                 # Точка входу Spring Boot
    │       └── controller/
    │           ├── BooksController.java     # Маршрутизація каталогу книг та виклик пошти
    │           └── CommentsController.java  # Маршрутизація коментарів
    └── src/main/resources/
        ├── application.properties    # Основні налаштування Spring Boot та ін'єкції з .env
        ├── mail-templates/           # Каталог FreeMarker шаблонів електронної пошти
        │   └── new_book.ftl          # HTML-шаблон сповіщення про нову книгу
        ├── messages.properties       # Ресурсний бандл (Українська / Default)
        ├── messages_en.properties    # Ресурсний бандл (Англійська)
        ├── messages_fr.properties    # Ресурсний бандл (Французька)
        └── templates/
            ├── books.html            # Головний каталог книг (Thymeleaf)
            ├── book-form.html        # Форма додавання книги (Thymeleaf)
            ├── book-comments.html    # Перегляд та додавання відгуків (Thymeleaf)
            └── fragments/
                └── header.html       # Спільний фрагмент шапки та навігації
```

## Архітектура, i18n та поштова система

- **Серверний рендеринг (Thymeleaf SSR):** MVC-контролери повертають назви HTML-представлень, а `ThymeleafViewResolver` формує сторінки на основі об'єкта `Model`.
- **Локалізація та мультимовність (i18n):** Використовується `CookieLocaleResolver` та `LocaleChangeInterceptor` для обробки параметру `?lang=en|fr|uk`.
- **Генерація листів (FreeMarker Engine):**
  - Для поштових шаблонів налаштовано окремий бін `freemarkerEmailConfig` у класі `EmailFreemarkerConfig`, який зчитує FTL-шаблони з директорії `classpath:/mail-templates/`.
  - `EmailTemplateProcessor` виконує рендеринг FTL-шаблону `new_book.ftl` у динамічний HTML-рядок.
- **SMTP Інтеграція та Пошта (`MailService`):**
  - При додаванні книги через `BooksController` автоматично викликається `mailService.sendNewBookEmail(savedBook)`.
  - `MailService` створює `MimeMessage` із підтримкою HTML (`MimeMessageHelper`) і здійснює надсилання листа через `JavaMailSender` (Gmail SMTP).
- **Безпека секретів (.env):**
  - Усі чутливі дані (паролі застосунків, поштові скриньки) винесені у файл `.env` та автоматично завантажуються бібліотекою `spring-dotenv`. Файл `.env` додано до `.gitignore`.

## Бізнес-правила

- **Поштові сповіщення:** При успішному додаванні нової книги адміністратор отримує стилізований HTML-лист із логотипом з GitHub, назвою, автором, роком видання та датою додавання.
- **Визначення раритетності:** У шаблоні `new_book.ftl` за допомогою директиви `<#if>` аналізується рік видання книги: якщо `year < 2000`, у листі відображається плашка **«Раритетне видання!»**.
- **Умовне відображення в UI:** Якщо список книг або коментарів порожній, у Thymeleaf-шаблонах використовується `th:if` / `th:unless` для відображення відповідного повідомлення.
- **Видалення відгуків:** Згідно з доменним правилом у `CommentService`, відгук до книги дозволено видаляти лише протягом 24 годин з моменту створення.

## Команди запуску

Збірка здійснюється з кореня проєкту, запуск — з модуля `web`.

1. **Налаштування секретів у файлі `web/.env`:** Перед першим запуском переконайтеся у наявності файлу `.env` з вашими даними:

   Фрагмент кода

   ```env
   MAIL_USERNAME=your_gmail@gmail.com
   MAIL_PASSWORD=your_16_char_app_password
   MAIL_ADMIN=recipient_email@gmail.com
   ```

2. **Повна збірка проєкту та виконання тестів (у корені):**

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

   - **Або напряму через IDE:** запустити метод `main()` у класі `web/src/main/java/sumdu/edu/ua/web/AppInit.java`.

Після запуску вебклієнт буде доступний у браузері за адресою: `http://localhost:8080/books`

## Архітектурні тести (ArchUnit)

У проєкті реалізовано автоматичну перевірку ізоляції шарів за допомогою бібліотеки ArchUnit. Тести перевіряють:

- Незалежність модуля `core` від вищих шарів (`web`, `persistence`).
- Відсутність прямих залежностей між `web` та `persistence` (окрім конфігураційних класів Spring IoC).

Запуск тестів з кореневої директорії:

Bash

```bash
mvn test
```

## Доступні вебсторінки та маршрути

### Маршрути каталогу книг (`BooksController`)

- **`GET /books`** — відображення каталогу книг (`books.html`). Підтримує перемикання мов (`?lang=en`, `?lang=fr`).
- **`GET /books/add`** — відображення форми додавання нової книги (`book-form.html`).
- **`POST /books`** — обробка відправки форми створення книги, автоматичне відправлення HTML-листа через FreeMarker/SMTP та перенаправлення (`redirect:/books`).

### Маршрути коментарів (`CommentsController`)

- **`GET /comments?bookId={id}`** — відображення списку коментарів до книги (`book-comments.html`).
- **`POST /comments`** — додавання нового коментаря до книги з перенаправленням (`redirect:/comments?bookId={id}`).
- **`POST /comments/delete`** — видалення коментаря за `commentId` та `bookId` з перенаправленням (`redirect:/comments?bookId={id}`).