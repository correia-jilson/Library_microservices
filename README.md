# Library Management — Java Microservices Learning Project

A step-by-step project for learning core Java, object-oriented design, Spring Boot and microservices,
built in 4 phases: a plain Java console app → a Spring Boot monolith → two microservices → discovery, gateway and Docker.

| Folder | Phase | Description |
|---|---|---|
| `phase1-core-java/` | 1 | Plain Java console app (OOP, generics, collections, file I/O) |
| `phase2-monolith/` | 2 | Single Spring Boot REST API (JPA, H2, validation) |
| `book-service/` | 3 | Microservice owning books (port 8081) |
| `loan-service/` | 3 | Microservice owning members & loans (port 8082) |
| `discovery-server/` | 4 | Eureka service registry (port 8761) |
| `api-gateway/` | 4 | Spring Cloud Gateway (port 8080) |

See [`CLAUDE.md`](CLAUDE.md) for the full plan and [`docs/architecture.md`](docs/architecture.md) for design notes.
