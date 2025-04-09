package ks.mx.de_local.DTO;


import ks.mx.de_local.entity.Provider.UserProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String password;
    private UserProvider provider;
    private String email;
}
