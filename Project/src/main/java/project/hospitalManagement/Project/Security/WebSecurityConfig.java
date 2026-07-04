package project.hospitalManagement.Project.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import project.hospitalManagement.Project.Entity.Type.PermissionType.*;
import project.hospitalManagement.Project.Entity.Type.RoleType;

import java.io.IOException;

import static project.hospitalManagement.Project.Entity.Type.PermissionType.APPOINTMENT_DELETE;
import static project.hospitalManagement.Project.Entity.Type.PermissionType.USER_MANAGE;
import static project.hospitalManagement.Project.Entity.Type.RoleType.ADMIN;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/public/**", "/auth/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasAnyAuthority(APPOINTMENT_DELETE.name(), USER_MANAGE.name())

                        .requestMatchers("/admin/**").hasRole(ADMIN.name())
                        .requestMatchers("/doctors/**").hasAnyRole(RoleType.DOCTOR.name(), ADMIN.name())
                                .anyRequest().authenticated()
                )

                //OAUTH2

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2 -> oAuth2
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2 error: {}", exception.getMessage());
                            handlerExceptionResolver.resolveException(request, response, null, exception);

                        })
                        .successHandler(oAuth2SuccessHandler)
                )

                //ACCESS DENIED EXCEPTION ("ROLES AND PERMISSION")

                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler((AccessDeniedHandler) (request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);

                        }));
//                .formLogin();
        return httpSecurity.build();
    }

}