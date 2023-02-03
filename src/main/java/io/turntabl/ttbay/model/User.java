package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.OfficeLocation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    private String userId;
    private String email;
    private String fullName;
    private String profileUrl;
    private OfficeLocation officeLocation;
    @OneToMany(mappedBy = "user", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OfficeDay> officeDays;

    public User(
            String userId,
            String email,
            String fullName,
            String profileUrl,
            OfficeLocation officeLocation){
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.officeLocation = officeLocation;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "profileId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", office=" + officeLocation +
                ", officeDays=" + officeDays +
                '}';
    }
}
