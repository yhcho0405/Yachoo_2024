package proj.yachoo.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.User;

@Repository
public class MemoryUserRepository implements UserRepository {

    private Map<String, User> users = new HashMap<>();

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public void save(User user) {

    }
}
