# Santiago Sacro — API REST

Backend del MVP **Santiago Sacro** (*Templos, arte e historia de Santiago*),
asignatura DSY1105 Desarrollo de Aplicaciones Moviles, Duoc UC.

Red Patrimonial del Arzobispado de Santiago.

---

## 1. Tecnologias

| Capa | Tecnologia |
|---|---|
| Lenguaje | Kotlin 2.1 |
| Framework | Spring Boot 3.4 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria (cambiable a MySQL) |
| Seguridad | Spring Security + JWT (jjwt) + BCrypt |
| Build | Gradle (Kotlin DSL) |
| Pruebas | JUnit 5 + kotlin.test |
| JDK | 21 |

## 2. Como ejecutar

Desde IntelliJ IDEA: abrir la carpeta `backend` como proyecto Gradle y ejecutar
`SantiagoSacroApplication.kt`.

Desde la terminal:

```bash
cd backend
./gradlew bootRun          # Linux / macOS
gradlew.bat bootRun        # Windows
```

La API queda en `http://localhost:8080`.
Consola de la base de datos: `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:santiagosacro`, usuario `sa`, sin clave).

Pruebas unitarias:

```bash
./gradlew test
```

> **Importante para el emulador de Android:** desde el emulador, `localhost` es el
> propio telefono. El computador se alcanza en `http://10.0.2.2:8080`.

## 3. Usuarios cargados automaticamente

| Correo | Clave | Rol | Recintos que administra |
|---|---|---|---|
| admin@santiagosacro.cl | Admin123 | ADMIN | — |
| visitante@correo.cl | Visita123 | VISITANTE | — |
| encargado.sanfrancisco@iglesiadesantiago.cl | Encargado123 | ENCARGADO | San Francisco, Catedral |
| encargado.lamerced@iglesiadesantiago.cl | Encargado123 | ENCARGADO | La Merced |

Todos los datos son **ficticios y sinteticos**, conforme al punto 4 del anexo de
requerimiento. Las coordenadas son aproximadas y de prueba.

## 4. Endpoints

### Publicos

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/auth/registro` | Registro de visitante |
| POST | `/api/auth/login` | Login, devuelve el JWT |
| GET | `/api/recintos` | Listado, filtros `q`, `comuna`, `estado` |
| GET | `/api/recintos/cercanos` | Por GPS: `latitud`, `longitud`, `radioKm` |
| GET | `/api/recintos/{id}` | Ficha patrimonial completa |
| GET | `/api/eventos` | Cartelera: `desde`, `hasta`, `idCategoria` |
| GET | `/api/eventos/{id}` | Detalle del evento |
| GET | `/api/avisos/vigentes` | Avisos activos sobre el mapa |
| GET | `/api/catalogos/categorias` | Categorias de evento |
| GET | `/api/catalogos/tipos-recinto` | Tipos de recinto |
| GET | `/api/catalogos/estados-apertura` | ABIERTO, CERRADO, EN_CELEBRACION |

### Requieren token

| Metodo | Ruta | Rol |
|---|---|---|
| GET | `/api/auth/perfil` | Autenticado |
| POST | `/api/recintos/{id}/solicitudes-guia` | Autenticado |
| GET | `/api/solicitudes/mias` | Autenticado |
| PATCH | `/api/recintos/{id}/estado` | ENCARGADO |
| POST | `/api/recintos/{id}/bloques` | ENCARGADO |
| POST | `/api/recintos/{id}/eventos` | ENCARGADO |
| PUT | `/api/eventos/{id}` | ENCARGADO |
| DELETE | `/api/eventos/{id}` | ENCARGADO (cancela, no borra) |
| POST | `/api/recintos/{id}/avisos` | ENCARGADO |
| GET | `/api/encargado/mis-recintos` | ENCARGADO |
| GET | `/api/encargado/solicitudes` | ENCARGADO |
| GET | `/api/encargado/recintos/{id}/guias` | ENCARGADO |
| PATCH | `/api/solicitudes/{id}` | ENCARGADO |

El token se envia en la cabecera: `Authorization: Bearer <token>`.

## 5. Estructura del proyecto

```
cl.duoc.santiagosacro
├── domain        Entidades JPA (el modelo relacional hecho codigo)
├── repository    Interfaces JpaRepository (acceso a datos)
├── dto           Objetos de entrada y salida de la API
├── service       Reglas de negocio y validaciones
├── controller    Endpoints REST
├── security      JWT, filtro, configuracion de Spring Security
├── config        Carga de datos sinteticos
├── exception     Excepciones propias y manejador global
└── util          Calculo de distancias (Haversine)
```

El flujo de una peticion es siempre el mismo:

```
Cliente → Controller → Service → Repository → Base de datos
                 ↑          ↓
                DTO      Entidad
```

Los controladores nunca devuelven entidades: siempre DTO. Eso evita exponer la
estructura interna de la base y los problemas de serializacion de relaciones JPA.

## 6. Decisiones de diseno defendibles

- **H2 en memoria:** el proyecto levanta sin instalar nada. Para migrar a MySQL
  basta cambiar `application.yml` y agregar el driver.
- **BCrypt:** la clave nunca se guarda en texto plano. Cada hash lleva su propia sal.
- **JWT sin estado:** el servidor no guarda sesiones, lo que encaja con una app movil.
- **Roles:** `VISITANTE`, `ENCARGADO` y `ADMIN`. El registro publico siempre crea
  visitantes; las cuentas de encargado las crea el Area de Cultura.
- **Autorizacion por recinto:** no basta con ser ENCARGADO. `verificarQueAdministra`
  comprueba que el recinto este entre los que ese usuario tiene asignados.
- **Cancelar en vez de borrar:** un evento cancelado conserva su registro.
- **`open-in-view: false`:** obliga a resolver las relaciones dentro de la
  transaccion, que es la practica correcta.

## 7. Pendiente para la segunda entrega

- Rutas patrimoniales (`ruta_sugerida`, `ruta_parada`) y calculo de recorrido.
- Favoritos sincronizados (en la primera entrega viven solo en Room).
- Carga real de imagenes; hoy la galeria guarda URLs sinteticas.
- `fecha_asignacion` de la tabla `usuario_recinto`: la relacion esta implementada
  como `@ManyToMany`, que no persiste ese atributo del modelo relacional.
