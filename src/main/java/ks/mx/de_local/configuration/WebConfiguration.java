package ks.mx.de_local.configuration;

import jakarta.servlet.http.HttpServletResponse;
import ks.mx.de_local.configuration.entrypoints.JWTEntrypoint;
import ks.mx.de_local.configuration.filters.JWTFilter;
import ks.mx.de_local.configuration.handler.CustomLoginHandler;
import ks.mx.de_local.configuration.handler.CustomOauth2SuccessHandler;
import ks.mx.de_local.entity.Provider.UserProvider;
import ks.mx.de_local.entity.User;
import ks.mx.de_local.service.UserService;
import lombok.Generated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.IOException;

@Configuration
public class WebConfiguration {
    private final JWTFilter jwtFilter;
    private final JWTEntrypoint jwtEntrypoint;
    private final UserService userService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final CustomLoginHandler customLoginHandler;
    private final SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
    private final String[] RESTRICTED_AREA = {"/f", "/info"};


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(req -> req
                .requestMatchers(RESTRICTED_AREA).authenticated()
                .anyRequest().permitAll())
            .formLogin(f -> f
                .loginPage("/authentication").permitAll()
                .successHandler(customLoginHandler))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntrypoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(o -> o
                .loginPage("/authentication")
                .successHandler(customOauth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    request.getSession().setAttribute("error.message", exception.getMessage());
                }))
            .logout(l -> l
                .deleteCookies("JSESSIONID", "AUTHORIZATION")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutUrl("/authentication/logout").permitAll()
                .addLogoutHandler((request, response, authentication) -> {
                    if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails){
                        User user = (User) userService.loadUserByUsername(userDetails.getUsername());
                        if (user.getProvider().equals(UserProvider.OAUTH2)){
                            try {
                                response.sendRedirect("/authentication/oauth2/logout");
                                return;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    SecurityContextHolder.clearContext();
                    contextLogoutHandler.logout(request, response, authentication);
                    System.out.println("logged out");
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (authentication == null) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.sendRedirect("/");
                    }
                }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Generated
    public WebConfiguration(final JWTFilter jwtFilter,
                            final JWTEntrypoint jwtEntrypoint,
                            final UserService userService,
                            final CustomLoginHandler customLoginHandler,
                            final CustomOauth2SuccessHandler customOauth2SuccessHandler){
        this.jwtFilter = jwtFilter;
        this.jwtEntrypoint = jwtEntrypoint;
        this.userService = userService;
        this.customLoginHandler = customLoginHandler;
        this.customOauth2SuccessHandler = customOauth2SuccessHandler;
    }
}
