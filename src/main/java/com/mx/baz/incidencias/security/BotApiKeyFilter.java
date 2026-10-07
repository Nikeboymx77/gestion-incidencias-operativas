package com.mx.baz.incidencias.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class BotApiKeyFilter
        extends OncePerRequestFilter {

    private static final String API_KEY_HEADER =
            "X-SGIO-API-KEY";

    @Value("${sgio.bot.api-key}")
    private String apiKey;
    
    
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path =
                request.getRequestURI();

        return !path.startsWith(
                "/api/bot/"
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String apiKeyRecibida =
                request.getHeader(API_KEY_HEADER);

        if (apiKeyRecibida == null
                || !apiKey.equals(apiKeyRecibida)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"mensaje\":\"API Key del bot inválida\"}"
            );

            return;
        }
        
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "BOT_SGIO",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_BOT"
                                )
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(
                request,
                response
        );
    }
}
