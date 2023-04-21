package io.turntabl.ttbay.exceptions;

public class BidCannotBeZero extends BidException{
    public BidCannotBeZero (){
        super("Bid cannot be zero");
    }
}
