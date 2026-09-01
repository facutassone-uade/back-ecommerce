# e-commerce

API REST de un e-commerce hecha con Spring Boot. Proyecto de la cátedra Aplicaciones Interactivas (UADE).

## Stack

- Java 17
- Spring Boot (Web, Data JPA, DevTools)
- MySQL 8 (vía Docker)
- Lombok
- Maven (con wrapper, no hace falta tener Maven instalado)

## Estructura de carpetas

El código está organizado por entidad (package-by-feature) en vez de por capa técnica. Cada paquete agrupa todo lo relacionado a esa entidad, con sus DTOs en una subcarpeta `dto/`:

```
src/main/java/com/uade/e_commerce/
├── ECommerceApplication.java
├── common/                       (código transversal, no atado a una entidad)
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BusinessValidationException.java
│   └── dto/
│       └── ErrorResponseDTO.java
├── product/
│   ├── Product.java
│   ├── ProductRepository.java
│   ├── ProductService.java
│   ├── ProductController.java
│   └── dto/
│       ├── ProductRequestDTO.java
│       ├── ProductResponseDTO.java
│       └── ProductSummaryDTO.java   (usado por cart/ y order/ al anidar el product)
├── category/
│   ├── Category.java
│   ├── CategoryRepository.java
│   ├── CategoryService.java
│   ├── CategoryController.java
│   └── dto/
│       ├── CategoryRequestDTO.java
│       └── CategoryResponseDTO.java
├── customer/
│   ├── Customer.java
│   ├── Address.java          (embeddable)
│   ├── CustomerRepository.java
│   ├── CustomerService.java
│   ├── CustomerController.java
│   └── dto/
│       ├── CustomerRequestDTO.java
│       ├── CustomerResponseDTO.java
│       ├── CustomerSummaryDTO.java   (usado por order/ y cart/ al anidar el customer)
│       └── AddressDTO.java
├── order/
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   ├── OrderService.java
│   ├── OrderController.java
│   └── dto/
│       ├── OrderRequestDTO.java
│       ├── OrderResponseDTO.java
│       ├── OrderItemRequestDTO.java
│       └── OrderItemResponseDTO.java
└── cart/
    ├── Cart.java
    ├── CartItem.java
    ├── CartRepository.java
    ├── CartItemRepository.java
    ├── CartService.java
    ├── CartController.java
    └── dto/
        ├── CartRequestDTO.java
        ├── CartResponseDTO.java
        ├── CartItemRequestDTO.java
        ├── CartItemResponseDTO.java
        └── CartCheckoutRequestDTO.java
```

## Base de datos

Levantar MySQL en Docker antes de correr la app:

```bash
docker run --name mysql-open -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e MYSQL_DATABASE=ecommerce_db -p 3306:3306 -d mysql:8.0
```

Config en `application.properties`: host `localhost:3306`, base `ecommerce_db`, user `root`, sin password. Hibernate crea/actualiza las tablas solo (`ddl-auto=update`).

Si el contenedor ya existe y solo hace falta prenderlo de nuevo: `docker start mysql-open`.

## Cómo levantarlo

```bash
./mvnw spring-boot:run
```

También se puede correr desde VSCode con el **Spring Boot Dashboard** o el botón `Run` sobre `ECommerceApplication.java`.

La app queda en `http://localhost:8080`.

## Endpoints

Todos los recursos exponen el mismo patrón CRUD:

| Método | Ruta | Acción |
|---|---|---|
| GET | `/api/{recurso}` | listar todos |
| GET | `/api/{recurso}/{id}` | buscar por id |
| POST | `/api/{recurso}` | crear |
| PUT | `/api/{recurso}/{id}` | actualizar |
| DELETE | `/api/{recurso}/{id}` | eliminar |

Recursos disponibles: `products`, `categories`, `customers`, `orders`, `carts`.

`GET /api/products` devuelve el listado ordenado alfabéticamente por `name`.

Además hay endpoints para las sub-entidades:

| Método | Ruta | Acción |
|---|---|---|
| POST | `/api/products/{id}/categories/{categoryId}` | asociar una categoría al producto |
| DELETE | `/api/products/{id}/categories/{categoryId}` | quitar una categoría del producto |
| POST | `/api/carts/{id}/items` | agregar un ítem al carrito |
| DELETE | `/api/carts/{id}/items/{itemId}` | quitar un ítem del carrito |
| DELETE | `/api/carts/{id}/items` | vaciar el carrito |
| POST | `/api/carts/{id}/checkout` | convertir el carrito en una orden (descuenta stock) |
| POST | `/api/orders/{id}/items` | agregar un ítem a la orden |
| DELETE | `/api/orders/{id}/items/{itemId}` | quitar un ítem de la orden |

`orders` y `carts` referencian un cliente vía `"customerId": 1` en el body.

`POST /api/carts/{id}/items` valida que haya stock suficiente antes de agregar el ítem (sumando lo que ya haya en el carrito de ese mismo producto); si no alcanza, responde 400.

Cuando un producto va anidado dentro de un ítem de carrito o de orden se serializa como `ProductSummaryDTO` (solo `id`, `name`, `price`, `stock`). El `ProductResponseDTO` completo (con `description` y `categories`) se usa únicamente en los endpoints de `/api/products`.

## Formato de errores

Cuando algo falla, la API responde siempre con el mismo JSON (`ErrorResponseDTO`), armado de forma centralizada en `common/GlobalExceptionHandler`.

Para errores de negocio y recursos inexistentes se usan excepciones de dominio lanzadas desde los services:
- `ResourceNotFoundException` → 404
- `BusinessValidationException` → 400

Ejemplo real de recurso inexistente:

```json
{
  "timestamp": "2026-08-29T12:24:53.643716",
  "status": 404,
  "error": "Not Found",
  "message": "Producto con id 999999 no existe",
  "path": "/api/products/999999"
}
```

Ejemplo real de validación de negocio:

```json
{
  "timestamp": "2026-08-29T12:29:46.058319",
  "status": 400,
  "error": "Bad Request",
  "message": "No se puede crear el carrito: customerId es obligatorio",
  "path": "/api/carts"
}
```

| Situación | Código |
|---|---|
| Ruta inexistente | 404 |
| Recurso por id inexistente, sea de la URL o referenciado en el body (ej. `/api/products/999999`, o crear un carrito con un `customerId` que no existe) | 404 |
| Regla de negocio inválida (ej. campo obligatorio faltante, checkout con carrito vacío o sin stock) | 400 |
| Body JSON inválido o parámetro con tipo incorrecto (ej. `/api/products/abc`) | 400 |
| Error inesperado del servidor | 500 |

## Probar la API

Importar la colección de Postman en `postman/e-commerce.postman_collection.json` (`File → Import` en Postman). Usa la variable `{{baseUrl}}` (default `http://localhost:8080`).
