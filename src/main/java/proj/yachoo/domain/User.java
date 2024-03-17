package proj.yachoo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private String sessionId;
    private Integer roomId;

    public User(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
        this.roomId = null;
    }

    public boolean isInLobby() {
        return roomId == null;
    }

    public boolean isInRoom() {
        return roomId != null;
    }
}
