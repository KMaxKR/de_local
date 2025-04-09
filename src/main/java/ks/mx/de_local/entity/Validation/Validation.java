package ks.mx.de_local.entity.Validation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code_validation_table")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Validation {

    @Id
    private int id;

    @Column(name = "user_id")
    private int user_id;

    @Column(name = "user_code")
    private int user_code;

    @Column(name = "user_email")
    private String user_email;
}
