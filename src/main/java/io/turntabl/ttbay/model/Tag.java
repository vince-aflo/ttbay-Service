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
public class Tag{
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference
    @JoinColumn(name = "item_id")
    private Item item;

    public Tag(Item itemId, String name){
        this.item = itemId;
        this.name = name;
    }

    @Override
    public String toString(){
        return "Tags{" +
                "name=" + name +
                '}';
    }
}
