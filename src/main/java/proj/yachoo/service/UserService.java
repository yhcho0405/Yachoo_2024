package proj.yachoo.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.User;
import proj.yachoo.domain.User.UserStatus;

@Service
public class UserService {
    private final AtomicInteger userCounter = new AtomicInteger(10000);

    public User generateUser(String sessionId) {
        String username = "User" + userCounter.getAndIncrement();
        return new User(username, sessionId);
    }
}
