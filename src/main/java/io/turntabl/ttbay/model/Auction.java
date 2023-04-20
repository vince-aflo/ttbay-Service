package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.turntabl.ttbay.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auction{
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auctioner_email")
    @JsonBackReference
    private User auctioner;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference(value = "item-auction")
    private Item item;

    private Date startDate;

    private Date endDate;

    private Double reservedPrice;

    private Double currentHighestBid;

    @OneToOne
    @JoinColumn(name = "winner_email")
    private User winner;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "bid-auction")
    @JsonIgnore
    private List<Bid> bids;

    private Double winningPrice;
}

