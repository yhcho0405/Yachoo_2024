package proj.yachoo.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import proj.yachoo.domain.Room;

@Repository
public class MemoryRoomRepository implements RoomRepository {
    private List<Room> rooms = new ArrayList<>();

    @Override
    public List<Room> findAll() {
        return rooms;
    }

    @Override
    public Room findById(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void save(Room room) {
        rooms.removeIf(r -> r.getId() == room.getId());
        rooms.add(room);
    }
}