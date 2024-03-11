package proj.yachoo.repository;

import proj.yachoo.domain.Room;

public interface RoomRepository {
    Room findById(int id);

}
