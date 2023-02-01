package io.turntabl.ttbay.dto;


import io.turntabl.ttbay.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private String message;
    private String fullName;
    private String email;
    private String picture;
    private Role role;
}
