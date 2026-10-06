package ms_vetcare_bff.client;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Service;

import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestClient;

@Service
public class ConsultationsClient {

    private final RestClient restClient;

    public ConsultationsClient(
            @Qualifier("consultationsRestClient")
            RestClient restClient
    ) {

        this.restClient = restClient;
    }


    public ResponseEntity<String> getConsultations(
            MultiValueMap<String, String> params,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .get()
            .uri(uriBuilder ->
                uriBuilder
                    .path("/api/consultations")
                    .queryParams(params)
                    .build()
            )
            .header(
                HttpHeaders.AUTHORIZATION,
                bearerToken(authentication)
            )
            .retrieve()
            .toEntity(String.class);
    }


    public ResponseEntity<String> getConsultationById(
            Long id,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .get()
            .uri(
                "/api/consultations/{id}",
                id
            )
            .header(
                HttpHeaders.AUTHORIZATION,
                bearerToken(authentication)
            )
            .retrieve()
            .toEntity(String.class);
    }


    public ResponseEntity<String> createConsultation(
            String body,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .post()
            .uri(
                "/api/consultations"
            )
            .header(
                HttpHeaders.AUTHORIZATION,
                bearerToken(authentication)
            )
            .header(
                HttpHeaders.CONTENT_TYPE,
                "application/json"
            )
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }


    public ResponseEntity<String> updateStatus(
            Long id,
            String body,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .put()
            .uri(
                "/api/consultations/{id}/status",
                id
            )
            .header(
                HttpHeaders.AUTHORIZATION,
                bearerToken(authentication)
            )
            .header(
                HttpHeaders.CONTENT_TYPE,
                "application/json"
            )
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }


    private String bearerToken(
            JwtAuthenticationToken authentication
    ) {

        return "Bearer " +
            authentication
                .getToken()
                .getTokenValue();
    }
}