# Santiago Sacro — App Android

Aplicacion movil del MVP **Santiago Sacro**, asignatura DSY1105, Duoc UC.
Entrega correspondiente a la Prueba 2.

## 1. Tecnologias

| Capa | Tecnologia |
|---|---|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Navigation Compose |
| Persistencia local | Room (SQLite) — 7 tablas |
| Lectura de QR | ML Kit Google Code Scanner |
| Recurso nativo | GPS (FusedLocationProvider) |
| Red | Retrofit + OkHttp — construida, sin consumir en esta entrega |
| minSdk / targetSdk | 24 / 35 |

## 2. Nota sobre la conexion con el backend

El enunciado de la Prueba 2 indica que la aplicacion movil **no debe conectarse
al backend en esta etapa**. En consecuencia:

- La **base de datos local es la fuente de datos** de toda la aplicacion, no un
  cache de respaldo.
- La capa de red (`data/remote`) sigue construida y compilando, pero **ningun
  repositorio la consume**. Queda lista para la Unidad 3.
- La aplicacion arranca y funciona completa sin ningun servidor encendido.

El cambio se hizo sin modificar pantallas ni ViewModels: los repositorios
conservaron sus firmas y solo cambio de donde construyen los DTO.

## 3. Como ejecutar

1. Abrir la carpeta `android` en Android Studio y esperar la sincronizacion de Gradle.
2. Ejecutar en un emulador **con Google Play Services** (el lector de QR lo
   necesita) o en un telefono fisico.
3. No hay que levantar nada mas. Los datos se siembran solos en el primer arranque.

Para probar el QR, generar un codigo con el texto `SANTIAGOSACRO-RECINTO-2`.
El numero final es el id del recinto: 1 Catedral, 2 San Francisco, 3 La Merced,
4 Santo Domingo, 5 San Agustin, 6 Sacramentinos.

En el emulador, la camara trasera debe configurarse como `Webcam0` para poder
escanear un codigo mostrado frente al computador.

Pruebas unitarias:

```bash
./gradlew testDebugUnitTest
```

### Cuentas de prueba

| Correo | Clave | Rol |
|---|---|---|
| `visitante@correo.cl` | `Visita123` | Visitante |
| `encargado.sanfrancisco@iglesiadesantiago.cl` | `Encargado123` | Encargado de San Francisco |
| `encargado.lamerced@iglesiadesantiago.cl` | `Encargado123` | Encargado de La Merced |
| `admin@santiagosacro.cl` | `Admin123` | Administrador |

Las claves se guardan en texto plano en la tabla local. Es una decision
consciente: una validacion que corre en el propio telefono es una demostracion
del flujo de acceso, no un control de seguridad. La autenticacion real vive en
el backend, con BCrypt y JWT.

## 4. Pantallas construidas

| Pantalla | Archivo | Que resuelve |
|---|---|---|
| Mapa | `PantallaMapa.kt` | HU03, HU07, HU08: mapa ilustrado, busqueda y GPS |
| Ficha patrimonial | `PantallaFicha.kt` | HU04, HU06, HU26: datos, horarios, patrimonio, favorito |
| Cartelera | `PantallaCartelera.kt` | HU09, HU27: eventos y filtro por categoria |
| Mi ruta | `PantallaRuta.kt` | HU16, HU17, HU18, HU20: paradas y tiempos estimados |
| Favoritos | `PantallaFavoritos.kt` | HU26: resuelto solo con Room |
| Lector QR | `PantallaQr.kt` | HU31: escaneo y apertura de la ficha |
| Solicitar guia | `PantallaSolicitud.kt` | HU21, HU22: solicitud y seguimiento del estado |
| Acceso | `PantallaAcceso.kt` | HU01, HU02: login y cierre de sesion |
| Registro | `PantallaRegistro.kt` | Registro de visitante, siempre con rol VISITANTE |
| Panel del encargado | `PantallaPanel.kt` | HU11, HU12, HU23, HU24 |

Son **10 pantallas y 9 ViewModels**: `PantallaAcceso` y `PantallaRegistro`
comparten `AccesoViewModel`, porque trabajan sobre el mismo estado de sesion.

## 5. Base de datos local

Las 7 entidades estan en `data/local/Entidades.kt`, sus consultas en `Daos.kt`,
y ambas se registran en `BaseDatos.kt`.

| Tabla | Para que |
|---|---|
| `recinto_local` | Los 6 recintos de la red |
| `horario_local` | Horario de atencion, relacion 1:N con recinto |
| `evento_local` | La cartelera |
| `usuario_local` | Validar el acceso sin backend |
| `solicitud_local` | Solicitudes de guia y su estado |
| `favorito` | Marcas del visitante |
| `parada_ruta` | Paradas del recorrido armado por el visitante |

`CategoriaLocal`, en el mismo archivo, **no es una tabla**: es la proyeccion de
un `SELECT DISTINCT` sobre `evento_local` que alimenta los chips de categoria.

### El sembrador

`Sembrador.kt` carga los datos sinteticos. Es el equivalente local del
`CargaDatosIniciales` del backend, y tiene tres propiedades deliberadas:

