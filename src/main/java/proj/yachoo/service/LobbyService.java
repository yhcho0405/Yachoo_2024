package proj.yachoo.service;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final Set<String> lobbyUsers = new HashSet<>();

    public void addUserToLobby(String sessionId) {
        lobbyUsers.add(sessionId);
    }

    public void removeUserFromLobby(String sessionId) {
        lobbyUsers.remove(sessionId);
    }

    public boolean isUserInLobby(String sessionId) {
        return lobbyUsers.contains(sessionId);
    }

    public Set<String> getLobbyUsers() {
        return new HashSet<>(lobbyUsers);
    }
}
