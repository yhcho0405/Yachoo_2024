package proj.yachoo.repository;

import proj.yachoo.domain.Room;
import java.util.List;

public interface RoomRepository {
    List<Room> findAll();
    Room findById(int id);
    void save(Room room);
}