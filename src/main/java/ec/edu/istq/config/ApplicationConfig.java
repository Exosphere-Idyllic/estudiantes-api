package ec.edu.istq.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuración principal de JAX-RS
 * Define la ruta base del API REST
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    // JAX-RS escaneará automáticamente las clases anotadas con @Path
}