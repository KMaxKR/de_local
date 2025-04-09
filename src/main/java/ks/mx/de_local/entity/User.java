package ks.mx.de_local.entity;

import java.util.Collection; // Standard Java imports
import java.util.Date;
import java.util.List;

import ks.mx.de_local.entity.Provider.UserProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "user_database")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private UserProvider provider;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "created_at")
    private Date createAt;

    @Column(name = "last_login")
    private Date last_login;

    @Column(name = "account_verification")
    private boolean account_valid;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return account_valid;
    }

    @Override
    public boolean isAccountNonLocked() {
        return account_valid;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return account_valid;
    }

    @Override
    public boolean isEnabled() {
        return account_valid;
    }
}
