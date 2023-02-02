package io.turntabl.ttbay.exceptions;

public class ProfileCreationException extends RuntimeException {
    public ProfileCreationException(String errorMessage){
        super(errorMessage);
    }
}
