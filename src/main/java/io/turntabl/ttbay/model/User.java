package io.turntabl.ttbay.model;

import io.turntabl.ttbay.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User   {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    private String username;
    private String fullName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    private String picture;
    private Role role;
    private String location;

}

