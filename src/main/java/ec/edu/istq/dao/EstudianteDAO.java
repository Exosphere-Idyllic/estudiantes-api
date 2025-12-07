package ec.edu.istq.dao;

import ec.edu.istq.model.Estudiante;
import java.sql.SQLException;
import java.util.List;

/**
 * INTERFAZ DAO (Data Access Object) PARA ESTUDIANTE
 * Define el contrato para las operaciones CRUD con la base de datos.
 * Patrón DAO: Separa la lógica de negocio del acceso a datos.
 */
public interface EstudianteDAO {

    // ==============================================================
    // OPERACIONES DE CONSULTA (READ)
    // ==============================================================

    /**
     * Obtiene todos los estudiantes registrados
     * @return Lista completa de estudiantes
     * @throws SQLException Error en la base de datos
     */
    List<Estudiante> obtenerTodos() throws SQLException;

    /**
     * Obtiene un estudiante por su ID único
     * @param id Identificador del estudiante
     * @return Estudiante encontrado o null si no existe
     * @throws SQLException Error en la base de datos
     */
    Estudiante obtenerPorId(Long id) throws SQLException;

    /**
     * Obtiene un estudiante por su número de cédula
     * @param cedula Cédula del estudiante
     * @return Estudiante encontrado o null si no existe
     * @throws SQLException Error en la base de datos
     */
    Estudiante obtenerPorCedula(String cedula) throws SQLException;

    // ==============================================================
    // OPERACIONES DE MANIPULACIÓN (CREATE, UPDATE, DELETE)
    // ==============================================================

    /**
     * Crea un nuevo estudiante en la base de datos
     * @param estudiante Objeto Estudiante a crear (sin ID)
     * @return Estudiante creado con ID generado
     * @throws SQLException Error en la base de datos
     */
    Estudiante crear(Estudiante estudiante) throws SQLException;

    /**
     * Actualiza los datos de un estudiante existente
     * @param estudiante Estudiante con datos actualizados
     * @return true si se actualizó correctamente
     * @throws SQLException Error en la base de datos
     */
    boolean actualizar(Estudiante estudiante) throws SQLException;

    /**
     * Elimina permanentemente un estudiante
     * @param id ID del estudiante a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Error en la base de datos
     */
    boolean eliminar(Long id) throws SQLException;

    // ==============================================================
    // OPERACIONES DE ESTADO (ACTIVAR/DESACTIVAR)
    // ==============================================================

    /**
     * Activa un estudiante (estado = true)
     * @param id ID del estudiante a activar
     * @return true si se activó correctamente
     * @throws SQLException Error en la base de datos
     */
    boolean activar(Long id) throws SQLException;

    /**
     * Desactiva un estudiante (estado = false)
     * @param id ID del estudiante a desactivar
     * @return true si se desactivó correctamente
     * @throws SQLException Error en la base de datos
     */
    boolean desactivar(Long id) throws SQLException;

    // ==============================================================
    // OPERACIONES DE BÚSQUEDA Y VERIFICACIÓN
    // ==============================================================

    /**
     * Busca estudiantes por término en múltiples campos
     * @param termino Texto a buscar en nombres, apellidos, cédula, email, carrera
     * @return Lista de estudiantes que coinciden con la búsqueda
     * @throws SQLException Error en la base de datos
     */
    List<Estudiante> buscar(String termino) throws SQLException;

    /**
     * Verifica si existe un estudiante con cierta cédula
     * @param cedula Cédula a verificar
     * @return true si ya existe un estudiante con esa cédula
     * @throws SQLException Error en la base de datos
     */
    boolean existePorCedula(String cedula) throws SQLException;

    /**
     * Verifica si existe un estudiante con cierta cédula, excluyendo un ID específico
     * (Útil para actualizaciones, evitar duplicados al modificar)
     * @param cedula Cédula a verificar
     * @param idExcluir ID a excluir de la verificación
     * @return true si ya existe otro estudiante con esa cédula
     * @throws SQLException Error en la base de datos
     */
    boolean existePorCedulaExcluyendo(String cedula, Long idExcluir) throws SQLException;
}