# Книга відгуків (Spring Boot MVC / REST API)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг та додавання/перегляду відгуків. Архітектура побудована за багатомодульним принципом Maven (`core`, `persistence`, `web`).

У цій версії вебрівень мігровано з застарілих сервлетів (`HttpServlet`) на архітектуру **Spring MVC** з обробкою запитів через єдиний **`DispatcherServlet`**. Замість ручної обробки HTTP-запитів реалізовано REST-контролери (`@RestController`), що автоматично серіалізують об'єкти у формат **JSON** за допомогою бібліотеки Jackson.

## Технологічний стек

- **JDK:** 21
- **Maven:** 3.9.16
- **Фреймворк:** Spring Boot 3.3.4 (`spring-boot-starter-web`, Spring MVC)
- **Вебсервер:** Вбудований Apache Tomcat (порт `8080`)
- **JSON:** Jackson (`jackson-databind`)
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
└── web/             # Spring MVC контролери (@RestController), WebConfig (@EnableWebMvc) та статичний UI
    ├── src/main/java/sumdu/edu/ua/web/
    └── src/main/resources/
        ├── public/index.html
        └── application.properties
```

## Архітектура Spring MVC та маршрутизація

- **`DispatcherServlet` (Front Controller):** єдиний сервлет-вхід, що перехоплює всі вхідні HTTP-запити та передає їх на відповідні контролери.
- **REST-контролери (`@RestController`):**
  - `BooksApiController` — обробляє маршрути за префіксом `/api/books`.
  - `CommentsApiController` — обробляє маршрути за префіксом `/api/comments`.
- **Конфігурація Spring MVC (`WebConfig.java`):**
  - Анотований `@Configuration` та `@EnableWebMvc`.
  - Реалізує `WebMvcConfigurer` для налаштування обробки статичних ресурсів (`ResourceHandlerRegistry`).
- **Автоматична JSON-серіалізація:** об'єкти повертаються через `ResponseEntity<T>`, а Jackson автоматично конвертує їх у JSON та виставляє заголовки `Content-Type: application/json`.

## Бізнес-правила

- **Валідація полів:** при додаванні книги чи коментаря виконується перевірка обов'язкових полів (назва, автор, текст). При недотриманні умов повертається статус `400 Bad Request`.
- **Видалення відгуків:** згідно з доменним правилом у `CommentService`, відгук дозволено видаляти лише протягом 24 годин з моменту створення.

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

Після запуску вебклієнт буде доступний у браузері за адресою: `http://localhost:8080/`

## Архітектурні тести (ArchUnit)

У проєкті реалізовано автоматичну перевірку ізоляції шарів за допомогою бібліотеки ArchUnit. Тести перевіряють:

- Незалежність модуля `core` від вищих шарів (`web`, `persistence`).
- Відсутність прямих залежностей між `web` та `persistence` (окрім конфігураційних класів Spring IoC).

Запуск тестів з кореневої директорії:

```bash
mvn test
```

## Список REST API ендпоїнтів

### Статичний вебклієнт

- **`GET /`** або **`GET /index.html`** — статична HTML-сторінка, яка взаємодіє з REST API через JavaScript `fetch()`.

### API книг (`BooksApiController`)

- **`GET /api/books`** — повертає JSON-список усіх книг (з підтримкою пошуку `q` та пагінації `page`, `size`).
- **`GET /api/books/{id}`** — повертає деталі конкретної книги за її `id` (або `404 Not Found`).
- **`POST /api/books`** — створює нову книгу (статус `201 Created`).
  - *Тіло запиту (JSON):* `{"title": "The Hunger Games", "author": "Suzanne Collins", "pubYear": 2008}`

### API коментарів (`CommentsApiController`)

- **`GET /api/comments?bookId={id}`** — повертає JSON-список коментарів до вказаної книги.
- **`POST /api/comments`** — додає новий коментар (статус `201 Created`).
- **`DELETE /api/comments/{id}?bookId={bookId}`** — видаляє коментар за його `id` (статус `204 No Content`).