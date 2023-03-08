package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

}
