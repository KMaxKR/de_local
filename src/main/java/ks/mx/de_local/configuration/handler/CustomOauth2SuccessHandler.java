package ks.mx.de_local.configuration.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ks.mx.de_local.DTO.UserDTO;
import ks.mx.de_local.entity.Provider.UserProvider;
import ks.mx.de_local.entity.User;
import ks.mx.de_local.service.JWTService;
import ks.mx.de_local.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JWTService jwtService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger("CustomOauth2SuccessHandler");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        response.addCookie(checkUserAuthenticationOAuth2Services(user));
        response.sendRedirect("/");
    }

    private Cookie checkUserAuthenticationOAuth2Services(OAuth2User user){
        logger.info("Info: {}",userService.checkIfUserExists(user.getAttribute("email")) );
        if (!userService.checkIfUserExists(user.getAttribute("email"))){
            userService.registerUser(UserDTO.builder()
                .username(user.getAttribute("name"))
                .email(user.getAttribute("email"))
                .provider(UserProvider.OAUTH2)
                .password(null)
                .build(), user.getAttribute("picture"));
        }
        User user1 = userService.loadUserByEmail(user.getAttribute("email"));
        logger.info(user1.toString());
        return createCookie(UserDTO.builder()
            .username(user1.getUsername())
            .provider(user.getAttribute("provider"))
            .password(null)
            .build());
    }

    private Cookie createCookie(UserDTO dto){
        if (userService.passwordMatching(dto)) {
            Cookie cookie = new Cookie("AUTHORIZATION", URLEncoder.encode(jwtService.generateToken(dto.getUsername()), StandardCharsets.UTF_8));
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(600);
            logger.info("cookie created{} = {}", cookie.getName(), cookie.getValue());
            return cookie;
        }else {
            return new Cookie("", "");
        }
    }
}
