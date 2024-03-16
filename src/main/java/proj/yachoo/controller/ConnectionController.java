package proj.yachoo.controller;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.User;
import proj.yachoo.dto.message.ConnectionInfoDto;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.UserService;

@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final UserService userService;
    private final RoomService roomService;

    @MessageMapping("/connect")
    @SendToUser("/queue/info")
    public ConnectionInfoDto handleConnection(StompHeaderAccessor headerAccessor) {
        User user = userService.generateUser(headerAccessor.getSessionId());
        headerAccessor.getSessionAttributes().put("user", user);

        List<Room> rooms = roomService.getRooms();
        int[] roomStatuses = rooms.stream().mapToInt(room -> room.getStatus().ordinal()).toArray();
        
        return new ConnectionInfoDto(user.getUsername(), rooms.size(), roomStatuses);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = (User) headerAccessor.getSessionAttributes().get("user");

        // 유저 연결 종료
        roomService.removeUserFromRoom(user);
    }

}