- **Siembra perezosa:** corre en la primera lectura desde el repositorio, no al
  arrancar la app. Asi sembrar y leer ocurren en la misma secuencia y ninguna
  pantalla puede adelantarse a los datos.
- **Idempotente:** consulta `COUNT(*)` antes de insertar. Ejecutarlo una vez o
  cien veces deja el mismo resultado, lo que permite invocarlo en cada lectura.
- **Fuera del hilo principal:** todas las funciones de los DAO son `suspend`, y
  Room despacha la consulta a un hilo de fondo. Tocar la base en el hilo de la
  interfaz esta prohibido y lanza excepcion.

La base usa `fallbackToDestructiveMigration()`: al cambiar el esquema se recrea
y el sembrador la rellena. Es aceptable porque todos los datos son sinteticos;
una aplicacion real escribiria migraciones.

## 6. Arquitectura MVVM

```
Pantalla (Compose)  ->  ViewModel  ->  Repositorio  ->  Room (fuente de datos)
      ^                    |                        \->  Retrofit (sin usar)
      |                    v
      +------------ StateFlow con el estado de la UI
```

- La **pantalla** solo dibuja y avisa de los eventos del usuario.
- El **ViewModel** mantiene el estado en un `StateFlow` y sobrevive a los giros
  de pantalla.
- El **repositorio** decide de donde salen los datos y traduce las filas de Room
  a los mismos DTO que antes llegaban de la API (`MapeadoresLocales.kt`).
- `Contenedor`, en `SantiagoSacroApp.kt`, arma las dependencias a mano, sin Hilt,
  para que el flujo sea facil de seguir y de explicar.

## 7. Decisiones defendibles

- **Lectura de QR sin permiso de camara:** el Google Code Scanner muestra su
  propia pantalla dentro de Google Play Services y devuelve solo el texto leido.
  La aplicacion nunca abre la camara, asi que no declara `CAMERA` ni pide
  permisos en tiempo de ejecucion.
- **El QR lleva un identificador, no datos:** el codigo contiene
  `SANTIAGOSACRO-RECINTO-n`. Asi la informacion se corrige en la base sin
  reimprimir las placas, el codigo se mantiene legible con poca luz, y el
  escaneo termina en una consulta a `recinto_local`.
- **Mapa dibujado con Canvas:** proyecta la latitud y longitud reales sobre el
  lienzo. Evita depender de una API key de Google Maps y mantiene el estilo del
  mockup. Migrar a Maps SDK es cambiar un Composable.
- **GPS opcional:** si el usuario niega el permiso, la app sigue funcionando y
  simplemente no ordena por cercania. La distancia se calcula con la formula de
  Haversine en `core/Geo.kt`.
- **`@Update` contra `UPDATE` por columna:** cambiar el estado de un recinto
  toca una sola columna y usa un `@Query` con `UPDATE`; aceptar una solicitud
  cambia tres campos a la vez y usa la anotacion `@Update` con la entidad
  completa.
- **`@Insert` con y sin estrategia de conflicto:** los recintos usan
  `OnConflictStrategy.REPLACE` porque son copias que se actualizan; los usuarios
  usan `@Insert` sin estrategia, con indice unico sobre el correo, porque ahi un
  choque es un error de negocio que la app debe informar.
- **`Flow` solo donde corresponde:** los DAO devuelven `Flow` cuando la pantalla
  debe reaccionar a un cambio hecho dentro de la app (favoritos, paradas de la
  ruta) y `suspend` con listas cuando es una consulta puntual.
- **El token no se guarda en Room:** vive en `SharedPreferences`, porque es
  configuracion del dispositivo y no informacion del dominio. Es lo que permite
  no volver a iniciar sesion al reabrir la app.
- **Autorizacion por recinto, no solo por rol:** dos usuarios pueden tener rol
  ENCARGADO y administrar templos distintos, asi que antes de cada escritura se
  verifica cual recinto administra el usuario de la sesion.
- **`Resultado<T>`:** envuelve exitos y errores para que la interfaz nunca reciba
  una excepcion suelta.

## 8. Limitaciones conocidas

Declaradas de forma explicita, con su prioridad MoSCoW del documento de
historias de usuario:

- **HU13 — bloque horario excepcional (Must):** el endpoint existe en el backend;
  falta la interfaz en el panel del encargado.
- **HU05 — galeria del recinto (Should):** no hay tabla local de recursos
  multimedia.
- **HU19 — rutas sugeridas (Should):** no implementada en ninguna de las dos capas.
- **HU25 — disponibilidad de guias (Should):** la nomina esta fija en el codigo,
  no es administrable.
- **HU28 y HU29 — avisos (Should):** el backend los soporta; falta la tabla local
  y la interfaz.
- **HU30 — editar o cancelar un evento (Should):** el backend lo soporta; falta
  la interfaz.
- **HU14 — sincronizacion con la red (Must):** excluida de esta entrega por
  instruccion del enunciado, no por priorizacion.
- **HU10 — ubicar un evento en el mapa (Should):** parcial. La cartelera navega a
  la ficha del recinto, pero no centra el mapa en el.
- **APK sin firmar** en modo release.

De las 31 historias de usuario, 22 estan implementadas, 1 es parcial, 1 esta
diferida por instruccion y 7 quedan pendientes. De las 18 historias **Must**,
16 estan construidas.