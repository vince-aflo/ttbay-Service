package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuction(Auction auction);


}
