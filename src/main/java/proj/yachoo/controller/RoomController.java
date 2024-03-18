package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.User;
import proj.yachoo.dto.request.JoinRoomRequestDto;
import proj.yachoo.dto.response.RoomListDto;
import proj.yachoo.service.LobbyService;
import proj.yachoo.service.NotificationService;
import proj.yachoo.service.RoomService;

@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final LobbyService lobbyService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/join")
    public void handleJoinRoom(JoinRoomRequestDto requestDto, StompHeaderAccessor headerAccessor) {
        User user = (User)headerAccessor.getSessionAttributes().get("user");
        int joined = roomService.joinRoom(user, requestDto.getRoomId());

        // room 접속
        if (joined != 0) {
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
            notificationService.sendUser(user.getSessionId(), "join room" + requestDto.getRoomId() + ".");
            notificationService.sendRoom(requestDto.getRoomId(), user.getUsername() + " joined the room.");
            notificationService.sendLobby(user.getUsername() + " joined the room" + requestDto.getRoomId() + ".");
            if (joined == 1) {
                notificationService.sendUser(user.getSessionId(), "다른 플레이어를 기다리는 중..");
                notificationService.sendUser(user.getSessionId(), "플레이어가 2명이 되면 자동으로 게임이 시작됩니다.");
            } else if (joined == 2) {
                notificationService.sendUser(user.getSessionId(), "게임을 시작합니다.");
                notificationService.sendRoom(requestDto.getRoomId(), "게임을 시작합니다.");
            }
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
