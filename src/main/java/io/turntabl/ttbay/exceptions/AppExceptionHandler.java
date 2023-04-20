package io.turntabl.ttbay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AppExceptionHandler{
    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<String> handleInvalidOrder(ProfileUpdateException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelCreateException.class)
    public ResponseEntity<String> handleItemCreationException(ModelCreateException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MismatchedEmailException.class, ForbiddenActionException.class, ItemAlreadyOnAuctionException.class, BidLessThanMaxBidException.class, BidCannotBeZero.class, UserCannotBidOnTheirAuction.class})
    public ResponseEntity<String> handleMismatchEmails(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<String> handleUsernameDuplicationValidation(UsernameAlreadyExistException exception, WebRequest request){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
