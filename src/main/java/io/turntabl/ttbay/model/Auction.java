package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.turntabl.ttbay.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auction {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "auctioner_email")
    @JsonBackReference
    private User auctioner;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    private Date startDate;
    private Date endDate;
    private Double reservedPrice;
    private Double currentHighestBid;
    @OneToOne
    @JoinColumn(name = "winner_email")
    private User winner;

    private AuctionStatus status;
    
}

