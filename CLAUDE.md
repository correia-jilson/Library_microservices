# CLAUDE.md — Library Management Microservices (Learning Project)

## 1. Who I am and how to work with me

I'm a graduate student learning **core Java, object-oriented design, Spring Boot and microservices**.
This project rebuilds concepts from my earlier Canvas-style CMS project
(Spring Boot + MongoDB + React, which used inheritance, an enum-based factory, a singleton,
static nested comparators, generics, CSV file handling and a Controller → Service → Repository design)
as a small **Library Management System**, first as a monolith and then as microservices.

**The goal is learning, not just working code.** Follow these rules:

- **Explain before you build.** Before each task, explain in 2–4 sentences which concept it teaches and why we're doing it this way.
- **Work in small steps.** One task from the checklist at a time. After each task, stop, summarize what changed, and tell me how to run or test it.
- **Let me write some code.** For each phase, mark 1–2 tasks as "🧑‍💻 YOU TRY": describe what to build and give hints, but don't write it unless I ask.
- **Don't skip ahead.** Never start the next phase until I say the current phase is done.
- **Keep it simple.** Don't add libraries, patterns or infrastructure not listed in this file unless I ask. If you think something is missing, suggest it and wait.
- **Comment the "why".** Add short comments where a concept appears for the first time (e.g. `// Factory pattern: ...`). Don't comment obvious lines.
- **Tests come with features.** Every phase adds tests for its main logic. Run them before saying a task is done.
- **Commits.** After each completed task, suggest a commit message in Conventional Commits style (`feat(book-service): add borrow endpoint`). Ask me before committing or pushing.
- **When something fails**, show me the error, explain what it means, then fix it. Don't silently retry.
- **Update the progress tracker** (section 9) as tasks are completed.

## 2. Tech stack

| Area | Choice |
|---|---|
| Language | Java 21 (LTS) |
| Build | Maven (each project has its own `pom.xml` and Maven wrapper) |
| Framework | Latest stable Spring Boot from start.spring.io, with the **matching** Spring Cloud release train. Check compatibility before adding Spring Cloud. |
| Database | H2 (Phases 2–3), PostgreSQL in Docker (Phase 4) |
| Persistence | Spring Data JPA |
| Service calls | `RestClient` first, then OpenFeign |
| Resilience | Resilience4j (circuit breaker, timeout) |
| Discovery / gateway | Eureka, Spring Cloud Gateway |
| Docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, `@WebMvcTest`, `@DataJpaTest` |
| Containers | Docker, Docker Compose |
| Utilities | Lombok is allowed **only from Phase 2 on**. Phase 1 is plain Java so I learn to write constructors, getters, `equals`/`hashCode` and `toString` myself. |

## 3. Repository structure

```
library-microservices/
├── CLAUDE.md
├── README.md
├── .gitignore
├── docs/
│   └── architecture.md          # diagrams + notes, updated each phase
├── phase1-core-java/            # Day 1: plain Java console app
├── phase2-monolith/             # Day 2: single Spring Boot REST app
├── book-service/                # Day 3: microservice (port 8081)
├── loan-service/                # Day 3: microservice (port 8082)
├── discovery-server/            # Day 4: Eureka (port 8761)
├── api-gateway/                 # Day 4: Spring Cloud Gateway (port 8080)
└── docker-compose.yml           # Day 4
```

Base package for everything: `com.library` (e.g. `com.library.book`, `com.library.loan`).

## 4. Domain model (shared by all phases)

- `LibraryItem` (abstract): `id`, `title`, `publicationYear`, `status`
  - `Book`: `author`, `isbn`
  - `Magazine`: `issueNumber`
  - `Dvd`: `durationMinutes`
- `Person` (abstract): `id`, `name`, `email`
  - `Member`: `memberType`, list of active loans
  - `Librarian`: `employeeId`
- `Loan`: `id`, `itemId`, `memberId`, `borrowDate`, `dueDate`, `returnDate`, `status`
- Enums:
  - `ItemStatus { AVAILABLE, BORROWED, LOST }`
  - `LoanStatus { ACTIVE, RETURNED, OVERDUE }`
  - `MemberType { STUDENT(3, 14), FACULTY(10, 30) }`, holding `maxBorrowLimit` and `loanDays`
- Business rules:
  - An item can only be borrowed if it is `AVAILABLE`.
  - A member can't exceed their `MemberType` borrow limit.
  - Late fee per day: Book $0.50, Magazine $0.25, DVD $1.00. Use polymorphism: each subclass overrides `calculateLateFee(int daysLate)`.

