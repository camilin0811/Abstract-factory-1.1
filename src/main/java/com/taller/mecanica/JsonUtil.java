package com.taller.mecanica;

import java.util.Optional;

/**
 * Utilidades minimas de JSON (sin dependencias externas) usadas para
 * construir el cuerpo de la peticion a Groq y para extraer el campo
 * "content" de la respuesta.
 */
final class JsonUtil {

    private JsonUtil() {
    }

    static String escape(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Busca la primera aparicion de "key":"..." en un JSON y devuelve su
     * valor ya des-escapado. Suficiente para respuestas simples como las
     * de la API de Groq, sin necesidad de un parser JSON completo.
     */
    static Optional<String> extractStringField(String json, String key) {
        String marker = "\"" + key + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return Optional.empty();
        }

        int i = start + marker.length();
        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':
                        sb.append('"');
                        i += 2;
                        continue;
                    case '\\':
                        sb.append('\\');
                        i += 2;
                        continue;
                    case 'n':
                        sb.append('\n');
                        i += 2;
                        continue;
                    case 'r':
                        sb.append('\r');
                        i += 2;
                        continue;
                    case 't':
                        sb.append('\t');
                        i += 2;
                        continue;
                    case '/':
                        sb.append('/');
                        i += 2;
                        continue;
                    case 'u':
                        if (i + 5 < json.length()) {
                            String hex = json.substring(i + 2, i + 6);
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 6;
                            continue;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (c == '"') {
                return Optional.of(sb.toString());
            }
            sb.append(c);
            i++;
        }
        return Optional.empty();
    }
}
