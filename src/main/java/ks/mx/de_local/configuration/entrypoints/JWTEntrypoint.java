package ks.mx.de_local.configuration.entrypoints;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTEntrypoint implements AuthenticationEntryPoint {
    public JWTEntrypoint(){
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            System.out.println("No cookies found in the request!");
        } else {
            for (Cookie cookie : cookies) {
                System.out.println("Found Cookie: " + cookie.getName() + " = " + cookie.getValue());
            }
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
    }
}