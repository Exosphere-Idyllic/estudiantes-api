package ec.edu.istq.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * COLECTOR DE MÉTRICAS DE LA API
 * Almacena estadísticas de todas las peticiones HTTP
 * Thread-safe usando estructuras concurrentes
 */
public class MetricsCollector {

    // Mapa de métricas por endpoint
    private static final Map<String, EndpointMetrics> endpointMetrics = new ConcurrentHashMap<>();

    // Métricas globales
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger successRequests = new AtomicInteger(0);
    private static final AtomicInteger errorRequests = new AtomicInteger(0);
    private static final AtomicLong totalResponseTime = new AtomicLong(0);

    /**
     * Registra una petición HTTP
     */
    public static void recordRequest(String method, String path, int statusCode, long responseTime) {
        // Normalizar el path (quitar IDs específicos)
        String normalizedPath = normalizePath(path);
        String endpointKey = method + " " + normalizedPath;

        // Obtener o crear métricas del endpoint
        EndpointMetrics metrics = endpointMetrics.computeIfAbsent(
                endpointKey,
                k -> new EndpointMetrics(method, normalizedPath)
        );

        // Actualizar métricas del endpoint
        metrics.recordRequest(statusCode, responseTime);

        // Actualizar métricas globales
        totalRequests.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);

        if (statusCode >= 200 && statusCode < 300) {
            successRequests.incrementAndGet();
        } else if (statusCode >= 400) {
            errorRequests.incrementAndGet();
        }
    }

    /**
     * Normaliza el path removiendo IDs y valores específicos
     * Ejemplos:
     *   estudiantes/123 -> estudiantes/{id}
     *   estudiantes/cedula/1234567890 -> estudiantes/cedula/{cedula}
     */
    private static String normalizePath(String path) {
        // Remover el prefijo "api/" si existe
        path = path.replaceFirst("^api/", "");

        // Patrones a normalizar
        path = path.replaceAll("/\\d+$", "/{id}");  // IDs al final
        path = path.replaceAll("/cedula/\\d+", "/cedula/{cedula}");  // Cédulas
        path = path.replaceAll("/\\d+/activar", "/{id}/activar");
        path = path.replaceAll("/\\d+/desactivar", "/{id}/desactivar");

        return path;
    }

    /**
     * Obtiene todas las métricas de endpoints
     */
    public static Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Métricas globales
        int total = totalRequests.get();
        metrics.put("totalRequests", total);
        metrics.put("successRequests", successRequests.get());
        metrics.put("errorRequests", errorRequests.get());
        metrics.put("averageResponseTime", total > 0 ? totalResponseTime.get() / total : 0);
        metrics.put("successRate", total > 0 ? (successRequests.get() * 100.0 / total) : 100.0);

        // Métricas por endpoint
        List<Map<String, Object>> endpoints = new ArrayList<>();
        endpointMetrics.forEach((key, endpointMetric) -> {
            endpoints.add(endpointMetric.toMap());
        });

        // Ordenar por número de requests (mayor a menor)
        endpoints.sort((a, b) ->
                Integer.compare((Integer) b.get("count"), (Integer) a.get("count"))
        );

        metrics.put("endpoints", endpoints);
        metrics.put("timestamp", System.currentTimeMillis());

        return metrics;
    }

    /**
     * Resetea todas las métricas
     */
    public static void reset() {
        endpointMetrics.clear();
        totalRequests.set(0);
        successRequests.set(0);
        errorRequests.set(0);
        totalResponseTime.set(0);
    }

    /**
     * Clase interna para almacenar métricas de un endpoint específico
     */
    private static class EndpointMetrics {
        private final String method;
        private final String path;
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private final List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        private volatile int lastStatusCode = 0;
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicInteger errorCount = new AtomicInteger(0);

        public EndpointMetrics(String method, String path) {
            this.method = method;
            this.path = path;
        }

        public void recordRequest(int statusCode, long responseTime) {
            count.incrementAndGet();
            totalTime.addAndGet(responseTime);
            responseTimes.add(responseTime);
            lastStatusCode = statusCode;

            if (statusCode >= 200 && statusCode < 300) {
                successCount.incrementAndGet();
            } else if (statusCode >= 400) {
                errorCount.incrementAndGet();
            }

            // Mantener solo las últimas 100 mediciones
            if (responseTimes.size() > 100) {
                responseTimes.remove(0);
            }
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            int totalCount = count.get();

            map.put("method", method);
            map.put("path", path);
            map.put("count", totalCount);
            map.put("averageTime", totalCount > 0 ? totalTime.get() / totalCount : 0);
            map.put("lastStatus", lastStatusCode);
            map.put("successCount", successCount.get());
            map.put("errorCount", errorCount.get());
            map.put("successRate", totalCount > 0 ? (successCount.get() * 100.0 / totalCount) : 100.0);

            // Calcular min, max, mediana de tiempos de respuesta
            if (!responseTimes.isEmpty()) {
                synchronized (responseTimes) {
                    List<Long> times = new ArrayList<>(responseTimes);
                    Collections.sort(times);
                    map.put("minTime", times.get(0));
                    map.put("maxTime", times.get(times.size() - 1));
                    map.put("medianTime", times.get(times.size() / 2));
                }
            } else {
                map.put("minTime", 0);
                map.put("maxTime", 0);
                map.put("medianTime", 0);
            }

            return map;
        }
    }
}