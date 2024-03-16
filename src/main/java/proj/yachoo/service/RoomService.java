package proj.yachoo.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.Room.RoomStatus;
import proj.yachoo.domain.User;
import proj.yachoo.repository.RoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
    private static final int TOTAL_ROOMS = 30;

    private final RoomRepository roomRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i <= TOTAL_ROOMS; i++) {
            roomRepository.save(new Room(i, RoomStatus.EMPTY));
        }
    }

    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    public synchronized boolean joinRoom(User user, int roomId) {
        Room room = roomRepository.findById(roomId);

        if (room != null) {
            RoomStatus currentStatus = room.getStatus();

            if (currentStatus.getNumberOfUsers() < RoomStatus.FULL.getNumberOfUsers()) {
                RoomStatus newStatus = RoomStatus.values()[currentStatus.ordinal() + 1];
                room.setStatus(newStatus);
                room.getUsers().add(user);
                roomRepository.save(room);
                return true;
            }
        }

        return false;
    }

    public synchronized void removeUserFromRoom(User user) {
        List<Room> rooms = roomRepository.findAll();
        Room room = rooms.stream().filter(r -> r.getUsers().contains(user)).findFirst().orElse(null);

        if (room != null) {
            room.getUsers().remove(user);
            RoomStatus newStatus = RoomStatus.values()[room.getUsers().size()];
            room.setStatus(newStatus);
            roomRepository.save(room);
        }
    }
}