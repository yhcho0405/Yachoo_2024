package proj.yachoo.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Room {
    private int id;
    private RoomStatus status;
    private List<User> users;

    public Room(int id, RoomStatus status) {
        this.id = id;
        this.status = status;
        this.users = new ArrayList<>();
    }

    @Getter
    public enum RoomStatus {
        EMPTY(0), STANDBY(1), FULL(2);

        private final int numberOfUsers;

        RoomStatus(int numberOfUsers) {
            this.numberOfUsers = numberOfUsers;
        }

    }
}
