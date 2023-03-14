package io.turntabl.ttbay.exceptions;

public class ForbiddenActionException extends Exception{
    public ForbiddenActionException(String errorMessage){
        super(errorMessage);
    }
}
