package io.turntabl.ttbay.service;


import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;

public interface AccountSettingsService {
    String deleteAccount(Authentication authentication,String userEnteredEmail) throws MismatchedEmailException, ResourceNotFoundException;
}
