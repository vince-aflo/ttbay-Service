package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Builder
public class User{
    @Id
    private String email;

    private String username;

    private String fullName;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private OfficeLocation officeLocation;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OfficeDay> officeDays;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Item> items;

    @OneToMany(mappedBy = "auctioner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Auction> auctions;

    public User(String username, String email, String fullName, String profileUrl, OfficeLocation officeLocation){
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.officeLocation = officeLocation;
    }

    public User(String email, String username, String fullName, String profileUrl, Role role, OfficeLocation officeLocation, List<OfficeDay> officeDays){
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.role = role;
        this.officeLocation = officeLocation;
        this.officeDays = officeDays;
    }
}
