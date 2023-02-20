package io.turntabl.ttbay.exceptions;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(String errorMessage){
        super(errorMessage);
    }
}
