package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.turntabl.ttbay.enums.Weekday;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private Weekday weekday;

    public OfficeDay(User userId, Weekday weekday) {
        this.user = userId;
        this.weekday = weekday;
    }

    @Override
    public String toString() {
        return "OfficeDay{" +
                "weekday=" + weekday +
                '}';
    }
}
