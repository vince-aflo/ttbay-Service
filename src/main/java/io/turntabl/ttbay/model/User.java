package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.OfficeLocation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String email;
    private String fullName;
    private String profileUrl;
    private OfficeLocation officeLocation;
    @OneToMany(mappedBy = "user", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OfficeDay> officeDays;

    public User(
            String email,
            String fullName,
            String profileUrl,
            OfficeLocation officeLocation){
        this.email = email;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.officeLocation = officeLocation;
    }


}
