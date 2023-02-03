package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.Office;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @NotBlank
    private String profileId;
    @Email
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String profileUrl;
    @NotNull
    private Office office;
    @Size(max = 5)
    @UniqueElements
    @OneToMany(mappedBy = "profile", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OfficeDay> officeDays;

    public Profile(
            String profileId,
            String email,
            String fullName,
            String profileUrl,
            Office office){
        this.profileId = profileId;
        this.email = email;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.office = office;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "profileId='" + profileId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", office=" + office +
                ", officeDays=" + officeDays +
                '}';
    }
}
