package proj.yachoo.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.Room;
import proj.yachoo.repository.MemoryRoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final MemoryRoomRepository roomRepository;

    public int activateRooms() {
        return roomRepository.getRooms().size();
    }

    public List<Integer> roomsStatus() {
        List<Integer> statusList = new ArrayList<>();
        for (Room room : roomRepository.getRooms().values()) {
            statusList.add(room.getStatus().getValue());
        }
        return statusList;
    }
}
