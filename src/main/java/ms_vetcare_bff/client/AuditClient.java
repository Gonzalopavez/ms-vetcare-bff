package ms_vetcare_bff.client;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;


@Service
public class AuditClient {

    private final RestClient restClient;


    public AuditClient(
            @Qualifier("auditRestClient")
            RestClient restClient
    ) {

        this.restClient = restClient;
    }


    /**
     * Lista todos los eventos de auditoría.
     *
     * BFF:
     * GET /api/audit/events
     *
     * Audit:
     * GET /api/audit/events
     */
    public ResponseEntity<String> getAuditEvents(
            JwtAuthenticationToken authentication
    ) {

        return restClient
                .get()
                .uri(
                        "/api/audit/events"
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(authentication)
                )
                .retrieve()
                .toEntity(String.class);
    }


    /**
     * Obtiene un evento de auditoría por ID.
     *
     * BFF:
     * GET /api/audit/events/{id}
     *
     * Audit:
     * GET /api/audit/events/{id}
     */
    public ResponseEntity<String> getAuditEventById(
            Long id,
            JwtAuthenticationToken authentication
    ) {

        return restClient
                .get()
                .uri(
                        "/api/audit/events/{id}",
                        id
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(authentication)
                )
                .retrieve()
                .toEntity(String.class);
    }


    /**
     * Reenvía el mismo JWT recibido por el BFF
     * hacia ms-vetcare-audit.
     */
    private String bearerToken(
            JwtAuthenticationToken authentication
    ) {

        return "Bearer "
                + authentication
                .getToken()
                .getTokenValue();
    }
}