package ms_vetcare_bff.controller;
import ms_vetcare_bff.client.CatalogClient;

import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/services")
public class CatalogController {

    private final CatalogClient catalogClient;

    public CatalogController(
            CatalogClient catalogClient
    ) {

        this.catalogClient =
            catalogClient;
    }


    @GetMapping
    public ResponseEntity<String> getServices(
            JwtAuthenticationToken authentication
    ) {

        return catalogClient
            .getServices(
                authentication
            );
    }


    @PostMapping
    public ResponseEntity<String> createService(
            @RequestBody String body,

            JwtAuthenticationToken authentication
    ) {

        return catalogClient
            .createService(
                body,
                authentication
            );
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateService(
            @PathVariable Long id,

            @RequestBody String body,

            JwtAuthenticationToken authentication
    ) {

        return catalogClient
            .updateService(
                id,
                body,
                authentication
            );
    }
}