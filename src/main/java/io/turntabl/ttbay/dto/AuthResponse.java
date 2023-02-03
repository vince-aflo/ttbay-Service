package io.turntabl.ttbay.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String fullName;
    private String email;
    private String picture;
    private boolean hasFilledUserProfile;
}
