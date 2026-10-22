package ms_vetcare_bff.controller;

import ms_vetcare_bff.client.AuditClient;

import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/audit/events")
public class AuditController {

    private final AuditClient auditClient;


    public AuditController(
            AuditClient auditClient
    ) {

        this.auditClient = auditClient;
    }


    @GetMapping
    public ResponseEntity<String> getAuditEvents(
            JwtAuthenticationToken authentication
    ) {

        return auditClient
                .getAuditEvents(
                        authentication
                );
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getAuditEventById(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {

        return auditClient
                .getAuditEventById(
                        id,
                        authentication
                );
    }
}