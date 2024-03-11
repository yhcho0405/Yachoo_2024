package proj.yachoo.repository;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.Room;

@Repository
@Getter
public class MemoryRoomRepository implements RoomRepository {

    private Map<Integer, Room> rooms = new HashMap<>();

    private static final int MAX_ROOMS = 30;

    @PostConstruct
    public void initRooms() {
        for (int i = 0; i < MAX_ROOMS; i++) {
            Room room = new Room(i, Room.Status.EMPTY);
            rooms.put(room.getId(), room);
        }
    }

    @Override
    public Room findById(int id) {
        return null;
    }

}
