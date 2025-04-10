package ks.mx.de_local.controllers;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;


@org.springframework.stereotype.Controller
@AllArgsConstructor
public class Controller {
    private final Logger logger = LoggerFactory.getLogger("Controller");

    @RequestMapping("/")
    public String returnMainPage(Model model, Authentication authentication){
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "main.html";
    }

    @RequestMapping("/google")
    public void checkAuthenticationGitHub(@AuthenticationPrincipal OAuth2User principal){
        logger.info(Collections.singletonMap("name", principal.getAttribute("name")).toString());
    }
}
