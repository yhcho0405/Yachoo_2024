package proj.yachoo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.User;
import proj.yachoo.repository.MemoryUserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemoryUserRepository userRepository;

    public void saveUser(User user) {
        user.setStatus(User.Status.ONLINE);
        userRepository.save(user);
    }
}
