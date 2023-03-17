package io.turntabl.ttbay.exceptions;

public class UserCannotBidOnTheirAuction extends Exception{
    public UserCannotBidOnTheirAuction(){
        super("User cannot bid on their own auction");
    }

}
