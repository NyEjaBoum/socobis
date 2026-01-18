package servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter(
        urlPatterns = {"/*"},
        initParams = {
                @WebInitParam(name = "allowedOrigins", value = "http://localhost:5173,http://localhost:8080"),
                @WebInitParam(name = "allowedMethods", value = "GET,POST,PUT,DELETE,OPTIONS,HEAD"),
                @WebInitParam(name = "allowedHeaders", value = "Content-Type,Authorization,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"),
                @WebInitParam(name = "allowCredentials", value = "true"),
                @WebInitParam(name = "maxAge", value = "3600")
        }
)
public class CorsFilter implements Filter {

    private Set<String> allowedOrigins = new HashSet<>();
    private String allowedMethods;
    private String allowedHeaders;
    private String allowCredentials;
    private String maxAge;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String origins = filterConfig.getInitParameter("allowedOrigins");
        if (origins != null && !origins.isEmpty()) {
            allowedOrigins.addAll(Arrays.asList(origins.split(",")));
        }

        allowedMethods = filterConfig.getInitParameter("allowedMethods");
        allowedHeaders = filterConfig.getInitParameter("allowedHeaders");
        allowCredentials = filterConfig.getInitParameter("allowCredentials");
        maxAge = filterConfig.getInitParameter("maxAge");

        System.out.println("CORS Filter initialized with origins: " + allowedOrigins);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String origin = request.getHeader("Origin");

        // Vérifier si l'origine est autorisée
        if (origin != null && (allowedOrigins.isEmpty() || allowedOrigins.contains(origin))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        if (allowedMethods != null) {
            response.setHeader("Access-Control-Allow-Methods", allowedMethods);
        }

        if (allowedHeaders != null) {
            response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        }

        if (allowCredentials != null) {
            response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        }

        if (maxAge != null) {
            response.setHeader("Access-Control-Max-Age", maxAge);
        }

        // Ajouter des headers pour le debug
        response.setHeader("Access-Control-Expose-Headers",
                "Authorization, Content-Disposition, Content-Length");

        // Gérer les requêtes OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}