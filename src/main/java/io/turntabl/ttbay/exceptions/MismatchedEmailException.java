package io.turntabl.ttbay.exceptions;

public class MismatchedEmailException extends Exception{
    public MismatchedEmailException(String errorMessage){
        super(errorMessage);
    }
}
