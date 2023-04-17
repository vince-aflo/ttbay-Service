package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.exceptions.UsernameAlreadyExistException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import io.turntabl.ttbay.service.UsernameService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsernameServiceImpl implements UsernameService {
    private final UserRepository userRepository;
    private final TokenAttributesExtractor tokenAttributesExtractor;

    @Override
    public String updateUsername(JwtAuthenticationToken auth, String username){
        Map<String, String> allUsernamesWithEmails = findAllUsernamesWithEmails();
        Map<String, String> usernamesWithEmailsWithoutActiveUser = removeActiveUsersUsernameAndEmail(auth, allUsernamesWithEmails);
        if (usernamesWithEmailsWithoutActiveUser.containsValue(username)){
            throw new UsernameAlreadyExistException("unavailable");
        }
        return "available";
    }

    public Map<String, String> findAllUsernamesWithEmails(){
        List<User> allUsers = userRepository.findAll();
        return allUsers.parallelStream().filter(user -> user.getUsername() != null).collect(Collectors.toMap(User::getEmail, User::getUsername));
    }

    public Map<String, String> removeActiveUsersUsernameAndEmail(JwtAuthenticationToken auth, Map<String, String> allUsernamesWithEmails){
        String userEmail = tokenAttributesExtractor.extractEmailFromToken(auth);
        Optional<User> foundUser = userRepository.findByEmail(userEmail);
        Map<String, String> usernamesWithEmailsWithoutActiveUser = new HashMap<>(allUsernamesWithEmails);
        if (foundUser.isPresent()){
            String usernameOfFoundUser = foundUser.get().getUsername();
            if (usernameOfFoundUser != null) {
                usernamesWithEmailsWithoutActiveUser.remove(foundUser.get().getEmail());
            }
        }
        return usernamesWithEmailsWithoutActiveUser;
    }
}
