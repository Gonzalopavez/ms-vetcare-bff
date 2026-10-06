package ms_vetcare_bff.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.MediaType;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        response.setStatus(
            HttpServletResponse.SC_UNAUTHORIZED
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
              "status": 401,
              "error": "Unauthorized",
              "message": "La solicitud no posee una credencial válida.",
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