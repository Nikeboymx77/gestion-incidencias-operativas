package com.mx.baz.incidencias.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.mx.baz.incidencias.security.BotApiKeyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final BotApiKeyFilter botApiKeyFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

    	http
	        .csrf(csrf -> csrf
	                .ignoringRequestMatchers("/api/bot/**")
	        )
	
	        .authorizeHttpRequests(auth -> auth
        			    // Recursos estáticos
        			    .requestMatchers(
        			            "/css/**",
        			            "/js/**",
        			            "/images/**",
        			            "/favicon.ico"
        			    ).permitAll()

        			    // Administración
        			    .requestMatchers("/empleados/**")
        			    	.hasRole("ADMIN")
        			    	
        			    .requestMatchers("/api/empleados/**")
        			        .hasRole("ADMIN")

	        			.requestMatchers("/usuarios/**")
	        			    .hasRole("ADMIN")
	
	        			.requestMatchers("/api/usuarios/**")
	        			    .hasRole("ADMIN")
	        			    
        			    .requestMatchers("/api/bot/**")
        			        .hasRole("BOT")
        			        
    			     // Consultas de incidencias
    			        .requestMatchers(HttpMethod.GET, "/api/incidencias/**")
    			            .hasAnyRole("ADMIN", "OPERADOR")

    			        // Operaciones sobre incidencias
    			        .requestMatchers(HttpMethod.POST, "/api/incidencias/**")
    			            .hasAnyRole("ADMIN", "OPERADOR")

    			        .requestMatchers(HttpMethod.PUT, "/api/incidencias/**")
    			            .hasAnyRole("ADMIN", "OPERADOR")

        			    // Todo lo demás requiere autenticación
        			    .anyRequest()
        			        .authenticated()
        			)
	        
	        .exceptionHandling(ex -> ex
	        	    .defaultAuthenticationEntryPointFor(
	        	        (request, response, authException) -> {

	        	            response.setStatus(
	        	                HttpStatus.UNAUTHORIZED.value()
	        	            );

	        	            response.setContentType(
	        	                MediaType.APPLICATION_JSON_VALUE
	        	            );

	        	            response.setCharacterEncoding("UTF-8");

	        	            response.getWriter().write(
	        	                "{\"codigo\":\"SESION_EXPIRADA\","
	        	                + "\"mensaje\":\"Tu sesión ha expirado. Inicia sesión nuevamente.\"}"
	        	            );
	        	        },
	        	        new OrRequestMatcher(
	        	            new AntPathRequestMatcher("/api/**"),
	        	            new AntPathRequestMatcher("/dashboard/fragment/**")
	        	        )
	        	    )
	        	)

            // Por ahora usamos el login de Spring
            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .permitAll()
            )

            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            );
        
        http.addFilterBefore(
                botApiKeyFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}