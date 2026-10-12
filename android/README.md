# Santiago Sacro — App Android

Aplicacion movil del MVP **Santiago Sacro**, asignatura DSY1105, Duoc UC.

## 1. Tecnologias

| Capa | Tecnologia |
|---|---|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Navigation Compose |
| Persistencia local | Room (SQLite) |
| Red | Retrofit + OkHttp |
| Recurso nativo | GPS (FusedLocationProvider) |
| minSdk / targetSdk | 24 / 35 |

## 2. Como ejecutar

1. Levantar primero el backend (carpeta `backend`), que debe quedar en el puerto 8080.
2. Abrir la carpeta `android` en Android Studio y esperar la sincronizacion de Gradle.
3. Ejecutar en un emulador con Play Services (por ejemplo Pixel 6, API 34).

> La URL de la API esta en `app/build.gradle.kts`, en `buildConfigField URL_API`.
> Por defecto es `http://10.0.2.2:8080/`, que es como el **emulador** ve el
> computador. Si pruebas en un telefono fisico, cambiala por la IP de tu equipo
> en la red local, por ejemplo `http://192.168.1.15:8080/`.

Pruebas unitarias:

```bash
./gradlew test
```

## 3. Pantallas construidas

| Pantalla | Archivo | Que resuelve |
|---|---|---|
| Mapa | `PantallaMapa.kt` | HU03, HU07: mapa ilustrado, busqueda, filtro y GPS |
| Ficha patrimonial | `PantallaFicha.kt` | HU04, HU06, HU26: datos, horarios, favorito |
| Cartelera | `PantallaCartelera.kt` | HU09, HU27: eventos y filtro por categoria |
| Mi ruta | `PantallaRuta.kt` | HU16, HU17, HU18: paradas y tiempos estimados |
| Favoritos | `PantallaFavoritos.kt` | HU26: resuelto solo con Room |
| Acceso | `PantallaAcceso.kt` | HU01, HU02: login y cierre de sesion |
| Registro | `PantallaRegistro.kt` | Registro de visitante |
| Panel del encargado | `PantallaPanel.kt` | HU11, HU12, HU23, HU24 |

## 4. Arquitectura MVVM

```
Pantalla (Compose)  ->  ViewModel  ->  Repositorio  ->  Retrofit (API)
      ^                    |                        \->  Room (local)
      |                    v
      +------------ StateFlow con el estado de la UI
```

- La **pantalla** solo dibuja y avisa de los eventos del usuario. No conoce la red.
- El **ViewModel** mantiene el estado en un `StateFlow` y sobrevive a los giros de pantalla.
- El **repositorio** decide de donde salen los datos: del backend o de Room.
- `Contenedor.kt` arma las dependencias a mano, sin Hilt, para que el flujo sea
  facil de seguir y de explicar.

## 5. Decisiones defendibles

- **Mapa dibujado con Canvas:** proyecta la latitud y la longitud reales de cada
  recinto sobre el lienzo. Evita depender de una API key de Google Maps en esta
  entrega y mantiene el estilo del mockup. Migrar a Maps SDK es cambiar un
  Composable.
- **GPS opcional:** si el usuario niega el permiso, la app sigue funcionando y
  simplemente no ordena por cercania.
- **Room como copia de consulta:** recintos y favoritos quedan disponibles sin
  conexion. Las rutas del visitante existen solo en el telefono.
- **El token no se guarda en Room:** vive en SharedPreferences, porque es
  configuracion del dispositivo y no informacion del dominio.
- **Interceptor de OkHttp:** agrega la cabecera `Authorization` automaticamente,
  asi ningun repositorio tiene que acordarse del token.
- **`Resultado<T>`:** envuelve exitos y errores para que la interfaz nunca reciba
  una excepcion suelta.

## 6. Pendiente para la segunda entrega

- Agendamiento de visitas desde la app (el endpoint ya existe en el backend).
- Sincronizar favoritos con el servidor.
- Camara para que el encargado suba fotos del recinto.
- Rutas sugeridas por la organizacion.
- Firma del APK en modo release.
