package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private Double bidAmount;

    @ManyToOne
    @JoinColumn(name = "bidder_email")
    private User bidder;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    @JsonBackReference
    private Auction auction;

}
