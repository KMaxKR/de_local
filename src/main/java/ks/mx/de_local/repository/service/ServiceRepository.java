package ks.mx.de_local.repository.service;

import ks.mx.de_local.DTO.UserDTO;
import ks.mx.de_local.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository {
    boolean registerUser(UserDTO dto, String image_url);
    User loadUserByEmail(String email);
    boolean checkIfUserExists(String email);
    void saveUser(User user);
}
