package io.turntabl.ttbay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemImage> imageList;


    @OneToMany(mappedBy = "item",cascade = CascadeType.ALL)
    @JsonManagedReference(value = "item-auction")
    private List<Auction> auction;
    private String description;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_email")
    private User user;
    private Boolean onAuction;

    private Boolean isSold;

    @Enumerated(EnumType.STRING)

    private ItemCondition condition;

    @Enumerated(EnumType.STRING)
    private Category category;

    private boolean isItemExchanged;

    private boolean highestBidderReceivedItem;

    private boolean auctioneerHandItemToHighestBidder;


    public Item(String name, String description,  User user, List<ItemImage> imageList, Boolean onAuction, Boolean isSold) {
        this.name = name;
        this.imageList = imageList;
        this.description = description;
        this.user = user;
        this.onAuction = onAuction;
        this.isSold = isSold;
    }

}
