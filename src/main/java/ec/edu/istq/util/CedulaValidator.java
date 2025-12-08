package ec.edu.istq.util;

/**
 * VALIDADOR DE CÉDULA ECUATORIANA
 * Implementa el algoritmo correcto de validación para cédulas de Ecuador.
 * Las posiciones IMPARES (índices 0,2,4,6,8) se multiplican por 2.
 */
public class CedulaValidator {

    /**
     * VERIFICA SI UNA CÉDULA ES VÁLIDA
     * @param cedula Número de cédula a validar
     * @return true si la cédula es válida según algoritmo ecuatoriano
     */
    public static boolean esValida(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }

        // Limpiar espacios y guiones
        cedula = cedula.trim().replace("-", "");

        // Validar longitud (10 dígitos para cédula ecuatoriana)
        if (cedula.length() != 10) {
            return false;
        }

        // Validar que todos los caracteres sean dígitos
        if (!cedula.matches("\\d{10}")) {
            return false;
        }

        // Validar código de provincia (01-24)
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        // Validar tercer dígito (debe ser menor a 6 para personas naturales)
        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito > 5) {
            return false;
        }

        // Aplicar algoritmo de validación
        int suma = 0;
        int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));

        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(cedula.substring(i, i + 1));

            // Las posiciones IMPARES (índices 0, 2, 4, 6, 8) se multiplican por 2
            if (i % 2 == 0) {
                digito = digito * 2;
                if (digito > 9) {
                    digito = digito - 9;
                }
            }

            suma += digito;
        }

        // Calcular dígito verificador esperado
        int residuo = suma % 10;
        int digitoEsperado = (residuo == 0) ? 0 : 10 - residuo;

        // Comparar con el dígito verificador proporcionado
        return digitoVerificador == digitoEsperado;
    }

    /**
     * VALIDA CÉDULA Y RETORNA MENSAJE DE ERROR DETALLADO
     * @param cedula Número de cédula a validar
     * @return Mensaje de error o null si es válida
     */
    public static String validarConMensaje(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return "La cédula no puede estar vacía";
        }

        cedula = cedula.trim().replace("-", "");

        if (cedula.length() != 10) {
            return "La cédula debe tener 10 dígitos";
        }

        if (!cedula.matches("\\d{10}")) {
            return "La cédula debe contener solo números";
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return "Código de provincia inválido (01-24)";
        }

        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito > 5) {
            return "Tercer dígito inválido (debe ser < 6)";
        }

        if (!esValida(cedula)) {
            return "La cédula no es válida (dígito verificador incorrecto)";
        }

        return null; // Válida
    }

    /**
     * FORMATEA UNA CÉDULA CON GUIONES
     * Ejemplo: 1713175071 -> 171317507-1
     * @param cedula Cédula sin formato
     * @return Cédula formateada
     */
    public static String formatear(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return cedula;
        }
        return cedula.substring(0, 9) + "-" + cedula.substring(9);
    }

    /**
     * EXTRAE LA PROVINCIA DE LA CÉDULA
     * @param cedula Cédula ecuatoriana
     * @return Código de provincia (1-24) o -1 si no se puede determinar
     */
    public static int obtenerProvincia(String cedula) {
        if (cedula == null || cedula.length() < 2) {
            return -1;
        }

        try {
            return Integer.parseInt(cedula.substring(0, 2));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * MÉTODO DE PRUEBA
     */
    public static void main(String[] args) {
        // Cédulas válidas de ejemplo
        String[] cedulas = {
                "1713175071", // Pichincha - VÁLIDA
                "0926687856", // Guayas - VÁLIDA
                "0102893954", // Azuay - VÁLIDA
                "1234567890"  // INVÁLIDA
        };

        System.out.println("=== PRUEBA DE VALIDACIÓN ===");
        for (String cedula : cedulas) {
            System.out.println("\nCédula: " + cedula);
            System.out.println("¿Válida?: " + (esValida(cedula) ? "✅ SÍ" : "❌ NO"));
            String mensaje = validarConMensaje(cedula);
            if (mensaje != null) {
                System.out.println("Error: " + mensaje);
            }
        }
    }
}