package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Weekday;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record ProfileDTO(@NotBlank(message = "username has to be set") String username,
                         @Email(message = "A valid email has to be set") String email,
                         @NotBlank(message = "fullName has to be set") String fullName,
                         @NotBlank(message = "profileUrl has to be set") String profileUrl,
                         @NotNull(message = "officeLocation has to be set") OfficeLocation officeLocation,
                         @Size(max = 5, message = "Only 5 office days can be set") @UniqueElements(message = "Office days must be unique") List<Weekday> officeDays) {
}
