package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.Room.RoomStatus;
import proj.yachoo.domain.User;
import proj.yachoo.dto.request.JoinRoomDto;
import proj.yachoo.dto.request.RoomSubscribedDto;
import proj.yachoo.dto.response.GameRoundInfoDto;
import proj.yachoo.dto.response.RoomListDto;
import proj.yachoo.service.LobbyService;
import proj.yachoo.service.NotificationService;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.game.GameService;

@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final LobbyService lobbyService;
    private final NotificationService notificationService;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/join")
    public void handleJoinRoom(JoinRoomDto requestDto, StompHeaderAccessor headerAccessor) {
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
            notificationService.roomList();
            notificationService.sendLobby(user.getUsername() + " joined the room" + requestDto.getRoomId() + ".");
        }
    }


    @MessageMapping("/room/subscribed")
    public void handleRoomSubscribed(RoomSubscribedDto subscribedDto, StompHeaderAccessor headerAccessor) {
        User user = (User) headerAccessor.getSessionAttributes().get("user");
        int roomId = subscribedDto.getRoomId();

        if (user != null && user.getRoomId() != null && user.getRoomId() == roomId) {
            notificationService.sendRoom(roomId, user.getUsername() + " joined the room.");
            Room room = roomService.getRoomById(roomId);

            if (room != null) {
                if (room.getStatus() == RoomStatus.STANDBY) {
                    notificationService.sendRoom(roomId, "다른 플레이어를 기다리는 중..");
                    notificationService.sendRoom(roomId, "플레이어가 2명이 되면 자동으로 게임이 시작됩니다.");

                } else if (room.getStatus() == RoomStatus.FULL) {
                    gameService.createGame(roomId);
                    messagingTemplate.convertAndSend(
                            "/topic/game/" + roomId + "/start",
                            true
                    );
                    notificationService.sendRoom(roomId, "게임을 시작합니다.");
                }
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
