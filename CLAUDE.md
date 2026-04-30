# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean compile

# Run (H2 in-memory, default)
mvn spring-boot:run

# Run (SQL Server dev profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# All tests
mvn test

# Single test class
mvn test -Dtest=BuscarTransacaoUseCaseTest

# Single test method
mvn test -Dtest=BuscarTransacaoUseCaseTest#shouldFindTransactionById
```

## Architecture

This is a **Spring Boot 3.4.3 / Java 21** REST API for personal financial management, following **Hexagonal Architecture** (Ports & Adapters).

### Layer Structure

```
br.com.contadin/
├── domain/           # Pure POJOs — no framework annotations
│   ├── model/        # Aggregate roots (Categoria, Transacao, Usuario, etc.)
│   ├── enums/        # TipoCategoria, TipoTransacao, Recorrencia, TipoInstituicao
│   └── exception/    # Domain-specific exceptions
├── application/      # Orchestration — use cases and contracts
│   ├── port/in/      # Input port interfaces (entry points for controllers)
│   ├── port/out/     # Output port interfaces (dependencies on external systems)
│   ├── usecase/      # Implements port/in; calls port/out
│   ├── dto/          # Request/Response DTOs with validation annotations
│   ├── mapper/       # MapStruct: DTO ↔ Domain
│   └── validation/   # Cross-cutting validations
└── infrastructure/   # Technical adapters
    ├── web/
    │   ├── controller/   # @RestController — depends on port/in interfaces only
    │   ├── mapper/       # MapStruct: Request/Response ↔ Domain
    │   └── exception/    # Global @ControllerAdvice
    ├── persistence/
    │   ├── entity/       # JPA @Entity classes
    │   ├── mapper/       # MapStruct: Entity ↔ Domain
    │   └── repository/   # Implements port/out using Spring Data JPA
    ├── security/         # JWT filter, BCrypt encoder, token blacklist
    └── mail/             # Email adapter
```

### Request Flow

```
HTTP → Controller → WebMapper (DTO→Domain) → port/in interface
    → UseCase → Domain logic + port/out calls
    → Repository (JPA Entity ↔ Domain mapping)
    → Response DTO → HTTP
```

### Key Patterns

- **Three model types per domain**: Domain model (no annotations), JPA Entity (persistence), DTO (API contract) — never mix them.
- **All mappers are MapStruct** `@Mapper(componentModel = "spring")` interfaces; implementations are generated at compile time.
- **Controllers never import concrete use cases** — only port/in interfaces. Repositories implement port/out interfaces, not the reverse.
- **`@RequiredArgsConstructor` + constructor injection** is the standard DI approach (Lombok).
- **Token blacklist** is in-memory (`InMemoryTokenBlacklistAdapter`) — invalidated on logout.
- **Async email sending** — `ContadinApplication` has `@EnableAsync`; mail calls should not block request threads.

## Configuration

| Profile | Database | Activation |
|---------|----------|------------|
| default | H2 in-memory (`jdbc:h2:mem:banco`) | `mvn spring-boot:run` |
| dev | SQL Server | `-Dspring-boot.run.profiles=dev` |

Schema is managed by `spring.jpa.hibernate.ddl-auto=update` — no migration tool.

**H2 Console** (default profile): `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:banco`, user `sa`, empty password.

**Swagger UI**: `http://localhost:8080/api-contadin`

### Key Environment Variables (dev profile)

```
DB_URL          # SQL Server host:port (default: localhost:1433)
DB_USERNAME     # (default: contadin)
DB_PASSWORD     # Required for dev profile
JWT_SECRET      # Base64 signing key
MAIL_PASSWORD   # Gmail app password
```

All variables have defaults in `application*.properties` via `${VAR:default}`.

## Testing

- Unit tests use `@ExtendWith(MockitoExtension.class)` — no Spring context loaded.
- Mock port/out interfaces (repositories, email, token provider); never mock domain model classes.
- Test only use case logic; controller and persistence layers tested separately.

## Mock Data

Set `app.set.mockdata=true` in `application.properties` (already enabled by default) to populate seed data on startup via `infrastructure/initializer/`.
