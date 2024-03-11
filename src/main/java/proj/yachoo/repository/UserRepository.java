package proj.yachoo.repository;

import proj.yachoo.domain.User;

public interface UserRepository {
    User findById(int id);
    void save(User user);
}
