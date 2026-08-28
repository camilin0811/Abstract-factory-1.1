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
| Productos concretos (gasolina) | `GasolineEngine`, `SparkPlugPart`, `GasolineDiagnosticAI` |
| Productos concretos (eléctrico) | `ElectricEngine`, `BatteryCellPart`, `ElectricDiagnosticAI` |
| Cliente | `TallerMecanico` |

## La "IA" del proyecto

`DiagnosticAI` es un **sistema experto basado en reglas**: cada implementación
(`GasolineDiagnosticAI`, `ElectricDiagnosticAI`) tiene una base de
conocimiento que asocia síntomas reportados por el cliente con posibles
fallas y un peso de sospecha. Al recibir una lista de síntomas, el motor de
inferencia suma los pesos de todas las fallas relacionadas y elige la de
mayor puntaje, calculando además un porcentaje de confianza. El resultado
es un `DiagnosticResult` con la falla probable, la confianza y una
recomendación para el cliente.

Esto simula el comportamiento de un módulo de IA sin depender de
librerías externas ni conexión a internet, ideal para un entorno académico.

## Cómo ejecutar

Compilar:

```bash
javac -d out src/main/java/com/taller/mecanica/*.java
```

Ejecutar:

```bash
java -cp out com.taller.mecanica.Main
```

## Ejemplo de salida

```
=== Atendiendo vehiculo ABC-123 ===
Encendiendo motor de combustión interna... vroom!
Ficha tecnica: Motor a gasolina 4 cilindros, 1.6L, inyección electrónica
Repuesto sugerido en stock: Juego de bujías de iridio ($45.90)
Diagnostico IA -> Mezcla de combustible demasiado rica (confianza: 25%) -> Revisar sensores de oxigeno e inyectores.

=== Atendiendo vehiculo EV-777 ===
Activando motor electrico... silencioso y listo.
Ficha tecnica: Motor electrico sincrono, 150kW, baterias de iones de litio 60kWh
Repuesto sugerido en stock: Modulo de celdas de bateria de litio ($890.00)
Diagnostico IA -> Degradacion de la bateria (confianza: 29%) -> Diagnostico de salud de bateria (State of Health).
```

> Nota: cuando dos fallas empatan en puntaje, el orden de desempate depende
> del `HashMap` interno, por lo que el resultado exacto puede variar levemente
> entre ejecuciones. Esto no afecta la lógica del patrón ni la del diagnóstico.
