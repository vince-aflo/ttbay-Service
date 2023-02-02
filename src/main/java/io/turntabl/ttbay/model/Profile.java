package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.Office;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    private String profileId;
    private String email;
    private String fullName;
    private String profileUrl;
    private Office office;
    @OneToMany(mappedBy = "profileId", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
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
