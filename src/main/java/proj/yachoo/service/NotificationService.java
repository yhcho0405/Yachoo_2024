package proj.yachoo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import proj.yachoo.dto.response.NotificationDto;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String SERVER_MESSAGE_FORMAT = "[Server] %s";
    private static final String SYSTEM_MESSAGE_FORMAT = "[System] %s";
    private static final String ROOM_MESSAGE_FORMAT = "[Room%d] %s";

    private final SimpMessagingTemplate messagingTemplate;

    public void sendGlobal(String message) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new NotificationDto(
                        String.format(SERVER_MESSAGE_FORMAT, message)
                )
        );
    }

    public void sendLobby(String message) {
        messagingTemplate.convertAndSend(
                "/topic/lobby/notifications",
                new NotificationDto(
                        String.format(SERVER_MESSAGE_FORMAT, message)
                )
        );
    }

    public void sendRoom(int roomId, String message) {
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/notifications",
                new NotificationDto(
                        String.format(ROOM_MESSAGE_FORMAT, roomId, message)
                )
        );
    }

    public void sendUser(String sessionId, String message) {
        messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/notifications",
                new NotificationDto(
                        String.format(SYSTEM_MESSAGE_FORMAT, message)
                ),
                createHeaders(sessionId)
        );
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        return headerAccessor.getMessageHeaders();
    }

}