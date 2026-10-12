package cl.duoc.santiagosacro.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val usuarioDetailsService: UsuarioDetailsService
) {

    /** BCrypt: nunca se guarda la clave en texto plano. */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val proveedor = DaoAuthenticationProvider()
        proveedor.setUserDetailsService(usuarioDetailsService)
        proveedor.setPasswordEncoder(passwordEncoder())
        return proveedor
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuracion = CorsConfiguration()
        configuracion.allowedOriginPatterns = listOf("*")
        configuracion.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuracion.allowedHeaders = listOf("*")
        val fuente = UrlBasedCorsConfigurationSource()
        fuente.registerCorsConfiguration("/**", configuracion)
        return fuente
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .headers { cabeceras -> cabeceras.frameOptions { it.disable() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { peticiones ->
                peticiones
                    // Publico
                    .requestMatchers("/api/auth/registro", "/api/auth/login").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/recintos/**",
                        "/api/eventos/**",
                        "/api/avisos/**",
                        "/api/catalogos/**"
                    ).permitAll()
                    // Solo encargados
                    .requestMatchers("/api/encargado/**").hasRole("ENCARGADO")
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/recintos/*/eventos",
                        "/api/recintos/*/avisos",
                        "/api/recintos/*/bloques"
                    ).hasRole("ENCARGADO")
                    .requestMatchers(HttpMethod.PUT, "/api/eventos/*").hasRole("ENCARGADO")
                    .requestMatchers(HttpMethod.DELETE, "/api/eventos/*").hasRole("ENCARGADO")
                    .requestMatchers(HttpMethod.PATCH, "/api/recintos/*/estado").hasRole("ENCARGADO")
                    .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*").hasRole("ENCARGADO")
                    // Cualquier usuario autenticado
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
