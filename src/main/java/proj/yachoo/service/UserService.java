package proj.yachoo.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.User;

@Service
public class UserService {
    private AtomicInteger userCounter = new AtomicInteger(10000);

    public User generateUser(String sessionId) {
        String username = "User" + userCounter.getAndIncrement();
        return new User(username, sessionId);
    }
}
