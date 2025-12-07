package ec.edu.istq.resource;

import ec.edu.istq.dao.EstudianteDAO;
import ec.edu.istq.dao.EstudianteDAOImpl;
import ec.edu.istq.model.Estudiante;
import ec.edu.istq.util.CedulaValidator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RECURSO REST PARA ESTUDIANTES
 * Expone endpoints HTTP para operaciones CRUD.
 * Anotaciones JAX-RS definen rutas y métodos HTTP.
 * Path base: /api/estudiantes (definido en ApplicationConfig)
 */
@Path("/estudiantes")
@Produces(MediaType.APPLICATION_JSON)    // Todas las respuestas en JSON
@Consumes(MediaType.APPLICATION_JSON)    // Todas las peticiones en JSON
public class EstudianteResource {

    // ==============================================================
    // DEPENDENCIAS - PATRÓN DAO
    // ==============================================================

    private final EstudianteDAO estudianteDAO = new EstudianteDAOImpl();

    // ==============================================================
    // ENDPOINTS DE CONSULTA (GET)
    // ==============================================================

    /**
     * GET /api/estudiantes
     * Obtiene todos los estudiantes registrados
     * @return Response con lista de estudiantes (200) o error (500)
     */
    @GET
    public Response obtenerTodos() {
        try {
            List<Estudiante> estudiantes = estudianteDAO.obtenerTodos();
            return Response.ok(estudiantes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al obtener estudiantes: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/estudiantes/{id}
     * Obtiene un estudiante por su ID
     * @param id ID del estudiante (Path Parameter)
     * @return Response con estudiante (200), no encontrado (404) o error (500)
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Long id) {
        try {
            Estudiante estudiante = estudianteDAO.obtenerPorId(id);

            if (estudiante != null) {
                return Response.ok(estudiante).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al obtener estudiante: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/estudiantes/cedula/{cedula}
     * Obtiene un estudiante por su cédula
     * @param cedula Cédula del estudiante (Path Parameter)
     * @return Response con estudiante (200), no encontrado (404) o error (500)
     */
    @GET
    @Path("/cedula/{cedula}")
    public Response obtenerPorCedula(@PathParam("cedula") String cedula) {
        try {
            Estudiante estudiante = estudianteDAO.obtenerPorCedula(cedula);

            if (estudiante != null) {
                return Response.ok(estudiante).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al obtener estudiante: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/estudiantes/buscar?q={termino}
     * Busca estudiantes por término en múltiples campos
     * @param termino Término de búsqueda (Query Parameter)
     * @return Response con lista de estudiantes (200) o error (400/500)
     */
    @GET
    @Path("/buscar")
    public Response buscar(@QueryParam("q") String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(crearMensajeError("El término de búsqueda es requerido"))
                        .build();
            }

            List<Estudiante> estudiantes = estudianteDAO.buscar(termino);
            return Response.ok(estudiantes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al buscar estudiantes: " + e.getMessage()))
                    .build();
        }
    }

    // ==============================================================
    // ENDPOINTS DE CREACIÓN (POST)
    // ==============================================================

    /**
     * POST /api/estudiantes
     * Crea un nuevo estudiante
     * @param estudiante Datos del estudiante en cuerpo de petición (JSON)
     * @return Response con estudiante creado (201), error validación (400),
     *         conflicto (409) o error (500)
     */
    @POST
    public Response crear(Estudiante estudiante) {
        try {
            // Validar datos del estudiante
            String errorValidacion = validarEstudiante(estudiante, true);
            if (errorValidacion != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(crearMensajeError(errorValidacion))
                        .build();
            }

            // Verificar que no exista otro estudiante con la misma cédula
            if (estudianteDAO.existePorCedula(estudiante.getCedula())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(crearMensajeError("Ya existe un estudiante con esa cédula"))
                        .build();
            }

            // Crear estudiante
            Estudiante estudianteCreado = estudianteDAO.crear(estudiante);

            if (estudianteCreado.getId() != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(estudianteCreado)
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(crearMensajeError("No se pudo crear el estudiante"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al crear estudiante: " + e.getMessage()))
                    .build();
        }
    }

    // ==============================================================
    // ENDPOINTS DE ACTUALIZACIÓN (PUT)
    // ==============================================================

    /**
     * PUT /api/estudiantes/{id}
     * Actualiza un estudiante existente
     * @param id ID del estudiante a actualizar (Path Parameter)
     * @param estudiante Datos actualizados en cuerpo de petición (JSON)
     * @return Response con estudiante actualizado (200), no encontrado (404),
     *         error validación (400), conflicto (409) o error (500)
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Long id, Estudiante estudiante) {
        try {
            // Verificar que el estudiante existe
            Estudiante existente = estudianteDAO.obtenerPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }

            // Asignar ID del path al objeto
            estudiante.setId(id);

            // Validar datos del estudiante
            String errorValidacion = validarEstudiante(estudiante, false);
            if (errorValidacion != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(crearMensajeError(errorValidacion))
                        .build();
            }

            // Verificar que no exista otro estudiante con la misma cédula
            if (estudianteDAO.existePorCedulaExcluyendo(estudiante.getCedula(), id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(crearMensajeError("Ya existe otro estudiante con esa cédula"))
                        .build();
            }

            // Actualizar estudiante
            boolean actualizado = estudianteDAO.actualizar(estudiante);

            if (actualizado) {
                return Response.ok(estudiante).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(crearMensajeError("No se pudo actualizar el estudiante"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al actualizar estudiante: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /api/estudiantes/{id}/activar
     * Activa un estudiante (cambia estado activo = true)
     * @param id ID del estudiante a activar
     * @return Response con mensaje de éxito (200), no encontrado (404) o error (500)
     */
    @PUT
    @Path("/{id}/activar")
    public Response activar(@PathParam("id") Long id) {
        try {
            boolean activado = estudianteDAO.activar(id);
            if (activado) {
                return Response.ok(crearMensajeExito("Estudiante activado correctamente")).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al activar estudiante: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /api/estudiantes/{id}/desactivar
     * Desactiva un estudiante (cambia estado activo = false)
     * @param id ID del estudiante a desactivar
     * @return Response con mensaje de éxito (200), no encontrado (404) o error (500)
     */
    @PUT
    @Path("/{id}/desactivar")
    public Response desactivar(@PathParam("id") Long id) {
        try {
            boolean desactivado = estudianteDAO.desactivar(id);
            if (desactivado) {
                return Response.ok(crearMensajeExito("Estudiante desactivado correctamente")).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al desactivar estudiante: " + e.getMessage()))
                    .build();
        }
    }

    // ==============================================================
    // ENDPOINTS DE ELIMINACIÓN (DELETE)
    // ==============================================================

    /**
     * DELETE /api/estudiantes/{id}
     * Elimina permanentemente un estudiante
     * @param id ID del estudiante a eliminar
     * @return Response con mensaje de éxito (200), no encontrado (404) o error (500)
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        try {
            // Verificar que el estudiante existe
            Estudiante existente = estudianteDAO.obtenerPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(crearMensajeError("Estudiante no encontrado"))
                        .build();
            }

            boolean eliminado = estudianteDAO.eliminar(id);

            if (eliminado) {
                return Response.ok(crearMensajeExito("Estudiante eliminado correctamente")).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(crearMensajeError("No se pudo eliminar el estudiante"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(crearMensajeError("Error al eliminar estudiante: " + e.getMessage()))
                    .build();
        }
    }

    // ==============================================================
    // MÉTODOS AUXILIARES DE VALIDACIÓN
    // ==============================================================

    /**
     * Valida los datos de un estudiante
     * @param estudiante Objeto a validar
     * @param esNuevo true si es creación, false si es actualización
     * @return Mensaje de error o null si es válido
     */
    private String validarEstudiante(Estudiante estudiante, boolean esNuevo) {
        if (estudiante == null) {
            return "Los datos del estudiante son requeridos";
        }

        // Validar cédula (requerida y formato ecuatoriano)
        if (estudiante.getCedula() == null || estudiante.getCedula().trim().isEmpty()) {
            return "La cédula es requerida";
        }
        if (!CedulaValidator.esValida(estudiante.getCedula())) {
            return "La cédula no es válida (formato ecuatoriano)";
        }

        // Validar nombres (requeridos, mínimo 2 caracteres)
        if (estudiante.getNombres() == null || estudiante.getNombres().trim().isEmpty()) {
            return "Los nombres son requeridos";
        }
        if (estudiante.getNombres().trim().length() < 2) {
            return "Los nombres deben tener al menos 2 caracteres";
        }

        // Validar apellidos (requeridos, mínimo 2 caracteres)
        if (estudiante.getApellidos() == null || estudiante.getApellidos().trim().isEmpty()) {
            return "Los apellidos son requeridos";
        }
        if (estudiante.getApellidos().trim().length() < 2) {
            return "Los apellidos deben tener al menos 2 caracteres";
        }

        // Validar email (requerido y formato válido)
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            return "El email es requerido";
        }
        if (!estudiante.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "El formato del email es inválido";
        }

        // Validar carrera (requerida)
        if (estudiante.getCarrera() == null || estudiante.getCarrera().trim().isEmpty()) {
            return "La carrera es requerida";
        }

        // Validar nivel (requerido, entre 1 y 10)
        if (estudiante.getNivel() == null) {
            return "El nivel es requerido";
        }
        if (estudiante.getNivel() < 1 || estudiante.getNivel() > 10) {
            return "El nivel debe estar entre 1 y 10";
        }

        return null; // Sin errores
    }

    // ==============================================================
    // MÉTODOS AUXILIARES DE RESPUESTA
    // ==============================================================

    /**
     * Crea un objeto JSON con mensaje de error
     */
    private Map<String, Object> crearMensajeError(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        respuesta.put("mensaje", mensaje);
        respuesta.put("timestamp", System.currentTimeMillis());
        return respuesta;
    }

    /**
     * Crea un objeto JSON con mensaje de éxito
     */
    private Map<String, Object> crearMensajeExito(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", false);
        respuesta.put("mensaje", mensaje);
        respuesta.put("timestamp", System.currentTimeMillis());
        return respuesta;
    }
}