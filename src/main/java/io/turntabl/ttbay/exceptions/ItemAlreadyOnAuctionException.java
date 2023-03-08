package io.turntabl.ttbay.exceptions;

public class ItemAlreadyOnAuctionException extends Exception{
    public ItemAlreadyOnAuctionException(String errorMessage){
        super(errorMessage);
    }
}