---

## 5. Day 1 — Fri, Nov 13: Phase 1, Core Java console app (`phase1-core-java/`)

**Concepts:** classes, encapsulation, inheritance, abstract classes, interfaces, polymorphism, enums with fields, Factory, Singleton, generics, collections, streams, lambdas, comparators, static nested classes, custom exceptions, file I/O, JUnit.

Packages: `model`, `enums`, `factory`, `repository`, `service`, `exception`, `util`, plus `Main`.

- [ ] 1.1 Maven project setup (plain Java, JUnit 5 only), with `Main` printing a welcome menu
- [ ] 1.2 Enums `ItemStatus`, `LoanStatus`, `MemberType` (with fields, constructor and getters)
- [ ] 1.3 Abstract `LibraryItem` + `Book`, `Magazine`, `Dvd`, with `calculateLateFee` overridden in each
- [ ] 1.4 `Borrowable` interface (`borrow()`, `returnItem()`) implemented by `LibraryItem`
- [ ] 1.5 Abstract `Person` + `Member`, `Librarian`; `Loan` class
- [ ] 1.6 Custom exceptions: `ItemNotFoundException`, `ItemNotAvailableException`, `BorrowLimitExceededException`
- [ ] 1.7 Generic `Repository<T, ID>` interface + `InMemoryRepository<T, ID>` (HashMap-based)
- [ ] 1.8 `ItemFactory` (creates items from a type string or enum) — 🧑‍💻 YOU TRY
- [ ] 1.9 `LibraryService` as a Singleton: borrow, return, search, overdue list
- [ ] 1.10 `ItemComparators` with static nested classes (by title, author, year) + stream examples (filter, sort, groupingBy status) — 🧑‍💻 YOU TRY
- [ ] 1.11 `CsvLoader`: load `data/books.csv` and `data/members.csv`; save loans to `data/loans.csv` on exit (try-with-resources)
- [ ] 1.12 Console menu in `Main`: list, search, borrow, return, overdue, exit
- [ ] 1.13 JUnit tests: borrow success, item not available, limit exceeded, late fee per type

**Done when:** `mvn test` passes, the menu borrow/return flow works, and data persists through CSV across restarts.

---

## 6. Day 2 — Sat, Nov 14: Phase 2, Spring Boot monolith (`phase2-monolith/`)

**Concepts:** Spring Boot, dependency injection, layered architecture, REST, DTOs, JPA entities and inheritance mapping, validation, global exception handling, file upload, OpenAPI, Spring tests.

Dependencies: Web, Data JPA, H2, Validation, Lombok, springdoc-openapi.

- [ ] 2.1 Generate the project; configure H2 + H2 console in `application.yml`; port 8080
- [ ] 2.2 Convert the domain to JPA entities (`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` for `LibraryItem`); keep enums as `@Enumerated(EnumType.STRING)`
- [ ] 2.3 Repositories extending `JpaRepository`, with derived queries (`findByAuthorContainingIgnoreCase`, `findByStatus`)
- [ ] 2.4 Request/response DTOs (Java records) + mapper class — 🧑‍💻 YOU TRY (write one mapper)
- [ ] 2.5 Services with constructor injection, reusing the Phase 1 rules; `@Transactional` on borrow/return
- [ ] 2.6 Controllers:
  - `GET/POST /api/books`, `GET /api/books/{id}`, `GET /api/books/search?author=`
  - `POST /api/members`, `GET /api/members/{id}/loans`
  - `POST /api/loans` (borrow), `PUT /api/loans/{id}/return`
- [ ] 2.7 Bean Validation on DTOs (`@NotBlank`, `@Email`, `@Positive`) with `@Valid`
- [ ] 2.8 `@RestControllerAdvice` mapping exceptions → 404 / 409 / 400 with a JSON `ErrorResponse` record
- [ ] 2.9 `POST /api/books/import` (multipart CSV), reusing the CSV logic
- [ ] 2.10 Swagger UI at `/swagger-ui.html`; seed data with a `CommandLineRunner`
- [ ] 2.11 Tests: a `@WebMvcTest` for `LoanController`, a `@DataJpaTest` for `BookRepository`, and a Mockito unit test for `LoanService`

**Done when:** the full borrow → return flow works from Swagger, bad input returns proper errors, and tests pass.

---

## 7. Day 3 — Sun, Nov 15: Phase 3, Split into microservices (`book-service/`, `loan-service/`)

