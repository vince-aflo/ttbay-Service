package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AccountSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountSettingsImpl implements AccountSettingsService {

    private final UserRepository userRepository;

    @Override
    public String deleteAccount(Authentication authentication, String userEnteredEmail) throws MismatchedEmailException, ResourceNotFoundException {


        // extract email from token
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = auth.getTokenAttributes();
        String email = (String) claims.get("email");

        // compare token email with user entered email
        if(!email.equals(userEnteredEmail)) {
            throw new MismatchedEmailException("You're forbidden to delete the account");
        }

        //find user in db
        Optional<User> user = userRepository.findByEmail(email);


        if(user.isEmpty()) {
            //throw  not found exception
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.deleteById(userEnteredEmail);
        return user.get().getEmail() + " user deleted successfully";
    }


}
