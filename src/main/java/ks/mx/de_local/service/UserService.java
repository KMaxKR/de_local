package ks.mx.de_local.service;

import ks.mx.de_local.DTO.UserDTO;
import ks.mx.de_local.entity.User;
import ks.mx.de_local.entity.Provider.UserProvider;
import ks.mx.de_local.entity.Validation.Validation;
import ks.mx.de_local.repository.entity.UserRepository;
import ks.mx.de_local.repository.entity.ValidationRepository;
import ks.mx.de_local.repository.service.ServiceRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService, ServiceRepository {
    private final Logger logger = LoggerFactory.getLogger("UserService");
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        try {
            User user = userRepository.findUserByUsername(username).orElseThrow();
            user.setLast_login(new Date());
            return user;
        }catch (UsernameNotFoundException e){
            logger.error("No such user {}", username);
            return null;
        }
    }

    @Override
    public boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }


    @Override
    public boolean registerUser(UserDTO dto, String image_url) {
        if (!userRepository.existsByUsername(dto.getUsername()) && !userRepository.existsByEmail(dto.getEmail())) {
            User user = User.builder()
                .username(dto.getUsername())
                .provider(dto.getProvider())
                .email(dto.getEmail())
                .image_url(image_url)
                .account_valid(false)
                .createAt(new Date())
                .last_login(new Date())
                .build();
            if (user.getProvider().equals(UserProvider.LOCAL)) {
                user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
            } else {
                user.setPassword(null);
            }
            userRepository.save(user);
            return true;
        }else {
            logger.info("User with this email/username already exists");
            return false;
        }
    }


    @Override
    public User loadUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow();
    }



    public boolean passwordMatching(UserDTO dto){
        if (String.valueOf(dto.getProvider()).equals(UserProvider.LOCAL.name())){
            System.out.println(dto.getPassword());
            System.out.println(loadUserByUsername(dto.getUsername()).getPassword());
            System.out.println(new BCryptPasswordEncoder().matches(dto.getPassword(), loadUserByUsername(dto.getUsername()).getPassword()));
            return true;
            //return new BCryptPasswordEncoder().matches(dto.getPassword(), loadUserByUsername(dto.getUsername()).getPassword());
        } else {
            return true;
        }
    }
}