**Concepts:** service boundaries, database per service, referencing by ID instead of object, synchronous service-to-service calls, `RestClient` vs OpenFeign, failure handling, circuit breaker, data consistency problems.

| Service | Port | Owns | Database |
|---|---|---|---|
| book-service | 8081 | items + availability | H2 `bookdb` |
| loan-service | 8082 | members + loans | H2 `loandb` |

- [ ] 3.1 Create `book-service` from the monolith's item code; add `PUT /api/books/{id}/status`
- [ ] 3.2 Create `loan-service` from the member + loan code; `Loan` stores `Long bookId` only (no `Book` entity)
- [ ] 3.3 Explain in `docs/architecture.md` why the monolith's object relationship becomes an ID reference
- [ ] 3.4 `BookClient` using `RestClient`; borrow flow:
  check availability → check member limit → save loan → mark book BORROWED
- [ ] 3.5 Return flow: update the loan → mark book AVAILABLE — 🧑‍💻 YOU TRY
- [ ] 3.6 Replace `RestClient` with an OpenFeign client; write a short note comparing the two
- [ ] 3.7 Failure experiment: stop book-service and borrow. Document what happens, then add a connect/read timeout and a clear 503 error
- [ ] 3.8 Resilience4j `@CircuitBreaker` with a fallback on `BookClient`
- [ ] 3.9 Consistency problem: if "mark BORROWED" fails after the loan is saved, roll back (delete the loan or mark it `FAILED`). Explain how a saga would solve this properly (no implementation)
- [ ] 3.10 Tests: mock `BookClient` in `LoanService` tests

**Done when:** both services run independently, borrowing works across them, and stopping book-service gives a clean error instead of a crash.

---

## 8. Day 4 — Mon, Nov 16: Phase 4, Discovery, gateway and Docker (`discovery-server/`, `api-gateway/`, `docker-compose.yml`)

**Concepts:** service discovery, client-side load balancing, API gateway, externalized config, containerization, multi-container orchestration.

- [ ] 4.1 `discovery-server` with `@EnableEurekaServer` on 8761
- [ ] 4.2 Register book-service and loan-service as Eureka clients; Feign calls `book-service` by name (no URLs)
- [ ] 4.3 `api-gateway` on 8080 with routes `/api/books/**` → book-service and `/api/members/**`, `/api/loans/**` → loan-service; CORS config here
- [ ] 4.4 Run two book-service instances (8081, 8083) and show load balancing in the logs
- [ ] 4.5 Switch both services to PostgreSQL via Spring profiles (`h2` default, `docker` profile for Postgres)
- [ ] 4.6 Dockerfile per service (multi-stage build) — 🧑‍💻 YOU TRY (write one, Claude reviews)
- [ ] 4.7 `docker-compose.yml`: discovery, gateway, book-service, loan-service, `book-db`, `loan-db`, with health checks and `depends_on`
- [ ] 4.8 Config via environment variables (DB URL, Eureka URL); no secrets committed
- [ ] 4.9 End-to-end test with a Postman collection or `docs/requests.http` through the gateway
- [ ] 4.10 Final README: architecture diagram (Mermaid), how to run each phase, endpoints, concepts learned
- [ ] Stretch (only if time allows): RabbitMQ `BookReturned` event from loan-service consumed by book-service

**Done when:** `docker compose up --build` starts everything, and a borrow/return through `localhost:8080` works end to end.

---

## 9. Progress tracker

| Day | Date | Phase | Status |
|---|---|---|---|
| 1 | Fri Nov 13 | Core Java | ⬜ Not started |
| 2 | Sat Nov 14 | Spring Boot monolith | ⬜ Not started |
| 3 | Sun Nov 15 | Microservices split | ⬜ Not started |
| 4 | Mon Nov 16 | Discovery, gateway, Docker | ⬜ Not started |

## 10. Commands cheat sheet

```bash
# Phase 1
cd phase1-core-java && ./mvnw test && ./mvnw exec:java

# Any Spring Boot service
cd <service> && ./mvnw spring-boot:run
./mvnw test

# Phase 4
docker compose up --build
docker compose down -v
```

Ports: gateway 8080 · book-service 8081 (and 8083) · loan-service 8082 · Eureka 8761

## 11. Code conventions

- Constructor injection only (no field `@Autowired`).
- DTOs as Java records; never return JPA entities from controllers.
- No passwords or secrets in code or committed files; use environment variables and `.env` (gitignored).
- Meaningful names; methods under ~30 lines; one public class per file.
- Log with SLF4J, not `System.out.println` (Phase 1 console output is the exception).
