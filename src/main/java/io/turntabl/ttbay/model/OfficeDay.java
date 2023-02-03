package io.turntabl.ttbay.model;

import io.turntabl.ttbay.enums.Weekday;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
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
