package io.turntabl.ttbay.exceptions;

public class ModelCreateException extends RuntimeException{
    public ModelCreateException(String errorMessage){
        super(errorMessage);
    }
}
