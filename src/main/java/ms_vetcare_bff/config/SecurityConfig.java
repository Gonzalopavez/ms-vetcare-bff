package ms_vetcare_bff.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

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

    /*
     * Orígenes permitidos por CORS.
     *
     * LOCAL:
     * http://localhost:4200
     *
     * AWS:
     * https://production.d3ry6yi9x6895b.amplifyapp.com
     *
     * También se puede reemplazar mediante:
     * VETCARE_CORS_ALLOWED_ORIGINS
     */
    @Value(
        "${vetcare.cors.allowed-origins:" +
        "http://localhost:4200," +
        "https://production.d3ry6yi9x6895b.amplifyapp.com}"
    )
    private List<String> allowedOrigins;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            CorsConfigurationSource corsConfigurationSource,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http

            /*
             * CORS.
             */
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource
                )
            )

            /*
             * API REST sin sesiones.
             */
            .csrf(csrf ->
                csrf.disable()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            /*
             * Respuestas JSON para 401 y 403.
             */
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
                 * Preflight CORS.
                 */
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()


                /*
                 * Health del BFF.
                 */
                .requestMatchers(
                    "/actuator/health"
                )
                .permitAll()


                /*
                 * ==============================
                 * PERFIL AUTENTICADO
                 * ==============================
                 *
                 * Cualquier usuario con JWT válido
                 * puede consultar su perfil.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/security/me"
                )
                .authenticated()


                /*
                 * ==============================
                 * CONSULTATIONS
                 * ==============================
                 *
                 * GET:
                 *
                 * Scope:
                 * Consultas.leer
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
                        "Consultas.leer",
                        "Admin",
                        "Operador",
                        "Cliente"
                    )
                )


                /*
                 * Crear consulta.
                 *
                 * POST /api/consultations
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
                 * Cambiar estado.
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
                 * ==============================
                 * CATALOG
                 * ==============================
                 *
                 * Consultar prestaciones.
                 *
                 * Scope:
                 * Catalogo.Leer
                 *
                 * Roles:
                 * Admin
                 * Operador
                 * Cliente
                 *
                 * Cliente necesita esta lectura
                 * para poder seleccionar una
                 * prestación al crear consulta.
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
                 * PUT /api/catalog/services/{id}
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
                 * ==============================
                 * AUDIT
                 * ==============================
                 *
                 * Audit actualmente no posee un
                 * scope Auditoria.Leer propio.
                 *
                 * El acceso se controla mediante
                 * el app role Auditor.
                 *
                 * El token sigue perteneciendo a
                 * VetCare API y es validado por
                 * Microsoft Entra ID.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/audit/events",
                    "/api/audit/events/**"
                )
                .hasRole(
                    "Auditor"
                )


                /*
                 * Cualquier otra ruta /api
                 * queda bloqueada.
                 */
                .requestMatchers(
                    "/api/**"
                )
                .denyAll()


                /*
                 * El resto también queda
                 * bloqueado por defecto.
                 */
                .anyRequest()
                .denyAll()
            )


            /*
             * Resource Server JWT.
             */
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
     * Exige simultáneamente:
     *
     * 1. Scope correcto.
     * 2. Al menos uno de los roles permitidos.
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


    /*
     * ==============================
     * CORS
     * ==============================
     */
    @Bean
    public CorsConfigurationSource
        corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();


        configuration.setAllowedOrigins(
            allowedOrigins
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


        /*
         * Los JWT se envían en Authorization,
         * no mediante cookies.
         */
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


    /*
     * ==============================
     * JWT AUTHORITIES
     * ==============================
     */
    @Bean
    public JwtAuthenticationConverter
        jwtAuthenticationConverter() {

        /*
         * Claim:
         *
         * scp
         *
         * Consultas.leer
         *
         * ↓
         *
         * SCOPE_Consultas.leer
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
         * Claim:
         *
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
         * Conservamos al mismo tiempo:
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