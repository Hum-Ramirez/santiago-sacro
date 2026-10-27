# Santiago Sacro

*Templos, arte e historia de Santiago*

MVP movil para la Red Patrimonial del Arzobispado de Santiago.
Asignatura **DSY1105 Desarrollo de Aplicaciones Moviles**, Duoc UC.

Entrega correspondiente a la **Prueba 2**.

## Contenido del repositorio

```
santiago-sacro/
├── backend/   API REST en Kotlin + Spring Boot (JWT, JPA, H2)
└── android/   App movil en Kotlin + Jetpack Compose (MVVM, Room)
```

Cada carpeta tiene su propio README con instrucciones detalladas.

## Las dos partes no estan conectadas en esta entrega

El enunciado de la Prueba 2 indica que la aplicacion movil **no debe conectarse
al backend en esta etapa**, y que la integracion se realiza durante la Unidad 3.

Por eso cada parte se prueba por separado:

- **La app movil funciona sola.** Su base de datos local es la fuente de datos,
  no un cache. No hay que levantar ningun servidor.
- **El backend funciona solo.** Se prueba con Postman o con el archivo
  `peticiones.http`.

La capa de red de la app (Retrofit, los DTO y el interceptor del token) sigue
construida y compilando, pero ningun repositorio la consume.

## Como probar cada parte

**App movil:** abrir `android` en Android Studio y ejecutar en un emulador con
Google Play Services o en un telefono fisico. Los datos se siembran solos en el
primer arranque.

**Backend:** abrir `backend` en IntelliJ y ejecutar `SantiagoSacroApplication.kt`.
Queda en `http://localhost:8080` con datos sinteticos ya cargados. Para probar
los endpoints, abrir `backend/peticiones.http` en IntelliJ o importar
`backend/SantiagoSacro.postman_collection.json` en Postman.

## Cobertura de la Prueba 2

| Exigencia del enunciado | Estado |
|---|---|
| Backend con avance minimo del 75% | 24 endpoints, 14 tablas, JWT con roles y autorizacion por recinto |
| Interfaz grafica al 100% con navegacion funcional | 10 pantallas enlazadas con Navigation Compose |
| Base de datos local | Room con 7 tablas, como fuente de datos de la aplicacion |
| Lectura de codigos QR | ML Kit Google Code Scanner, sin permiso de camara |
| Video de presentacion | Entregado aparte |

Funcionalidad adicional, no exigida por esta entrega: geolocalizacion con
`FusedLocationProvider` y ordenamiento por cercania con la formula de Haversine.

## Alcance frente a las historias de usuario

De las 31 historias comprometidas, **22 estan implementadas**, 1 es parcial,
1 esta diferida por instruccion del enunciado (HU14, sincronizacion) y 7 quedan
pendientes, de las cuales 6 son de prioridad *Should*. De las 18 historias
**Must**, 16 estan construidas.

El detalle historia por historia, con su estado y la pantalla que la resuelve,
esta en el documento de historias de usuario de la entrega.

## Datos ficticios

Conforme al punto 4 del anexo de requerimiento, el sistema funciona
exclusivamente con datos sinteticos creados para el ejercicio academico. No se
utiliza informacion real de las entidades del Arzobispado de Santiago.