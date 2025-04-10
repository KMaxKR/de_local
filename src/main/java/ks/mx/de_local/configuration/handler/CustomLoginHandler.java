package ks.mx.de_local.configuration.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ks.mx.de_local.DTO.UserDTO;
import ks.mx.de_local.entity.User;
import ks.mx.de_local.service.JWTService;
import ks.mx.de_local.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class CustomLoginHandler implements AuthenticationSuccessHandler {
    private final JWTService jwtService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger("CustomLoginHandler");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        Cookie token = createCookie(UserDTO.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .provider(user.getProvider())
            .build());
        response.addCookie(token);
        response.sendRedirect("/");
    }

    private Cookie createCookie(UserDTO dto){
        //TODO how it work password matching by default
        //if (userService.passwordMatching(dto)) {
        Cookie cookie = new Cookie("AUTHORIZATION", URLEncoder.encode(jwtService.generateToken(dto.getUsername()), StandardCharsets.UTF_8));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(600);
        logger.info("cookie created{} = {}", cookie.getName(), cookie.getValue());
        return cookie;
        //}else {
        //return new Cookie("", "");
        //}
    }
}
