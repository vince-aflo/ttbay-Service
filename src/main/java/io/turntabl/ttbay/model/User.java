package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.Office;
import io.turntabl.ttbay.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;


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
    private Role role;
    private String profileUrl;
    private Office office;
    @Size(max = 5)
    @UniqueElements
    @OneToMany(mappedBy = "user", fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OfficeDay> officeDays;

}

