package ms_vetcare_bff.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ms_vetcare_bff.security.RestAccessDeniedHandler;
import ms_vetcare_bff.security.RestAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
JwtAuthenticationConverter jwtAuthenticationConverter,
CorsConfigurationSource corsConfigurationSource,
RestAuthenticationEntryPoint authenticationEntryPoint,
RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource
                )
            )

            .csrf(csrf ->
                csrf.disable()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exception -> exception

    .authenticationEntryPoint(
        authenticationEntryPoint
    )

    .accessDeniedHandler(
        accessDeniedHandler
    )
)

            .authorizeHttpRequests(auth -> auth

                /*
                 * Endpoint público utilizado para
                 * comprobar el estado del BFF.
                 */
                .requestMatchers(
                    "/actuator/health"
                )
                .permitAll()


                /*
                 * Endpoint del perfil autenticado.
                 *
                 * El usuario debe poseer un Access Token
                 * válido, pero no necesita un rol
                 * específico para consultar su propio
                 * perfil.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/security/me"
                )
                .authenticated()


                /*
                 * CONSULTAS
                 *
                 * Listar consultas y obtener una
                 * consulta por ID.
                 *
                 * Scope:
                 * Consultas.Leer
                 *
                 * Roles:
                 * Admin
                 * Operador
                 * Cliente
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/consultations",
                    "/api/consultations/**"
                )
                .access(
                    scopeAndRoles(
                        "Consultas.Leer",
                        "Admin",
                        "Operador",
                        "Cliente"
                    )
                )


                /*
                 * Crear una consulta.
                 *
                 * Scope:
                 * Consultas.Escribir
                 *
                 * Roles:
                 * Operador
                 * Cliente
                 */
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/consultations"
                )
                .access(
                    scopeAndRoles(
                        "Consultas.Escribir",
                        "Operador",
                        "Cliente"
                    )
                )


                /*
                 * Modificar el estado de
                 * una consulta.
                 *
                 * PUT
                 * /api/consultations/{id}/status
                 *
                 * Scope:
                 * Consultas.Escribir
                 *
                 * Roles:
                 * Admin
                 * Operador
                 */
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/consultations/*/status"
                )
                .access(
                    scopeAndRoles(
                        "Consultas.Escribir",
                        "Admin",
                        "Operador"
                    )
                )


                /*
                 * CATÁLOGO
                 *
                 * Consultar prestaciones.
                 *
                 * Scope:
                 * Catalogo.Leer
                 *
                 * Roles:
                 * Admin
                 * Operador
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/catalog/services",
                    "/api/catalog/services/**"
                )
                .access(
                    scopeAndRoles(
    "Catalogo.Leer",
    "Admin",
    "Operador",
    "Cliente"
)
                )


                /*
                 * Crear prestaciones.
                 *
                 * Scope:
                 * Catalogo.Escribir
                 *
                 * Rol:
                 * Admin
                 */
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/catalog/services"
                )
                .access(
                    scopeAndRoles(
                        "Catalogo.Escribir",
                        "Admin"
                    )
                )


                /*
                 * Modificar prestaciones.
                 *
                 * PUT
                 * /api/catalog/services/{id}
                 *
                 * Scope:
                 * Catalogo.Escribir
                 *
                 * Rol:
                 * Admin
                 */
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/catalog/services/*"
                )
                .access(
                    scopeAndRoles(
                        "Catalogo.Escribir",
                        "Admin"
                    )
                )


                /*
                 * Cualquier otra URL bajo /api
                 * queda bloqueada mientras no exista
                 * una regla explícita.
                 */
                .requestMatchers(
                    "/api/**"
                )
                .denyAll()


                /*
                 * Cualquier otro endpoint del BFF
                 * también queda bloqueado por defecto.
                 */
                .anyRequest()
                .denyAll()
            )

            .oauth2ResourceServer(oauth2 ->
    oauth2

        .authenticationEntryPoint(
            authenticationEntryPoint
        )

        .accessDeniedHandler(
            accessDeniedHandler
        )

        .jwt(jwt ->
            jwt.jwtAuthenticationConverter(
                jwtAuthenticationConverter
            )
        )
);

        return http.build();
    }


    /*
     * Construye una regla que exige
     * simultáneamente:
     *
     * 1. El scope solicitado.
     * 2. Uno de los roles permitidos.
     *
     * Ejemplo:
     *
     * SCOPE_Catalogo.Escribir
     * +
     * ROLE_Admin
     */
    private AuthorizationManager<RequestAuthorizationContext>
        scopeAndRoles(
            String scope,
            String... roles
        ) {

        return AuthorizationManagers.allOf(

            AuthorityAuthorizationManager
                .hasAuthority(
                    "SCOPE_" + scope
                ),

            AuthorityAuthorizationManager
                .hasAnyRole(
                    roles
                )
        );
    }


    @Bean
    public CorsConfigurationSource
        corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setExposedHeaders(
            List.of(
                "WWW-Authenticate"
            )
        );

        configuration.setAllowCredentials(
            false
        );

        configuration.setMaxAge(
            3600L
        );

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }


    @Bean
    public JwtAuthenticationConverter
        jwtAuthenticationConverter() {

        /*
         * scp
         *
         * Consultas.Leer
         *
         * ↓
         *
         * SCOPE_Consultas.Leer
         */
        JwtGrantedAuthoritiesConverter
            scopesConverter =
                new JwtGrantedAuthoritiesConverter();

        scopesConverter
            .setAuthoritiesClaimName(
                "scp"
            );

        scopesConverter
            .setAuthorityPrefix(
                "SCOPE_"
            );


        /*
         * roles
         *
         * Admin
         *
         * ↓
         *
         * ROLE_Admin
         */
        JwtGrantedAuthoritiesConverter
            rolesConverter =
                new JwtGrantedAuthoritiesConverter();

        rolesConverter
            .setAuthoritiesClaimName(
                "roles"
            );

        rolesConverter
            .setAuthorityPrefix(
                "ROLE_"
            );


        /*
         * Conservamos simultáneamente:
         *
         * SCOPE_...
         * ROLE_...
         */
        DelegatingJwtGrantedAuthoritiesConverter
            authoritiesConverter =
                new DelegatingJwtGrantedAuthoritiesConverter(
                    scopesConverter,
                    rolesConverter
                );


        JwtAuthenticationConverter
            authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter
            .setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
            );

        return authenticationConverter;
    }
}