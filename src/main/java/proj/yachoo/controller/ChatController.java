package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.User;
import proj.yachoo.dto.response.ChatMessageDto;
import proj.yachoo.service.UserService;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessageDto chatMessageDto) {
        User user = userService.findByUsername(chatMessageDto.getUsername());
        if (user != null) {
            if (user.isInLobby()) {
                sendToLobby(chatMessageDto);
            } else if (user.isInRoom()) {
                sendToRoom(user.getRoomId(), chatMessageDto);
            }
        }
    }

    private void sendToLobby(ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/lobby/chat", chatMessageDto);
    }

    private void sendToRoom(int roomId, ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", chatMessageDto);
    }
}
