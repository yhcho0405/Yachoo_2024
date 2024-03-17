package proj.yachoo.repository;

import proj.yachoo.domain.User;

public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
    User findBySessionId(String sessionId);
    void removeUser(User user);
}