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
public class ItemImage {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String imageUrl;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference
    @JoinColumn(name = "item_id")
    private Item item;

    public ItemImage(Item itemId, String imageUrl) {
        this.item = itemId;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ItemImage{" +
                "imageUrl=" + imageUrl +
                '}';
    }
}
