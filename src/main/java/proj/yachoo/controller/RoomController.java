package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.User;
import proj.yachoo.dto.request.JoinRoomRequestDto;
import proj.yachoo.service.RoomService;

@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/join")
    @SendToUser("/queue/room/join")
    public void handleJoinRoom(JoinRoomRequestDto requestDto, StompHeaderAccessor headerAccessor) {
        User user = (User)headerAccessor.getSessionAttributes().get("user");
        boolean joined = roomService.joinRoom(user, requestDto.getRoomId());

        if (joined) {
            messagingTemplate.convertAndSendToUser(
                    user.getSessionId(), "/queue/room/join",
                    requestDto.getRoomId(),
                    createHeaders(user.getSessionId())
            );
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