package ms_vetcare_bff.controller;

import ms_vetcare_bff.client.ConsultationsClient;

import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationsController {

    private final ConsultationsClient consultationsClient;

    public ConsultationsController(
            ConsultationsClient consultationsClient
    ) {

        this.consultationsClient =
            consultationsClient;
    }


    @GetMapping
    public ResponseEntity<String> getConsultations(
            @RequestParam
            MultiValueMap<String, String> params,

            JwtAuthenticationToken authentication
    ) {

        return consultationsClient
            .getConsultations(
                params,
                authentication
            );
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getConsultationById(
            @PathVariable Long id,

            JwtAuthenticationToken authentication
    ) {

        return consultationsClient
            .getConsultationById(
                id,
                authentication
            );
    }


    @PostMapping
    public ResponseEntity<String> createConsultation(
            @RequestBody String body,

            JwtAuthenticationToken authentication
    ) {

        return consultationsClient
            .createConsultation(
                body,
                authentication
            );
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,

            @RequestBody String body,

            JwtAuthenticationToken authentication
    ) {

        return consultationsClient
            .updateStatus(
                id,
                body,
                authentication
            );
    }
}