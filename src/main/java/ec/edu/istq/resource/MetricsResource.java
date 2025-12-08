package ec.edu.istq.resource;

import ec.edu.istq.metrics.MetricsCollector;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * ENDPOINT REST PARA CONSULTAR MÉTRICAS DE LA API
 * Expone estadísticas de uso de todos los endpoints
 */
@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    /**
     * GET /api/metrics
     * Obtiene todas las métricas recolectadas
     * @return JSON con métricas globales y por endpoint
     */
    @GET
    public Response getMetrics() {
        try {
            Map<String, Object> metrics = MetricsCollector.getAllMetrics();
            return Response.ok(metrics).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("mensaje", "Error al obtener métricas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    /**
     * DELETE /api/metrics
     * Resetea todas las métricas a cero
     * @return Mensaje de confirmación
     */
    @DELETE
    public Response resetMetrics() {
        try {
            MetricsCollector.reset();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Métricas reseteadas correctamente");
            response.put("timestamp", System.currentTimeMillis());

            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("mensaje", "Error al resetear métricas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    /**
     * GET /api/metrics/health
     * Health check del sistema de métricas
     * @return Estado del sistema
     */
    @GET
    @Path("/health")
    public Response healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Metrics");
        health.put("timestamp", System.currentTimeMillis());
        return Response.ok(health).build();
    }
}