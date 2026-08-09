# Foodie — Claude Code Rules

## Project Overview

- **Stack:** Java 17, Spring Boot 3.3.2, Vaadin 24.4.10 (Flow), Maven
- **Type:** Progressive Web App (PWA)
- **Root package:** `com.hasshe.foodie`
- **Databases:** H2 (local / test), PostgreSQL (production)

---

## Architecture

### Layer Order

```
Vaadin View  →  Controller  →  ServiceInterface  →  ServiceImpl  →  DbInterface  →  DbImpl / Repository
```

Each layer may only call the interface of the layer directly below it. No skipping layers.

### Package Structure

```
com.hasshe.foodie
├── views/           # Vaadin pages and reusable UI components
│   └── components/  # Reusable Vaadin component classes (CardComponent, etc.)
├── controller/      # Spring controllers / Vaadin presenter logic
├── service/
│   ├── api/         # Service interfaces
│   └── impl/        # Service implementations
├── db/
│   ├── api/         # DB/repository interfaces
│   ├── impl/        # DB implementations
│   └── entity/      # JPA entities — must NOT leave this package
├── domain/          # Domain model objects — used between service and controller only
├── dto/             # Display/view objects — used between controller and Vaadin views
└── constants/       # All application-wide constants
```

---

## Coding Conventions

### No Inheritance

Never use class inheritance (`extends`) for application code. Use interfaces and composition instead.

```java
// WRONG
public class RestaurantServiceImpl extends BaseService { ... }

// CORRECT
public class RestaurantServiceImpl implements RestaurantService { ... }
```

### Interfaces Are Mandatory

Every service and DB class must implement an interface. The caller always depends on the interface, never the concrete class.

```java
// Service layer
public interface RestaurantService {
    RestaurantDomain findById(String id);
}

public class RestaurantServiceImpl implements RestaurantService { ... }

// DB layer
public interface RestaurantDb {
    RestaurantEntity findById(String id);
}

public class RestaurantDbImpl implements RestaurantDb { ... }
```

### Entity Isolation

Three distinct object types exist — one per layer boundary. They must never cross their boundary.

| Object Type | Package | Allowed In |
|---|---|---|
| `*Entity` | `db.entity` | `db` layer only |
| `*Domain` | `domain` | `service` → `controller` |
| `*Display` | `dto` | `controller` → Vaadin view |

Mappers must be written to convert between layers. Use a dedicated `*Mapper` class per domain type.

```java
// WRONG — entity leaking to controller
public RestaurantEntity getRestaurant(String id) { ... }

// CORRECT
public RestaurantDomain getRestaurant(String id) { ... }
```

### Constants

Never repeat a string literal or magic number. All constants live in `com.hasshe.foodie.constants`.

```java
// WRONG
grid.addColumn("restaurantName");

// CORRECT
grid.addColumn(RestaurantConstants.COLUMN_NAME);
```

Group constants by domain in separate classes: `RestaurantConstants`, `RouteConstants`, `ThemeConstants`, etc.

### Class Size

Keep classes small and focused on a single responsibility. If a class exceeds ~150 lines, break it up. Prefer many small classes over one large one.

### No Comments

Do not write comments in code. Well-named classes, methods, and variables are the documentation. The only acceptable exception is a single-line comment explaining a non-obvious external constraint or bug workaround — if removing the comment would genuinely confuse a future reader.

```java
// WRONG
// Get the restaurant by ID
public RestaurantDomain findById(String id) { ... }

// CORRECT
public RestaurantDomain findById(String id) { ... }
```

Never write Javadoc on internal classes. No `// TODO`, `// FIXME`, or `// HACK` — resolve the issue or create a ticket.

### Null Safety

Never return `null`. Use `Optional<T>` for values that might not exist.

```java
// WRONG
public RestaurantDomain findById(String id) {
    return null;
}

// CORRECT
public Optional<RestaurantDomain> findById(String id) {
    return Optional.ofNullable(...);
}
```

Never pass `null` as a method argument. If a value is absent, model it explicitly with `Optional` or a dedicated empty-state object.

### Immutability

`*Domain` and `*Display` objects must be Java records. This enforces immutability and eliminates setters.

```java
// WRONG
public class RestaurantDisplay {
    private String name;
    public void setName(String name) { this.name = name; }
}

// CORRECT
public record RestaurantDisplay(String name, String cuisineType, double rating) {}
```

`*Entity` classes are the only objects that may be mutable (required by JPA).

### Dependency Injection

Use constructor injection only. All injected fields must be `final`. Never use `@Autowired` on fields or setters.

```java
// WRONG
@Autowired
private RestaurantService restaurantService;

// CORRECT
private final RestaurantService restaurantService;

public RestaurantController(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
}
```

### Exception Handling

Use a custom exception hierarchy rooted at `FoodieException`. Never throw raw `RuntimeException` or `Exception`.

```
FoodieException
├── NotFoundException
├── ValidationException
└── ServiceException
```

- One global `@ControllerAdvice` handles all exceptions — no per-class catch-and-rethrow
- Never swallow an exception with an empty catch block
- Wrap third-party exceptions at the DB layer boundary before they propagate up

### Validation

Validate input at the service boundary. Use Bean Validation annotations (`@NotNull`, `@Size`, `@NotBlank`) on `*Display` objects and method parameters in the service interface.

- Views may do lightweight UI validation (empty field checks) for UX only
- The service layer is the authoritative validation point — it must never trust its caller
- Entities are not the place for business validation

### Logging

Use SLF4J exclusively. Never use `System.out.println` or `java.util.logging`.

```java
private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);
```

