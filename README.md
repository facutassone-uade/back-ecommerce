# e-commerce

API REST de un e-commerce hecha con Spring Boot. Proyecto de la cátedra Aplicaciones Interactivas (UADE).

## Stack

- Java 17
- Spring Boot (Web, Data JPA, DevTools)
- MySQL 8 (vía Docker)
- Lombok
- Maven (con wrapper, no hace falta tener Maven instalado)

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
