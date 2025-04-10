package ks.mx.de_local.service;

import ks.mx.de_local.entity.User;
import ks.mx.de_local.entity.Validation.Validation;
import ks.mx.de_local.repository.entity.ValidationRepository;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidationService {
    private final Logger logger = LoggerFactory.getLogger("ValidationService");
    private final UserService userService;
    private final ValidationRepository validationRepository;

    public void saveValidation(String email, int code){
        int user_id = Math.toIntExact(userService.loadUserByEmail(email).getId());
        validationRepository.save(Validation.builder()
            .user_email(email)
            .user_code(code)
            .user_id(user_id)
            .build());
    }
    public void validateUser(String email, int code){
        int cd = Math.toIntExact(validationRepository.findUserCodeById(validationRepository.findIdByUserEmail(email)));
        if (code == cd){
            User user = userService.loadUserByEmail(email);
            user.setAccount_valid(true);
            userService.saveUser(user);
            logger.info("User was update (account_valid = true)");
        } else{
            logger.info("Code not matching");
        }
    }

    public void check() {
        logger.info("Info {}", validationRepository.findIdByUserEmail("kira.pc.one@gmail.com"));
        logger.info("Info code: {}", validationRepository.findUserCodeById(1L));
    }
}
