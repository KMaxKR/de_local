package ks.mx.de_local.repository.entity;

import ks.mx.de_local.entity.Validation.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {

    @Query("SELECT v.id FROM Validation v WHERE v.user_email = :email")
    Long findIdByUserEmail(@Param("email") String email);

    @Query("SELECT v.user_code FROM Validation v WHERE v.id = :id")
    Long findUserCodeById(@Param("id") Long id);

}