- `DEBUG` — entry/exit of service methods, intermediate values useful for tracing
- `INFO` — significant business events (restaurant created, user registered)
- `WARN` — recoverable unexpected states
- `ERROR` — unrecoverable failures; always include the exception

Never log passwords, tokens, personal data, or payment information at any level.

### Visibility

Default to the most restrictive access modifier possible.

- `public` only when another package genuinely needs it
- `package-private` (no modifier) for classes used only within their own package
- `*Entity` constructors should be package-private or protected — JPA only, not for external instantiation
- No `public` utility/helper classes with only static methods; prefer injected single-method interfaces

### Assertions

Use assertions to enforce internal invariants and preconditions at method boundaries. Assertions document what must be true — they are not a substitute for user-input validation (which belongs at the service boundary).

Use Spring's `Assert` utility for preconditions inside service and DB implementations:

```java
public RestaurantDomain findById(String id) {
    Assert.hasText(id, "id must not be blank");
    Assert.notNull(id, "id must not be null");
    ...
}
```

Use `assert` statements for internal invariants that should never be violated by correct code:

```java
RestaurantDomain domain = mapper.mapToDomain(entity);
assert domain != null : "mapper must never return null";
```

Rules:
- Every public method that accepts object or `String` parameters must assert they are non-null / non-blank at the top of the method body
- Assertions on internal state go after any transformation (e.g. after a map call) to catch mapper bugs early
- Never use assertions as a replacement for throwing `ValidationException` on user-supplied input — assertions guard developer contracts, not user data

### Naming

- Methods are verbs: `findById`, `createRestaurant`, `mapToDisplay`
- Boolean methods and fields are questions: `isActive`, `hasReviews`, `isEmpty`
- No abbreviations: `restaurantService` not `restSvc`, `identifier` not `id` unless it's a domain term
- Constants are `UPPER_SNAKE_CASE`

---

## Database

### Local / Test

H2 in-memory database. Spring Boot auto-configures it. Use `application-local.properties` or `application-test.properties` to activate it.

```properties
# application-local.properties
spring.datasource.url=jdbc:h2:mem:foodiedb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Production

PostgreSQL. Credentials must come from environment variables — never hardcode them.

```properties
# application-prod.properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## Vaadin Conventions

### Reusable Components

Every distinct UI concept must be its own class in `views/components/`. A component class wraps the Vaadin primitives and exposes a clean API.

```java
// WRONG — inline card logic scattered in a view
VerticalLayout card = new VerticalLayout();
card.add(new H3(restaurant.getName()));
// ...

// CORRECT
RestaurantCardComponent card = new RestaurantCardComponent(restaurantDisplay);
```

A component class implements an interface, takes only display objects (never domain or entity), and has no business logic.

### Theming

All visual constants (colours, spacing, font sizes, border radii, button variants) live in `ThemeConstants`. CSS custom properties defined in `frontend/themes/foodie/` must reference these names.

- One place to change a colour — `ThemeConstants.COLOR_PRIMARY`
- One place to change button padding — `ThemeConstants.BUTTON_PADDING_M`
- Never hard-code hex values, pixel sizes, or style strings inline in Java

```java
// WRONG
button.getStyle().set("background-color", "#FF5733");

// CORRECT
button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
// or via CSS variable defined in ThemeConstants
```

### Page / View Classes

Vaadin `@Route` classes belong in `views/`. They:
- Accept only `*Display` objects from the controller
- Build the layout by composing `*Component` classes from `views/components/`
- Contain no business logic

---

## Testing Conventions

### Rule: Every Function Has a Unit Test

No exceptions. Every public method must have a corresponding test class in `src/test/`.

### Test Structure

Each test method covers one behaviour. Use descriptive names that read as sentences.

```
given_[precondition]_when_[action]_then_[outcome]
```

### Required Coverage Per Method

Each method must be covered by:

| Category | Count | Description |
|---|---|---|
| Happy path | 2+ | Normal, expected inputs that return the correct result |
| Unhappy path | 2+ | Invalid inputs, missing data, constraint violations |
| Edge case | 2+ | Boundary values, empty collections, null inputs, max lengths |

### Example

```java
class RestaurantServiceImplTest {

    // Happy path
    @Test
    void given_validId_when_findById_then_returnsDomain() { ... }

    @Test
    void given_multipleRestaurants_when_findAll_then_returnsAllDomains() { ... }

    // Unhappy path
    @Test
    void given_unknownId_when_findById_then_throwsNotFoundException() { ... }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() { ... }

    // Edge cases
    @Test
    void given_emptyRepository_when_findAll_then_returnsEmptyList() { ... }

    @Test
    void given_idWithMaxLength_when_findById_then_handlesCorrectly() { ... }
}
```

### Mocking

- Mock only the direct dependency (the interface one layer below)
- Never mock the class under test
- Use `@ExtendWith(MockitoExtension.class)` and `@Mock` / `@InjectMocks`
- Use H2 for repository-level integration tests, not for service/controller unit tests

---

## File Naming Conventions

| Type | Suffix | Example |
|---|---|---|
| Vaadin view | `View` | `RestaurantListView` |
| Vaadin component | `Component` | `RestaurantCardComponent` |
| Service interface | `Service` | `RestaurantService` |
| Service impl | `ServiceImpl` | `RestaurantServiceImpl` |
| DB interface | `Db` | `RestaurantDb` |
| DB impl | `DbImpl` | `RestaurantDbImpl` |
| JPA entity | `Entity` | `RestaurantEntity` |
| Domain object | `Domain` | `RestaurantDomain` |
| Display / DTO | `Display` | `RestaurantDisplay` |
| Mapper | `Mapper` | `RestaurantMapper` |
| Constants | `Constants` | `RestaurantConstants` |
| Test | `Test` | `RestaurantServiceImplTest` |
