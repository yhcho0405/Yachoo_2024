package proj.yachoo.service.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.Room.RoomStatus;
import proj.yachoo.domain.game.Game;
import proj.yachoo.service.NotificationService;
import proj.yachoo.service.RoomService;

@Service
@RequiredArgsConstructor
public class GameService {
    private final NotificationService notificationService;
    private final RoomService roomService;

    public void startGame(int roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room != null && room.getStatus() == RoomStatus.FULL) {
            notificationService.sendRoom(roomId, "게임을 시작합니다.");
            // room.setGame(new Game());
        }
    }
}