package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import proj.yachoo.domain.User;
import proj.yachoo.dto.response.ConnectionInfoDto;
import proj.yachoo.dto.response.RoomListDto;
import proj.yachoo.service.LobbyService;
import proj.yachoo.service.NotificationService;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.UserService;

@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final UserService userService;
    private final RoomService roomService;
    private final LobbyService lobbyService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/connect")
    @SendToUser("/queue/info")
    public ConnectionInfoDto handleConnection(StompHeaderAccessor headerAccessor) {
        User user = userService.generateUser(headerAccessor.getSessionId());
        headerAccessor.getSessionAttributes().put("user", user);

        lobbyService.addUserToLobby(user.getSessionId());
        notificationService.sendUser(user.getSessionId(), user.getUsername() + " joined the lobby.");
        notificationService.sendLobby(user.getUsername() + " joined the lobby.");

        return new ConnectionInfoDto(
                user.getUsername(),
                roomService.getRooms().size(),
                roomService.getRoomStatuses()
        );
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = userService.findBySessionId(headerAccessor.getSessionId());

        // 유저 연결 종료
        if (user != null) {
            userService.removeUser(user);
            if (user.isInRoom()) { // room에 접속해 있을 때
                notificationService.sendRoom(user.getRoomId(), user.getUsername() + " left the room.");
                roomService.removeUserFromRoom(user);
                messagingTemplate.convertAndSend(
                        "/topic/room/list",
                        new RoomListDto(
                                roomService.getRooms().size(),
                                roomService.getRoomStatuses()
                        )
                );

            } else if (user.isInLobby()) { // lobby에 접속해 있을 때
                lobbyService.removeUserFromLobby(user.getSessionId());
                notificationService.sendLobby(user.getUsername() + " left the lobby.");
            }
        }
    }
}
