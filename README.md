# e-commerce

API REST de un e-commerce hecha con Spring Boot. Proyecto de la cátedra Aplicaciones Interactivas (UADE).

## Stack

- Java 17
- Spring Boot (Web, Data JPA, DevTools)
- H2 (base en memoria para desarrollo)
- Lombok
- Maven (con wrapper, no hace falta tener Maven instalado)

## Cómo levantarlo

```bash
./mvnw spring-boot:run
```

También se puede correr desde VSCode con el **Spring Boot Dashboard** o el botón `Run` sobre `ECommerceApplication.java`.

La app queda en `http://localhost:8080`.

## Base de datos

H2 en memoria, se resetea cada vez que se para la app. Consola web en:

```
http://localhost:8080/h2-console
```

- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (vacío)

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
