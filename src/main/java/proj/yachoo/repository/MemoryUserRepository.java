package proj.yachoo.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.User;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public void save(User user) {
        userMap.put(user.getUsername(), user);
    }

    @Override
    public User findByUsername(String username) {
        return userMap.get(username);
    }

    @Override
    public User findBySessionId(String sessionId) {
        return userMap.values().stream()
                .filter(user -> user.getSessionId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void removeUser(User user) {
        userMap.remove(user.getUsername());
    }
}
