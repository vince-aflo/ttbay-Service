package io.turntabl.ttbay.model;



import io.turntabl.ttbay.enums.Weekday;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OfficeDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.ALL)
   @NotNull(message="profile has to be set")
    private User user;
   @NotNull(message = "weekday has to be set")
    private Weekday weekday;
}
