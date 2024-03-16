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
import proj.yachoo.dto.message.ConnectionInfoDto;
import proj.yachoo.dto.message.RoomListDto;
import proj.yachoo.dto.request.JoinRoomRequestDto;
import proj.yachoo.service.LobbyService;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.UserService;

@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final UserService userService;
    private final RoomService roomService;
    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/connect")
    @SendToUser("/queue/info")
    public ConnectionInfoDto handleConnection(StompHeaderAccessor headerAccessor) {
        User user = userService.generateUser(headerAccessor.getSessionId());
        headerAccessor.getSessionAttributes().put("user", user);

        lobbyService.addUserToLobby(user.getSessionId());

        return new ConnectionInfoDto(
                user.getUsername(),
                roomService.getRooms().size(),
                roomService.getRoomStatuses()
        );
    }

    @MessageMapping("/room/join")
    public void handleJoinRoom(JoinRoomRequestDto requestDto, StompHeaderAccessor headerAccessor) {
        User user = (User)headerAccessor.getSessionAttributes().get("user");
        boolean joined = roomService.joinRoom(user, requestDto.getRoomId());

        // room 접속
        if (joined) {
            lobbyService.removeUserFromLobby(user.getSessionId());
            messagingTemplate.convertAndSendToUser(
                    user.getSessionId(), "/queue/room/join",
                    requestDto.getRoomId(),
                    createHeaders(user.getSessionId())
            );
            messagingTemplate.convertAndSend(
                    "/topic/room/list",
                    new RoomListDto(
                            roomService.getRooms().size(),
                            roomService.getRoomStatuses()
                    )
            );
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = (User) headerAccessor.getSessionAttributes().get("user");

        // 유저 연결 종료
        if (user.getStatus() == User.UserStatus.ROOM) { // room에 접속해 있을 때
            roomService.removeUserFromRoom(user);

            messagingTemplate.convertAndSend(
                    "/topic/room/list",
                    new RoomListDto(
                            roomService.getRooms().size(),
                            roomService.getRoomStatuses()
                    )
            );
        } else { // lobby에 접속해 있을 때
            lobbyService.removeUserFromLobby(user.getSessionId());
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        return headerAccessor.getMessageHeaders();
    }

}
