package com.taller.mecanica;

import java.util.List;

/**
 * Producto abstracto: asistente de diagnóstico inteligente.
 * Cada familia de vehículos tiene su propia IA, entrenada
 * (mediante una base de reglas) con las fallas típicas de ese tipo de motor.
 */
public interface DiagnosticAI {
    DiagnosticResult diagnose(List<String> symptoms);
}
