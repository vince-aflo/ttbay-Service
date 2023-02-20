package io.turntabl.ttbay.exceptions;

public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
