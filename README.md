# e-commerce

API REST de un e-commerce hecha con Spring Boot. Proyecto de la cátedra Aplicaciones Interactivas (UADE).

## Características principales

- **Autenticación**: HTTP Basic Authentication para proteger endpoints
- **Autorización basada en roles**: ADMIN (operaciones administrativas), USER (cliente regular), PUBLIC (registro y login)
- **Gestión de productos y categorías**: CRUD completo con asociaciones
- **Carrito de compras**: Agregar/eliminar items, validación de stock, checkout
- **Órdenes**: Creación automática al hacer checkout, gestión de items
- Clientes: Perfil personalizable con dirección

## Autenticación y Autorización

La API utiliza **HTTP Basic Authentication** para proteger endpoints. Cada request debe incluir credenciales en el header `Authorization`.

### Roles disponibles

| Rol | Descripción | Acceso |
|-----|------------|--------|
| **ADMIN** | Administrador del sistema | Gestión de productos, categorías, clientes; visualizar todas las órdenes |
| **USER** | Cliente regular | Comprar, gestionar carrito, ver propias órdenes, actualizar perfil |
| **PUBLIC** | Sin autenticar | Registro y login |

### Endpoints de autenticación

| Método | Ruta | Rol | Descripción |
|--------|------|-----|------------|
| POST | `/api/auth/register` | PUBLIC | Registrar nueva cuenta |
| POST | `/api/auth/login` | PUBLIC | Iniciar sesión |
| GET | `/api/auth/me` | USER / ADMIN | Obtener perfil actual |
| POST | `/api/auth/logout` | USER / ADMIN | Cerrar sesión |

### Credenciales de prueba

```
Admin:
- Email: admin@example.invalid
- Password: change-me-admin

Usuario:
- Email: user@example.invalid
- Password: change-me-user

Segundo Usuario:
- Email: user2@example.invalid
- Password: change-me-user-2
```

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

### Productos

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/products` | USER / ADMIN | Listar todos los productos |
| GET | `/api/products/{id}` | USER / ADMIN | Buscar producto por id |
| POST | `/api/products` | ADMIN | Crear nuevo producto |
| PUT | `/api/products/{id}` | ADMIN | Actualizar producto |
| DELETE | `/api/products/{id}` | ADMIN | Eliminar producto |

### Categorías

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/categories` | USER / ADMIN | Listar todas las categorías |
| GET | `/api/categories/{id}` | USER / ADMIN | Buscar categoría por id |
| POST | `/api/categories` | ADMIN | Crear nueva categoría |
| PUT | `/api/categories/{id}` | ADMIN | Actualizar categoría |
| DELETE | `/api/categories/{id}` | ADMIN | Eliminar categoría |

### Clientes

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/customers` | ADMIN | Listar todos los clientes |
| GET | `/api/customers/{id}` | USER (propio) / ADMIN | Obtener datos del cliente |
| PUT | `/api/customers/{id}` | USER (propio) / ADMIN | Actualizar datos del cliente |
| DELETE | `/api/customers/{id}` | ADMIN | Eliminar cliente |

### Órdenes

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/orders` | ADMIN | Listar todas las órdenes |
| GET | `/api/orders/{id}` | USER (propia) / ADMIN | Obtener orden por id |
| GET | `/api/orders/user/{userId}` | USER (propia) / ADMIN | Listar órdenes del usuario |
| POST | `/api/orders` | USER | Crear nueva orden |
| PUT | `/api/orders/{id}` | USER (propia) / ADMIN | Actualizar orden |
| DELETE | `/api/orders/{id}` | USER (propia) / ADMIN | Eliminar orden |

