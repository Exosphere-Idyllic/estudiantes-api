package ec.edu.istq.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FILTRO CORS PARA PERMITIR PETICIONES DESDE EL FRONTEND
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Permitir peticiones desde cualquier origen (para desarrollo)
        response.setHeader("Access-Control-Allow-Origin", "*");

        // Métodos HTTP permitidos
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Headers permitidos
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Tiempo de cache para preflight
        response.setHeader("Access-Control-Max-Age", "3600");

        // Si es una petición OPTIONS (preflight), responder OK
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
}