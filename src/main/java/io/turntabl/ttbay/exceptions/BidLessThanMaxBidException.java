package io.turntabl.ttbay.exceptions;

public class BidLessThanMaxBidException extends BidException{
    public BidLessThanMaxBidException(String message){
        super(message);
    }
}
