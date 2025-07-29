package de.germanmatrimony.backend.dto;

import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private String password;
    private String gender;
    
    @JsonProperty("dob")
    private LocalDate dateOfBirth;
}
