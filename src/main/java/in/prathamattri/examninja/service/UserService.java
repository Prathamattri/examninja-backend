package in.prathamattri.examninja.service;

import in.prathamattri.examninja.model.User;
import in.prathamattri.examninja.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User getUser(String email) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        return userByEmail.orElse(null);
    }
}
