package ms_vetcare_bff.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @GetMapping("/me")
    public Map<String, Object> getAuthenticatedUser(
            JwtAuthenticationToken authentication
    ) {

        Jwt jwt = authentication.getToken();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put(
            "authenticated",
            authentication.isAuthenticated()
        );

        response.put(
            "principal",
            authentication.getName()
        );

        response.put(
            "oid",
            jwt.getClaimAsString("oid")
        );

        response.put(
            "tid",
            jwt.getClaimAsString("tid")
        );

        response.put(
            "name",
            jwt.getClaimAsString("name")
        );

        response.put(
            "preferred_username",
            jwt.getClaimAsString("preferred_username")
        );

        response.put(
            "aud",
            jwt.getAudience()
        );

        response.put(
            "iss",
            jwt.getIssuer()
        );

        response.put(
            "scp",
            jwt.getClaimAsString("scp")
        );

        response.put(
            "roles",
            jwt.getClaimAsStringList("roles")
        );

        response.put(
            "expiresAt",
            jwt.getExpiresAt()
        );

        response.put(
            "authorities",
            authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );

        return response;
    }
}