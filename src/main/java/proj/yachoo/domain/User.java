package proj.yachoo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private String sessionId;
    private UserStatus status;

    public enum UserStatus {
        LOBBY, ROOM
    }

    public User(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
        this.status = UserStatus.LOBBY;
    }
}
