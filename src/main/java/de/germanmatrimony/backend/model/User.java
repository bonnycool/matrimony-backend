package de.germanmatrimony.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "users",uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotBlank
    @Email
    @Column( nullable = false)
    private String email;

    private String password;

    private String gender;

   @JsonFormat(pattern = "yyyy-MM-dd")  // ✅ Required for JSON → LocalDate
   @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String role = "USER";
}
