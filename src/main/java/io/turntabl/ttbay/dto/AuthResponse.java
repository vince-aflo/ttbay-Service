package io.turntabl.ttbay.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
        String message,
        String fullName,
        String email,
        String picture,
        boolean hasFilledUserProfile
){}
