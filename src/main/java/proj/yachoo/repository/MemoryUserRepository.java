package proj.yachoo.repository;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.User;

@Repository
public class MemoryUserRepository implements UserRepository {

    private static final int START_USER_NUM = 10000;

    private AtomicInteger userCounter = new AtomicInteger(START_USER_NUM);
    private Map<Integer, User> users = new HashMap<>();



    @Override
    public User findById(int id) {
        return null;
    }

    @Override
    public void save(User user) {
        user.setId(userCounter);
        users.put(userCounter, user);
    }
}
