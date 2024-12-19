package hexlet.code.app.controller;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public List<User> index() {
        return userRepository.findAll();
    }

    @GetMapping()
    public Optional<User> show(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @GetMapping()
    public Optional<User> show(@PathVariable String email) {
        return  userRepository.findByEmail(email);
    }
}
