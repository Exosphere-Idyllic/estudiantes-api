package ec.edu.istq.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestPostgreSQL {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/estudiantes_db";
        String user = "postgres";
        String password = "admin123";

        try {
            // 1. Cargar driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Driver cargado");

            // 2. Conectar
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión establecida a: " + url);

            // 3. Ejecutar consulta
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM estudiantes");

            if (rs.next()) {
                System.out.println("✅ Total de estudiantes: " + rs.getInt("total"));
            }

            // 4. Mostrar algunos estudiantes
            rs = stmt.executeQuery("SELECT id, cedula, nombres, carrera FROM estudiantes LIMIT 3");
            System.out.println("\n📋 Algunos estudiantes:");
            while (rs.next()) {
                System.out.printf("ID: %d | Cédula: %s | Nombre: %s | Carrera: %s%n",
                        rs.getInt("id"),
                        rs.getString("cedula"),
                        rs.getString("nombres"),
                        rs.getString("carrera"));
            }

            // 5. Cerrar conexión
            conn.close();
            System.out.println("\n✅ Prueba completada exitosamente!");

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}