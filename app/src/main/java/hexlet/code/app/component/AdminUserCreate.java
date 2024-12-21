package hexlet.code.app.component;

import hexlet.code.app.model.User;
import hexlet.code.app.service.CustomUserDetailsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminUserCreate {
    private static final String ADMIN_EMAIL = "hexlet@example.com";
    private static final String ADMIN_PASSWORD = "qwerty";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void createAdminUser() {
        User user = new User();
        user.setEmail(ADMIN_EMAIL);
        user.setPassword(ADMIN_PASSWORD);
        customUserDetailsService.createUser(user);
    }
}
