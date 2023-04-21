package io.turntabl.ttbay.controller;


import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.AccountSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1/account")
public class AccountSettingsController{
    private final AccountSettingsService accountSettingsService;

    @DeleteMapping("/user/{email}")
    public ResponseEntity<String> deleteUser(Authentication authentication, @PathVariable String email) throws MismatchedEmailException, ResourceNotFoundException{
        return ResponseEntity.ok(accountSettingsService.deleteAccount(authentication,email));
    }
}
