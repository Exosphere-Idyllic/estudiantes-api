package ec.edu.istq.filter;

import ec.edu.istq.metrics.MetricsCollector;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * FILTRO JAX-RS PARA CAPTURAR MÉTRICAS DE LA API
 * Intercepta todas las peticiones y respuestas para recolectar estadísticas
 */
@Provider
public class MetricsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String START_TIME_PROPERTY = "startTime";

    /**
     * Se ejecuta ANTES de procesar la petición
     * Registra el tiempo de inicio
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Guardar timestamp de inicio en el contexto
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
    }

    /**
     * Se ejecuta DESPUÉS de procesar la petición
     * Calcula el tiempo de respuesta y registra las métricas
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Obtener tiempo de inicio
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        if (startTime == null) {
            return;
        }

        // Calcular tiempo de respuesta
        long responseTime = System.currentTimeMillis() - startTime;

        // Obtener información de la petición
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int statusCode = responseContext.getStatus();

        // Registrar métrica
        MetricsCollector.recordRequest(method, path, statusCode, responseTime);

        // Log en consola
        System.out.printf("[METRICS] %s %s -> %d (%dms)%n",
                method, path, statusCode, responseTime);
    }
}