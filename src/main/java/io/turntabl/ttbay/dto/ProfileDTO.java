package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.Office;
import io.turntabl.ttbay.enums.Weekday;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record ProfileDTO(
        @NotBlank
        String profileId,
        @Email
        String email,
        @NotBlank
        String fullName,
        @NotBlank
        String profileUrl,
        @NotNull
        Office office,
        @Size(max = 5)
        @UniqueElements
        List<Weekday> officeDays
) {}
