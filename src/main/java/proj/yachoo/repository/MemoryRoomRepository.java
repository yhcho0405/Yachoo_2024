package proj.yachoo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.Room;

@Repository
public class MemoryRoomRepository implements RoomRepository {
    private final Map<Integer, Room> roomMap = new HashMap<>();

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(roomMap.values());
    }

    @Override
    public Room findById(int id) {
        return roomMap.get(id);
    }

    @Override
    public void save(Room room) {
        roomMap.put(room.getId(), room);
    }
}