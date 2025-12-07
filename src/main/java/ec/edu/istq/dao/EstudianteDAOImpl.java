package ec.edu.istq.dao;

import ec.edu.istq.model.Estudiante;
import ec.edu.istq.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTACIÓN CONCRETA DEL DAO PARA ESTUDIANTE
 * Implementa las operaciones CRUD usando JDBC y PostgreSQL.
 * Usa PreparedStatements para prevenir SQL Injection.
 * Maneja recursos con try-with-resources para evitar leaks.
 */
public class EstudianteDAOImpl implements EstudianteDAO {

    // ==============================================================
    // CONSTANTES SQL (QUERIES PREPARADAS)
    // ==============================================================

    private static final String SQL_SELECT_TODOS =
            "SELECT * FROM estudiantes ORDER BY id DESC";

    private static final String SQL_SELECT_POR_ID =
            "SELECT * FROM estudiantes WHERE id = ?";

    private static final String SQL_SELECT_POR_CEDULA =
            "SELECT * FROM estudiantes WHERE cedula = ?";

    private static final String SQL_INSERT =
            "INSERT INTO estudiantes (cedula, nombres, apellidos, email, telefono, carrera, nivel) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, fecha_creacion, fecha_actualizacion";

    private static final String SQL_UPDATE =
            "UPDATE estudiantes SET cedula = ?, nombres = ?, apellidos = ?, " +
                    "email = ?, telefono = ?, carrera = ?, nivel = ?, activo = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM estudiantes WHERE id = ?";

    private static final String SQL_CAMBIAR_ESTADO =
            "UPDATE estudiantes SET activo = ? WHERE id = ?";

    private static final String SQL_BUSCAR =
            "SELECT * FROM estudiantes WHERE nombres LIKE ? OR apellidos LIKE ? OR " +
                    "cedula LIKE ? OR email LIKE ? OR carrera LIKE ?";

    private static final String SQL_EXISTE_CEDULA =
            "SELECT COUNT(*) FROM estudiantes WHERE cedula = ?";

    private static final String SQL_EXISTE_CEDULA_EXCLUYENDO =
            "SELECT COUNT(*) FROM estudiantes WHERE cedula = ? AND id != ?";

    // ==============================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE CONSULTA
    // ==============================================================

    @Override
    public List<Estudiante> obtenerTodos() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();

