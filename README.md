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
├── product/
│   ├── Product.java
│   ├── ProductRepository.java
│   ├── ProductService.java
│   ├── ProductController.java
│   └── dto/
│       ├── ProductRequestDTO.java
│       └── ProductResponseDTO.java
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
    ├── CartRepository.java
    ├── CartService.java
    ├── CartController.java
    └── dto/
        ├── CartRequestDTO.java
        └── CartResponseDTO.java
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
| DELETE | `/api/{recurso}/{id}` | eliminar |

Recursos disponibles: `products`, `customers`, `orders`, `carts`.

`orders` y `carts` referencian un cliente vía `"customer": { "id": 1 }`.

## Probar la API

Importar la colección de Postman en `postman/e-commerce.postman_collection.json` (`File → Import` en Postman). Usa la variable `{{baseUrl}}` (default `http://localhost:8080`).
