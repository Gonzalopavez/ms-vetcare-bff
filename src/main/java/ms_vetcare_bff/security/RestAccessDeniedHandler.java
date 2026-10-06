package ms_vetcare_bff.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.MediaType;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAccessDeniedHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        response.setStatus(
            HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding(
            "UTF-8"
        );

        String jsonResponse = """
            {
              "timestamp": "%s",
              "status": 403,
              "error": "Forbidden",
              "message": "El usuario está autenticado, pero no posee autorización suficiente.",
              "path": "%s"
            }
            """.formatted(
                Instant.now(),
                request.getRequestURI()
            );

        response
            .getWriter()
            .write(jsonResponse);
    }
}