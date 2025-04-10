package ks.mx.de_local.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ks.mx.de_local.DTO.UserDTO;
import ks.mx.de_local.entity.Provider.UserProvider;
import ks.mx.de_local.service.EmailService;
import ks.mx.de_local.service.JWTService;
import ks.mx.de_local.service.UserService;
import ks.mx.de_local.service.ValidationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@AllArgsConstructor
public class AuthenticationController {
    private final JWTService jwtService;
    private final UserService userService;
    private final EmailService emailService;
    private final ValidationService validationService;
    private final Logger logger = LoggerFactory.getLogger("AuthenticationController");
    private final SecurityContextLogoutHandler doLogout = new SecurityContextLogoutHandler();

    // return authentication page
    @RequestMapping("/authentication")
    public String returnAuthenticationPage(){
        return "authentication.html";
    }

    //return signup page
    @RequestMapping("/signup")
    public String returnSignUpPage(){
        return "signup.html";
    }

    // sign up user with classic authentication
    @RequestMapping(path = "/authentication/signup", method = RequestMethod.POST)
    public void registerUser(UserDTO userDTO, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.info("User info: {}", userDTO);
        userDTO.setProvider(UserProvider.LOCAL);
        if (userService.registerUser(userDTO, null)){
            // TODO Secured transfer
            request.getSession().setAttribute("email", userDTO.getEmail());
            response.sendRedirect("/authentication/confirmation");
        }else {
            logger.info("sign up error");
            response.sendRedirect("/");
        }
    }

    // send email with code
    @RequestMapping("/authentication/confirmation")
    public String authenticationConfirmation(HttpServletRequest request) throws IOException {
        try {
            emailService.sendConfirmationCode(request.getSession().getAttribute("email").toString());
        }catch (Exception e){
            logger.info("Cannot invoke getAttribute(email)");
        }
        return "validation.html";
    }

    // validate code
    @RequestMapping("/authentication/confirm")
    public String validateCode(HttpServletRequest request){
        int code = Integer.parseInt(request.getParameter("code"));
        validationService.validateUser(request.getSession().getAttribute("email").toString(), code);
        logger.info("Code1: {}", code);
        return "main.html";
    }

    // check user authentication status
    @RequestMapping("/f")
    public void f(HttpServletResponse response, HttpServletRequest request) throws IOException {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        Cookie[] cookies = request.getCookies();
        for (Cookie c:cookies){
            if (c.getName().equals("AUTHORIZATION")) {
                logger.info("Token: {}", URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8));
            }
        }
        response.sendRedirect("/");
    }

    @RequestMapping("/authentication/oauth2/logout")
    public String Oauth2Logout(HttpServletResponse response) throws IOException {
        //response.sendRedirect("https://accounts.google.com/logout");
        return "oauth2_logout";
    }


    @RequestMapping("/c")
    public void infoCheck(HttpServletResponse response) throws IOException {
        validationService.check();
        response.sendRedirect("/");
    }
}
