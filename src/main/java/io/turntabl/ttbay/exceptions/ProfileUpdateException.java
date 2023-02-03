package io.turntabl.ttbay.exceptions;

public class ProfileUpdateException extends RuntimeException {
    public ProfileUpdateException(String errorMessage){
        super(errorMessage);
    }
}
