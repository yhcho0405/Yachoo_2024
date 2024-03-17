package proj.yachoo.service;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.User;
import proj.yachoo.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AtomicInteger userCounter = new AtomicInteger(1000);
    private final UserRepository userRepository;

    public User generateUser(String sessionId) {
        String username = "User" + userCounter.getAndIncrement();
        User user = new User(username, sessionId);

        userRepository.save(user);

        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId);
    }

    public void removeUser(User user) {
        userRepository.removeUser(user);
    }
}
