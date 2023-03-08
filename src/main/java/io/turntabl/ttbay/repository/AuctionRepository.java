package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<List<Auction>> findAllByAuctioner(User auctioner);
}
