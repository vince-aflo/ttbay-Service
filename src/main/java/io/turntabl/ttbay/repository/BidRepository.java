package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository  extends JpaRepository<Bid, Long> {
    Optional<List<Bid>> findByAuction(Auction auction);
}
