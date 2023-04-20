package io.turntabl.ttbay.exceptions;

public class UserCannotBidOnTheirAuction extends BidException{
    public UserCannotBidOnTheirAuction(){
        super("User cannot bid on their own auction");
    }
}
