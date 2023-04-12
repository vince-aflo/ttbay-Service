package io.turntabl.ttbay.service;

import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.User;
import jakarta.mail.MessagingException;

public interface EmailTriggerService {
    void sendBidWasMadeEmail(Auction auction, User bidder,Double bidAmount) throws MessagingException;
}
