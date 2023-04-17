package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>{
    List<Item> findByUser(User user);
}
