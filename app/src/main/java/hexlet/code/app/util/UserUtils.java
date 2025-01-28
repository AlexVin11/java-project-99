package hexlet.code.app.util;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null || !authentification.isAuthenticated()) {
            return null;
        }
        var email = authentification.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    public boolean isCurrentUser(Long id) {
        var emailOfUser = userRepository.findById(id).orElseThrow().getEmail();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return emailOfUser.equals(authentication.getName());
    }

    public boolean userHasAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public User getTestUser() {
        return  userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