### Carrito de compras

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/carts` | USER | Listar carritos del usuario |
| GET | `/api/carts/{id}` | USER (propio) / ADMIN | Obtener carrito por id |
| POST | `/api/carts` | USER | Crear nuevo carrito |
| PUT | `/api/carts/{id}` | USER (propio) / ADMIN | Actualizar carrito |
| DELETE | `/api/carts/{id}` | USER (propio) / ADMIN | Eliminar carrito |

### Sub-entidades

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/products/{id}/categories/{categoryId}` | ADMIN | Asociar categoría al producto |
| DELETE | `/api/products/{id}/categories/{categoryId}` | ADMIN | Quitar categoría del producto |
| POST | `/api/carts/{id}/items` | USER (propio) / ADMIN | Agregar ítem al carrito |
| DELETE | `/api/carts/{id}/items/{itemId}` | USER (propio) / ADMIN | Quitar ítem del carrito |
| DELETE | `/api/carts/{id}/items` | USER (propio) / ADMIN | Vaciar carrito |
| POST | `/api/carts/{id}/checkout` | USER (propio) / ADMIN | Convertir carrito en orden (descuenta stock) |
| POST | `/api/orders/{id}/items` | USER (propia) / ADMIN | Agregar ítem a la orden |
| DELETE | `/api/orders/{id}/items/{itemId}` | USER (propia) / ADMIN | Quitar ítem de la orden |

### Notas sobre endpoints

- `GET /api/products` devuelve el listado ordenado alfabéticamente por `name`
- `POST /api/carts/{id}/items` valida que haya stock suficiente antes de agregar el ítem; si no alcanza, responde 400
- `POST /api/carts/{id}/checkout` valida stock para cada item, descuenta del inventario, crea la orden y limpia el carrito. Responde 400 si el carrito está vacío o no hay stock suficiente
- Cuando un producto va anidado dentro de un ítem de carrito u orden se serializa como `ProductSummaryDTO` (id, name, price, stock)
- El `ProductResponseDTO` completo (con description y categories) se usa solo en endpoints de `/api/products`
- `orders` y `carts` referencian un cliente vía `"customerId": número` en el body

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

Importar la colección de Postman en `postman/e-commerce.postman_collection.json` (`File → Import` en Postman).

### Cómo usar la colección

1. **Variables de entorno**: La colección usa `{{baseUrl}}` (default `http://localhost:8080`) y credenciales de prueba (`{{adminEmail}}`, `{{adminPassword}}`, `{{userEmail}}`, `{{userPassword}}`)
2. **Autenticación HTTP Basic**: Los endpoints protegidos ya tienen configuradas credenciales. Cada request incluye el header de autenticación correspondiente
3. **Flujo recomendado**:
   - Primero: `POST /api/auth/login` (como USER o ADMIN)
   - Ver perfil: `GET /api/auth/me`
   - Productos: `GET /api/products` (listar), `GET /api/products/{id}` (detalle)
   - Carrito: `POST /api/carts` (crear), `POST /api/carts/{id}/items` (agregar items), `POST /api/carts/{id}/checkout` (comprar)
   - Órdenes: `GET /api/orders/user/{{userId}}` (mis órdenes)

### Seguridad en pruebas

⚠️ **Nota**: Las credenciales de prueba son solo para desarrollo local. En producción:
- Cambiar todas las contraseñas
- No incluir credenciales en el versionado
- Usar variables de entorno para sensibles

## Datos de prueba (DataInitializer)

Al levantar la app, se cargan automáticamente datos iniciales en la BD (solo si está vacía):

### Categorías (4)
- Electronics
- Clothing
- Books
- Home & Garden

### Productos (8)
- Laptop, Wireless Mouse, USB-C Cable (Electrónica)
- T-Shirt, Jeans (Ropa)
- Programming Book, Design Book (Libros)
- Desk Lamp (Hogar)

Todos incluyen stock y precios variados.

### Usuarios precargados

Estos usuarios se crean automáticamente para pruebas:

| Email | Password | Rol | Descripción |
|-------|----------|-----|------------|
| admin@example.invalid | change-me-admin | ADMIN | Administrador del sistema |
| user@example.invalid | change-me-user | USER | Cliente de prueba 1 |
| user2@example.invalid | change-me-user-2 | USER | Cliente de prueba 2 |

Cada usuario tiene un cliente asociado con dirección de ejemplo.

Verás mensajes `✓` en la consola confirmar la carga de datos.

