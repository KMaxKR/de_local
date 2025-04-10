package ks.mx.de_local.configuration.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ks.mx.de_local.service.JWTService;
import ks.mx.de_local.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger("JWTFilter");
    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String req_token = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null){
            Cookie[] var6 = cookies;
            int var7 = var6.length;

            for (Cookie cookie:cookies){
                if ("AUTHORIZATION".equals(cookie.getName())){
                    req_token = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (req_token != null && req_token.startsWith("Bearer ")){
            req_token = req_token.substring(7);
        }

        if (req_token != null && !req_token.isEmpty()){
            try {
                if (jwtService.isValidToken(req_token) && userService.loadUserByUsername(jwtService.getUsernameFromToken(req_token)).isEnabled()){
                    String username = jwtService.getUsernameFromToken(req_token);
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.info("User {}, authenticated successful", username);
                    logger.info("For request {} ", request.getRequestURI());
                }
            }catch (Exception e){
                logger.error("Authentication error: {} ", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
