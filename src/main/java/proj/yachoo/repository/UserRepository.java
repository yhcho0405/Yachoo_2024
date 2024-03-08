package proj.yachoo.repository;

import proj.yachoo.domain.User;

public interface UserRepository {
    User findById(String id);
    void save(User user);
}
