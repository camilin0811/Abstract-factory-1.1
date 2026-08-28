package com.autoshop.mechanic;

import java.util.List;

/**
 * Abstract product: intelligent diagnostic assistant.
 * Each vehicle family has its own AI, tuned (via a rule base or a real
 * language model) with the typical faults of that engine type.
 */
public interface DiagnosticAI {
    DiagnosticResult diagnose(List<String> symptoms);
}
