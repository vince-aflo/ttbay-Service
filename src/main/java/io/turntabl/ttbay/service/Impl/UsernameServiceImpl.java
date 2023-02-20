package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.exceptions.UsernameAlreadyExistException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UsernameService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsernameServiceImpl implements UsernameService {

    private final UserRepository userRepository;

    @Override
    public String updateUsername(JwtAuthenticationToken auth, String username) {
        Map<String, String> allUsernames = findAllUnavailableUsernames();

        Map<String, String> unavailableUsernames = removeActiveUsersUsername(auth, allUsernames);

        if (unavailableUsernames.containsValue(username)) {
            throw new UsernameAlreadyExistException("unavailable");
        }
        return "available";
    }

    public Map<String, String> findAllUnavailableUsernames() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.parallelStream().filter(user -> user.getUsername() != null).collect(Collectors.toMap(User::getEmail, User::getUsername));
    }

    public Map<String, String> removeActiveUsersUsername(JwtAuthenticationToken auth, Map<String, String> usernames) {
        String userEmail = (String) auth.getTokenAttributes().get("email");
        User foundUser = userRepository.findByEmail(userEmail).orElse(null);
        assert foundUser != null;
        String usernameOfFoundUser = foundUser.getUsername();

        if (usernameOfFoundUser != null) {
            usernames.remove(foundUser.getEmail());
        }
        return usernames;
    }

}
