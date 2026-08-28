# AutoAI Repair Shop — Abstract Factory Pattern

## Real-life case

An auto repair shop services two completely different vehicle families:
**gasoline vehicles** and **electric vehicles**. Each family needs its own
engine, its own critical spare part, and its own **AI diagnostic
assistant**, tuned to the typical faults of that vehicle type. The shop
must never mix parts from one family with another (for example, it should
never suggest a spark plug set for an electric car).

The **Abstract Factory** pattern solves this: the shop (`AutoRepairShop`)
only knows the `VehicleFactory` interface, and no matter which concrete
factory it receives, it always gets a compatible set of products (engine +
spare part + diagnostic AI from the same family).

## Pattern structure

| Role | Class / Interface |
|---|---|
| Abstract factory | `VehicleFactory` |
| Concrete factories | `GasolineVehicleFactory`, `ElectricVehicleFactory` |
| Abstract product | `Engine` |
| Abstract product | `SparePart` |
| Abstract product | `DiagnosticAI` |
| Concrete products (gasoline) | `GasolineEngine`, `SparkPlugPart`, `GroqDiagnosticAI("gasoline")` |
| Concrete products (electric) | `ElectricEngine`, `BatteryCellPart`, `GroqDiagnosticAI("electric")` |
| Client | `AutoRepairShop` |

## The project's AI: Groq

`DiagnosticAI` is implemented by **`GroqDiagnosticAI`**, which queries a
real language model through the [Groq API](https://console.groq.com/)
(compatible with OpenAI's Chat Completions format). The vehicle type
(`"gasoline"` or `"electric"`) is injected into the system prompt so the
model reasons with the right context for each product family, and the
customer's reported symptoms are sent as the user prompt.

The model replies in a fixed 3-line format (`FAULT`, `CONFIDENCE`,
`RECOMMENDATION`), which `GroqDiagnosticAI` parses with a regular
expression to build the `DiagnosticResult`. The HTTP client (`GroqClient`)
and the JSON handling (`JsonUtil`) are hand-written with `java.net.http`,
with no external libraries, so the project keeps compiling with plain
`javac`.

### Setting up the Groq key

1. Create a free account and generate a key at https://console.groq.com/keys
2. Configure it in one of two ways:
   - **Environment variable** (recommended):
     ```powershell
     $env:GROQ_API_KEY = "your_key_here"
     ```
   - **Local file**: copy `groq.properties.example` as `groq.properties` in
     the project root and paste your key there. This file is in
     `.gitignore`, so it never gets pushed to GitHub.
3. (Optional) You can change the model with the `GROQ_MODEL` environment
   variable (defaults to `openai/gpt-oss-120b`, available on Groq's free
   tier).

> If `GROQ_API_KEY` is not set, the program does not crash: every diagnosis
> returns a `DiagnosticResult` explaining the error ("AI not configured"),
> because `GroqDiagnosticAI` catches the exception instead of propagating
> it.

### Offline alternative (no internet connection)

The project keeps `GasolineDiagnosticAI` and `ElectricDiagnosticAI`, a
simulated version (a rule-based expert system, no HTTP calls). Since all
of them implement the same `DiagnosticAI` interface, switching back to the
offline version only requires changing one line in the corresponding
factory, for example in `GasolineVehicleFactory`:

```java
// Instead of:
return new GroqDiagnosticAI("gasoline");
// use:
return new GasolineDiagnosticAI();
```

This is exactly what the Abstract Factory pattern demonstrates: the client
(`AutoRepairShop`) does not change a single line when the AI implementation
is swapped out.

## How to run it

Compile:

```bash
javac -d out src/main/java/com/autoshop/mechanic/*.java
```

Run (requires `GROQ_API_KEY` to be set and an internet connection):

```bash
java -cp out com.autoshop.mechanic.Main
```

## Sample output

```
=== Servicing vehicle ABC-123 ===
Starting internal combustion engine... vroom!
Technical specs: 4-cylinder gasoline engine, 1.6L, electronic fuel injection
Suggested spare part in stock: Iridium spark plug set ($45.90)
AI diagnosis -> Stuck open fuel injector (confidence: 70%) -> Remove and bench-test the fuel injector(s), replace any that leak, then clear any flood condition and verify proper idle after reinstall.

=== Servicing vehicle EV-777 ===
Activating electric motor... quiet and ready.
Technical specs: Synchronous electric motor, 150kW, 60kWh lithium-ion battery pack
Suggested spare part in stock: Lithium battery cell module ($890.00)
AI diagnosis -> BMS fault (confidence: 75%) -> Perform a full BMS and high-voltage battery diagnostic, inspect charger and HV connections, and replace the BMS or battery pack if faults are confirmed.
```

> The exact model response may vary between runs because it is a real
> generative AI, not a fixed result.
