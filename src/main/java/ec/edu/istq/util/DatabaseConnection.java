package ec.edu.istq.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * GESTOR DE CONEXIONES A BASE DE DATOS
 * Implementa el patrón Singleton para reutilizar conexiones.
 * Configurado para PostgreSQL en contenedor Docker.
 */
public class DatabaseConnection {

    // ==============================================================
    // CONFIGURACIÓN DE CONEXIÓN - DOCKER POSTGRESQL
    // ==============================================================

    // URL de conexión JDBC para PostgreSQL
    // Formato: jdbc:postgresql://host:puerto/base_de_datos
    private static final String URL = "jdbc:postgresql://localhost:5432/estudiantes_db";

    // Credenciales de acceso (definidas en docker-compose.yml)
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";

    // ==============================================================
    // BLOQUE ESTÁTICO - CARGA DEL DRIVER
    // ==============================================================

    static {
        try {
            // Cargar el driver JDBC de PostgreSQL
            // Necesario para versiones antiguas de Java
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Driver PostgreSQL cargado exitosamente");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: Driver PostgreSQL no encontrado");
            System.err.println("   Asegúrate de incluir la dependencia en pom.xml");
            e.printStackTrace();
        }
    }

    // ==============================================================
    // MÉTODOS PÚBLICOS DE CONEXIÓN
    // ==============================================================

    /**
     * OBTIENE UNA CONEXIÓN A LA BASE DE DATOS
     * Usa DriverManager para crear nueva conexión
     * @return Connection objeto de conexión JDBC
     * @throws SQLException Si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("🔗 Conexión establecida con PostgreSQL");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ ERROR de conexión a PostgreSQL:");
            System.err.println("   URL: " + URL);
            System.err.println("   User: " + USER);
            System.err.println("   Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * PRUEBA LA CONEXIÓN A LA BASE DE DATOS
     * Método útil para verificar que Docker y PostgreSQL están funcionando
     * @throws SQLException Si la conexión falla
     */
    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Prueba de conexión exitosa");
                System.out.println("   Base de datos: estudiantes_db");
                System.out.println("   PostgreSQL: " + conn.getMetaData().getDatabaseProductVersion());
            }
        }
    }

    /**
     * VERIFICA EL ESTADO DE LA BASE DE DATOS
     * @return true si la base de datos es accesible
     */
    public static boolean isDatabaseAvailable() {
        try {
            testConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}