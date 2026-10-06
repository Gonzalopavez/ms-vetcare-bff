package ms_vetcare_bff.client;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;

@Service
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(
            @Qualifier("catalogRestClient")
            RestClient restClient
    ) {

        this.restClient = restClient;
    }


    public ResponseEntity<String> getServices(
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .get()
            .uri(
                "/api/catalog/services"
            )
            .header(
                HttpHeaders.AUTHORIZATION,
                bearerToken(authentication)
            )
            .retrieve()
            .toEntity(String.class);
    }


    public ResponseEntity<String> createService(
            String body,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .post()
            .uri(
                "/api/catalog/services"
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


    public ResponseEntity<String> updateService(
            Long id,
            String body,
            JwtAuthenticationToken authentication
    ) {

        return restClient
            .put()
            .uri(
                "/api/catalog/services/{id}",
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