        // try-with-resources asegura el cierre automático de recursos
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_TODOS)) {

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener todos los estudiantes: " + e.getMessage());
            throw e; // Relanzar para manejo superior
        }

        return estudiantes;
    }

    @Override
    public Estudiante obtenerPorId(Long id) throws SQLException {
        Estudiante estudiante = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_POR_ID)) {

            pstmt.setLong(1, id); // Asignar parámetro al primer ?
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                estudiante = mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estudiante por ID: " + e.getMessage());
            throw e;
        }

        return estudiante;
    }

    @Override
    public Estudiante obtenerPorCedula(String cedula) throws SQLException {
        Estudiante estudiante = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_POR_CEDULA)) {

            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                estudiante = mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estudiante por cédula: " + e.getMessage());
            throw e;
        }

        return estudiante;
    }

    // ==============================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE MANIPULACIÓN
    // ==============================================================

    @Override
    public Estudiante crear(Estudiante estudiante) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {

            // Asignar parámetros al PreparedStatement
            pstmt.setString(1, estudiante.getCedula());
            pstmt.setString(2, estudiante.getNombres());
            pstmt.setString(3, estudiante.getApellidos());
            pstmt.setString(4, estudiante.getEmail());
            pstmt.setString(5, estudiante.getTelefono());
            pstmt.setString(6, estudiante.getCarrera());
            pstmt.setInt(7, estudiante.getNivel());

            // Ejecutar INSERT y obtener ResultSet con valores retornados
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // PostgreSQL retorna los valores especificados en RETURNING
                estudiante.setId(rs.getLong("id"));
                estudiante.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                estudiante.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                System.out.println("✅ Estudiante creado con ID: " + estudiante.getId());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear estudiante: " + e.getMessage());
            throw e;
        }

        return estudiante;
    }

    @Override
    public boolean actualizar(Estudiante estudiante) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            // Asignar todos los parámetros
            pstmt.setString(1, estudiante.getCedula());
            pstmt.setString(2, estudiante.getNombres());
            pstmt.setString(3, estudiante.getApellidos());
            pstmt.setString(4, estudiante.getEmail());
            pstmt.setString(5, estudiante.getTelefono());
            pstmt.setString(6, estudiante.getCarrera());
            pstmt.setInt(7, estudiante.getNivel());
            pstmt.setBoolean(8, estudiante.getActivo());
            pstmt.setLong(9, estudiante.getId());

            int filasActualizadas = pstmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("✅ Estudiante actualizado: ID " + estudiante.getId());
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estudiante: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean eliminar(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setLong(1, id);
            int filasEliminadas = pstmt.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("✅ Estudiante eliminado: ID " + id);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar estudiante: " + e.getMessage());
            throw e;
        }
    }

    // ==============================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE ESTADO
    // ==============================================================

    @Override
    public boolean activar(Long id) throws SQLException {
        return cambiarEstado(id, true);
    }

    @Override
    public boolean desactivar(Long id) throws SQLException {
        return cambiarEstado(id, false);
    }

    /**
     * Método privado para cambiar estado (activo/inactivo)
     * @param id ID del estudiante
     * @param activo Nuevo estado
     * @return true si se cambió exitosamente
     */
    private boolean cambiarEstado(Long id, boolean activo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CAMBIAR_ESTADO)) {

            pstmt.setBoolean(1, activo);
            pstmt.setLong(2, id);

            int filasActualizadas = pstmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("✅ Estado cambiado a " + (activo ? "ACTIVO" : "INACTIVO") + ": ID " + id);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al cambiar estado del estudiante: " + e.getMessage());
            throw e;
        }
    }

    // ==============================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE BÚSQUEDA Y VERIFICACIÓN
    // ==============================================================

    @Override
    public List<Estudiante> buscar(String termino) throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_BUSCAR)) {

            // Búsqueda con wildcards para búsqueda parcial
            String terminoBusqueda = "%" + termino + "%";
            pstmt.setString(1, terminoBusqueda); // nombres
            pstmt.setString(2, terminoBusqueda); // apellidos
            pstmt.setString(3, terminoBusqueda); // cedula
            pstmt.setString(4, terminoBusqueda); // email
            pstmt.setString(5, terminoBusqueda); // carrera

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar estudiantes: " + e.getMessage());
            throw e;
        }

        return estudiantes;
    }

    @Override
    public boolean existePorCedula(String cedula) throws SQLException {
        return verificarExistenciaCedula(cedula, null);
    }

    @Override
    public boolean existePorCedulaExcluyendo(String cedula, Long idExcluir) throws SQLException {
        return verificarExistenciaCedula(cedula, idExcluir);
    }

    /**
     * Método privado para verificar existencia de cédula
     * @param cedula Cédula a verificar
     * @param idExcluir ID a excluir (null para no excluir)
     * @return true si existe la cédula
     */
    private boolean verificarExistenciaCedula(String cedula, Long idExcluir) throws SQLException {
        String sql = (idExcluir == null) ? SQL_EXISTE_CEDULA : SQL_EXISTE_CEDULA_EXCLUYENDO;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            if (idExcluir != null) {
                pstmt.setLong(2, idExcluir);
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // COUNT(*) > 0
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia por cédula: " + e.getMessage());
            throw e;
        }
    }

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================

    /**
     * MAPEA UN RESULTSET A UN OBJETO ESTUDIANTE
     * Convierte filas de base de datos a objetos Java
     * @param rs ResultSet con los datos de la fila actual
     * @return Objeto Estudiante mapeado
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(rs.getLong("id"));
        estudiante.setCedula(rs.getString("cedula"));
        estudiante.setNombres(rs.getString("nombres"));
        estudiante.setApellidos(rs.getString("apellidos"));
        estudiante.setEmail(rs.getString("email"));
        estudiante.setTelefono(rs.getString("telefono"));
        estudiante.setCarrera(rs.getString("carrera"));
        estudiante.setNivel(rs.getInt("nivel"));
        estudiante.setActivo(rs.getBoolean("activo"));
        estudiante.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        estudiante.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return estudiante;
    }

    /**
     * MÉTODO DE PRUEBA PARA VERIFICAR CONEXIÓN
     * Útil para debugging y pruebas iniciales
     */
    public void testConexion() {
        try {
            DatabaseConnection.testConnection();
            System.out.println("✅ DAO funcionando correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error en el DAO: " + e.getMessage());
        }
    }
}