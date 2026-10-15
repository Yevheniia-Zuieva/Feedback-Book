# Книга відгуків (Spring Boot Starter)

Проєкт реалізує багатомодульний вебдодаток для управління каталогом книг та додавання/перегляду відгуків. Архітектура побудована за багатомодульним принципом Maven (`core`, `persistence`, `web`).

У цій версії вебрівень мігровано на **Spring Boot Starter** із реалізацією принципів Інверсії управління (IoC) та Впровадження залежностей (DI). Класичні Jakarta Servlets зареєстровані як Spring-біни за допомогою `ServletRegistrationBean` у класі конфігурації. Для збереження даних застосовується вбудована база даних H2 (JDBC).

## Технологічний стек

* **JDK:** 21
* **Maven:** 3.9.16
* **Фреймворк:** Spring Boot 3.3.4 (`spring-boot-starter`, `spring-boot-starter-web`)
* **Вебсервер:** Вбудований Apache Tomcat
* **JSON:** Jackson (включено у Spring Boot Web)
* **СУБД:** H2 (вбудована, файловий режим)
* **Тестування:** JUnit 5, ArchUnit
* **Архітектура:** Багатомодульний проєкт із розділенням на Domain/Ports/Adapters.

## Структура проєкту

Проєкт розділено на три незалежні модулі для чіткого розділення відповідальності (Separation of Concerns):

```text
MPF_Labs/
├── pom.xml          # Батьківський POM (управління версіями та модулями)
├── core/            # Доменна модель, порти та сервіси бізнес-логіки (@Service)
│   └── src/main/java/sumdu/edu/ua/core/
├── persistence/     # Реалізація портів доступу до БД H2 (@Repository)
│   └── src/main/java/sumdu/edu/ua/persistence/
└── web/             # Spring Boot конфігурація (@SpringBootApplication), ServletRegistrationBean, сервлети та статичний UI
    ├── src/main/java/sumdu/edu/ua/web/
    └── src/main/resources/application.properties
```

## Конфігурація та IoC/DI

* **Головний клас:** `AppInit.java` з анотацією `@SpringBootApplication`.
* **Конфігурація бінів (`ServletConfig.java`):**

  * Анотований `@Configuration`.
  * Реєстрація сервлетів `BooksServlet` та `CommentsServlet` у контейнері Spring за допомогою методів з анотацією `@Bean` (`ServletRegistrationBean`).
  * Створено додатковий кастомний бін (`applicationName`).
* **Ін'єкція залежностей:** передача репозиторіїв та сервісів виконується через конструктори (`Constructor Injection`).
* **Файл параметрів (`application.properties`):**

  * `server.port=8080` — налаштування порту сервера.
  * Налаштування веб-контексту та автоконфігурацій Spring Boot.

## Бізнес-правила

* **Видалення відгуків:** згідно з доменним правилом у `CommentService`, відгук до книги дозволено видаляти лише протягом 24 годин з моменту його створення.

## Команди запуску

Збірка здійснюється з кореня проєкту, запуск — з модуля `web`.

1. **Повна збірка проєкту та виконання тестів (у корені):**

   ```bash
   mvn clean install
   ```

2. **Запуск Spring Boot застосунку:**

   * **Через Maven CLI:**

     ```bash
     mvn spring-boot:run -pl web
     ```

   * **Або напряму через IDE:** запустити метод `main()` у класі `web/src/main/java/sumdu/edu/ua/web/AppInit.java`.

Після запуску вебклієнт буде доступний у браузері за адресою: `http://localhost:8080/`

## Архітектурні тести (ArchUnit)

У проєкті реалізовано автоматичну перевірку ізоляції шарів за допомогою бібліотеки ArchUnit. Тести перевіряють:

* Незалежність модуля `core` від вищих шарів (`web`, `persistence`).
* Відсутність прямих залежностей між `web` та `persistence` (окрім конфігураційних класів Spring IoC).

Запуск тестів з кореневої директорії:

```bash
mvn test
```

## Список доступних URL та ендпоїнтів

### Клієнтський інтерфейс

* **`GET /`** або **`GET /index.html`** — статична HTML-сторінка з JavaScript-клієнтом для перегляду та додавання відгуків.

### API сервлети

* **`GET /books`** — повертає JSON-список усіх книг.
* **`GET /books/{id}`** — повертає деталі конкретної книги за її `id`.
* **`GET /comments`** — повертає JSON-список усіх відгуків через `CommentsServlet`.
* **`POST /comments`** — додавання нового відгуку.
