# Santiago Sacro

*Templos, arte e historia de Santiago*

MVP movil para la Red Patrimonial del Arzobispado de Santiago.
Asignatura **DSY1105 Desarrollo de Aplicaciones Moviles**, Duoc UC.

## Contenido del repositorio

```
santiago-sacro/
├── backend/   API REST en Kotlin + Spring Boot (JWT, JPA, H2)
└── android/   App movil en Kotlin + Jetpack Compose (MVVM, Room, Retrofit)
```

Cada carpeta tiene su propio README con instrucciones detalladas.

## Orden para probar todo

1. **Backend:** abrir `backend` en IntelliJ y ejecutar `SantiagoSacroApplication.kt`.
   Queda en `http://localhost:8080` con datos sinteticos ya cargados.
2. **Probar la API:** abrir `backend/peticiones.http` en IntelliJ, o importar
   `backend/SantiagoSacro.postman_collection.json` en Postman.
3. **App movil:** abrir `android` en Android Studio y ejecutar en un emulador.

## Cobertura de la primera entrega

| Exigencia | Estado |
|---|---|
| Login y registro de usuario | Completo, con JWT y BCrypt |
| Backend al 75% | Autenticacion, recintos, horarios, estados, eventos, avisos, guias y solicitudes |
| 100% de interfaces con navegacion | 8 pantallas enlazadas con Navigation Compose |
| Arquitectura model-view-viewmodel-navigation | Completa |
| Base de datos local (Room) | Recintos en cache, favoritos y paradas de ruta |
| Recurso del telefono | GPS con FusedLocationProvider |
| Conexion con backend al 40% | Superado: todas las pantallas consumen la API |

## Datos ficticios

Conforme al punto 4 del anexo de requerimiento, el sistema funciona
exclusivamente con datos sinteticos creados por el equipo. No se utiliza
informacion real de las entidades del Arzobispado.
