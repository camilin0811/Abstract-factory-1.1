# Taller Mecánico AutoIA — Patrón Abstract Factory

## Caso de la vida real

Un taller mecánico atiende dos familias de vehículos completamente distintas:
**vehículos a gasolina** y **vehículos eléctricos**. Cada familia necesita su
propio motor, su propio repuesto crítico y su propio asistente de
**diagnóstico con IA**, entrenado con las fallas típicas de ese tipo de
vehículo. El taller nunca debe mezclar piezas de una familia con otra (por
ejemplo, no se le puede sugerir un juego de bujías a un auto eléctrico).

El patrón **Abstract Factory** resuelve esto: el taller (`TallerMecanico`)
solo conoce la interfaz `VehicleFactory` y, sin importar qué fábrica
concreta reciba, siempre obtiene un conjunto de productos compatible entre
sí (motor + repuesto + IA de diagnóstico de la misma familia).

## Estructura del patrón

| Rol | Clase / Interfaz |
|---|---|
| Fábrica abstracta | `VehicleFactory` |
| Fábricas concretas | `GasolineVehicleFactory`, `ElectricVehicleFactory` |
| Producto abstracto | `Engine` |
| Producto abstracto | `SparePart` |
| Producto abstracto | `DiagnosticAI` |
| Productos concretos (gasolina) | `GasolineEngine`, `SparkPlugPart`, `GroqDiagnosticAI("gasolina")` |
| Productos concretos (eléctrico) | `ElectricEngine`, `BatteryCellPart`, `GroqDiagnosticAI("electrico")` |
| Cliente | `TallerMecanico` |

## La IA del proyecto: Groq

`DiagnosticAI` ahora se implementa con **`GroqDiagnosticAI`**, que consulta
un modelo de lenguaje real a través de la [API de Groq](https://console.groq.com/)
(compatible con el formato de Chat Completions de OpenAI). El tipo de
vehículo (`"gasolina"` o `"electrico"`) se inyecta en el prompt del sistema
para que el modelo razone con el contexto correcto de cada familia de
productos, y los síntomas reportados por el cliente se envían como prompt
de usuario.

El modelo responde en un formato fijo de 3 líneas (`FALLA`, `CONFIANZA`,
`RECOMENDACION`), que `GroqDiagnosticAI` parsea con una expresión regular
para construir el `DiagnosticResult`. Todo el cliente HTTP (`GroqClient`) y
el manejo de JSON (`JsonUtil`) están escritos a mano con `java.net.http`,
sin librerías externas, para poder seguir compilando el proyecto solo con
`javac`.

### Configurar la clave de Groq

1. Crea una cuenta gratuita y genera una clave en https://console.groq.com/keys
2. Configúrala de una de estas dos formas:
   - **Variable de entorno** (recomendado):
     ```powershell
     $env:GROQ_API_KEY = "tu_clave_aqui"
     ```
   - **Archivo local**: copia `groq.properties.example` como `groq.properties`
     en la raíz del proyecto y pega tu clave ahí. Este archivo está en
     `.gitignore`, así que nunca se sube a GitHub.
3. (Opcional) Puedes cambiar el modelo con la variable de entorno
   `GROQ_MODEL` (por defecto usa `openai/gpt-oss-120b`, disponible en el
   free tier de Groq).

> Si `GROQ_API_KEY` no está configurada, el programa no se cae: cada
> diagnóstico devuelve un `DiagnosticResult` explicando el error
> ("IA no configurada"), gracias a que `GroqDiagnosticAI` captura la
> excepción en vez de propagarla.

### Alternativa offline (sin conexión a internet)

El proyecto conserva `GasolineDiagnosticAI` y `ElectricDiagnosticAI`, una
versión simulada (sistema experto basado en reglas, sin llamadas HTTP).
Gracias a que todas implementan la misma interfaz `DiagnosticAI`, para
volver a la versión offline solo hay que cambiar una línea en la fábrica
correspondiente, por ejemplo en `GasolineVehicleFactory`:

```java
// En vez de:
return new GroqDiagnosticAI("gasolina");
// usar:
return new GasolineDiagnosticAI();
```

Esto es exactamente lo que demuestra el patrón Abstract Factory: el cliente
(`TallerMecanico`) no cambia ni una línea al intercambiar la implementación
de la IA.

## Cómo ejecutar

Compilar:

```bash
javac -d out src/main/java/com/taller/mecanica/*.java
```

Ejecutar (requiere `GROQ_API_KEY` configurada y conexión a internet):

```bash
java -cp out com.taller.mecanica.Main
```

## Ejemplo de salida

```
=== Atendiendo vehiculo ABC-123 ===
Encendiendo motor de combustión interna... vroom!
Ficha tecnica: Motor a gasolina 4 cilindros, 1.6L, inyección electrónica
Repuesto sugerido en stock: Juego de bujías de iridio ($45.90)
Diagnostico IA -> Inyeccion de combustible defectuosa (confianza: 78%) -> Revisar y limpiar o reemplazar los inyectores y verificar el filtro de aire.

=== Atendiendo vehiculo EV-777 ===
Activando motor electrico... silencioso y listo.
Ficha tecnica: Motor electrico sincrono, 150kW, baterias de iones de litio 60kWh
Repuesto sugerido en stock: Modulo de celdas de bateria de litio ($890.00)
Diagnostico IA -> Degradacion de la bateria / fallo del BMS (confianza: 78%) -> Realizar diagnostico del BMS y pruebas de carga, inspeccionar conexiones y considerar reemplazo de modulos de bateria o del BMS segun resultados.
```

> La respuesta exacta del modelo puede variar entre ejecuciones porque es
> una IA generativa real, no un resultado fijo.
