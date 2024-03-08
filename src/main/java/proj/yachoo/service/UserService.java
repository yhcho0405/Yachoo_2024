package proj.yachoo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.Status;
import proj.yachoo.domain.User;
import proj.yachoo.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user) {

        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }
}
