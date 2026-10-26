# Книга відгуків (Spring Boot MVC / Thymeleaf SSR & i18n)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг та додавання/перегляду відгуків. Архітектура побудована за багатомодульним принципом Maven (`core`, `persistence`, `web`).

У цій версії вебрівень мігровано на **серверний рендеринг (SSR)** за допомогою шаблонізатора **Thymeleaf**. Замість REST-контролерів з JSON-відповідями реалізовано MVC-контролери (`@Controller`), які генерують динамічні HTML-сторінки на основі об'єктів моделі (`Model`), забезпечують двостороннє зв'язування форм (Form Binding) та підтримують мультимовність (i18n).

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.16
- **Фреймворк:** Spring Boot 3.3.4 (`spring-boot-starter-web`, `spring-boot-starter-thymeleaf`)
- **Шаблонізатор:** Thymeleaf (HTML5 / SSR)
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
└── web/             # MVC-контролери, Thymeleaf шаблони, конфігурація i18n
    ├── src/main/java/sumdu/edu/ua/
    │   ├── config/
    │   │   ├── LanguageConfig.java   # Конфігурація i18n (LocaleResolver, Interceptor)
    │   │   └── WebConfig.java        # Налаштування ресурсів та MVC
    │   └── web/
    │       ├── AppInit.java          # Точка входу Spring Boot
    │       └── controller/
    │           ├── BooksController.java     # Маршрутизація каталогу книг
    │           └── CommentsController.java  # Маршрутизація коментарів
    └── src/main/resources/
        ├── application.properties    # Налаштування UTF-8 та Thymeleaf
        ├── messages.properties       # Ресурсний бандл (Українська / Default)
        ├── messages_en.properties    # Ресурсний бандл (Англійська)
        ├── messages_fr.properties    # Ресурсний бандл (Французька)
        └── templates/
            ├── books.html            # Головний каталог книг
            ├── book-form.html        # Форма додавання книги
            ├── book-comments.html    # Перегляд та додавання відгуків
            └── fragments/
                └── header.html       # Спільний фрагмент шапки та навігації
```

## Архітектура Spring MVC, Thymeleaf та i18n

- **Серверний рендеринг (SSR):** контролери повертають назви HTML-представлень (наприклад, `return "books"`), а `ThymeleafViewResolver` рендерить підсумковий HTML з даними з `Model`.
- **Зв'язування форм (Form Binding):** у шаблоні `book-form.html` атрибути `th:object="${bookForm}"` та `th:field="*{fieldName}"` забезпечують автоматичний двосторонній мапінг полів HTML-форми на Java-об'єкт `Book`.
- **Локалізація та мультимовність (i18n):**
  - Використовується `CookieLocaleResolver` для збереження обраної мови у кукі `lang` (мова за замовчуванням — українська `uk`).
  - `LocaleChangeInterceptor` перехоплює параметр `?lang=` у URL (наприклад, `/books?lang=en`).
  - Усі текстові елементи виводяться через ключі ресурсних бандлів `th:text="#{key}"`.
- **Шаблонізація (Layout Fragments):** спільний заголовок та меню навігації винесено у фрагмент `fragments/header.html` та підключаються на сторінках через `th:replace="~{fragments/header :: header}"`.

## Бізнес-правила

- **Умовне відображення:** якщо список книг або коментарів порожній, у шаблонах використовується `th:if` / `th:unless` для відображення відповідного повідомлення ("Немає книг.", "Коментарів поки немає.").
- **Видалення відгуків:** згідно з доменним правилом у `CommentService`, відгук до книги дозволено видаляти лише протягом 24 годин з моменту створення.

## Команди запуску

Збірка здійснюється з кореня проєкту, запуск — з модуля `web`.

1. **Повна збірка проєкту та виконання тестів (у корені):**

   ```bash
   mvn clean install
   ```

2. **Запуск Spring Boot застосунку:**
   - **Через Maven CLI:**

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

```bash
mvn test
```

## Доступні вебсторінки та маршрути

### Маршрути каталогу книг (`BooksController`)

- **`GET /books`** — відображення каталогу книг (`books.html`). Підтримує перемикання мов через параметр `?lang=en` або `?lang=fr`.
- **`GET /books/add`** — відображення форми додавання нової книги (`book-form.html`).
- **`POST /books`** — обробка відправки форми створення книги з перенаправленням (`redirect:/books`).

### Маршрути коментарів (`CommentsController`)

- **`GET /comments?bookId={id}`** — відображення списку коментарів до книги (`book-comments.html`).
- **`POST /comments`** — додавання нового коментаря до книги з перенаправленням (`redirect:/comments?bookId={id}`).
- **`POST /comments/delete`** — видалення коментаря за `commentId` та `bookId` з перенаправленням (`redirect:/comments?bookId={id}`).