package hexlet.code.app.controller.api;

import hexlet.code.app.dto.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public void create(@RequestBody AuthRequest authRequest) {
        var authentification = new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
                authRequest.getPassword());
        authenticationManager.authenticate(authentification);
    }
}
