package ec.edu.istq.util;

/**
 * VALIDADOR DE CÉDULA ECUATORIANA
 * Implementa algoritmo de validación para cédulas de Ecuador.
 * Basado en el módulo 10 (algoritmo de Luhn modificado).
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

        // Extraer los primeros 9 dígitos y el dígito verificador
        String primeros9 = cedula.substring(0, 9);
        int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));

        // Aplicar algoritmo de validación
        int suma = 0;

        for (int i = 0; i < primeros9.length(); i++) {
            int digito = Integer.parseInt(primeros9.substring(i, i + 1));

            // Para posiciones pares (considerando índice 0 como posición 1)
            if ((i + 1) % 2 == 0) {
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

        if (!esValida(cedula)) {
            return "La cédula no es válida (algoritmo de verificación falló)";
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
}