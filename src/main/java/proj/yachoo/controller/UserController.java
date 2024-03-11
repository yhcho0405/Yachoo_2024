package proj.yachoo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.User;
import proj.yachoo.dto.message.InitialRegistrationMessage;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoomService roomService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public InitialRegistrationMessage addUser(StompHeaderAccessor headerAccessor) {
        User user = new User();
        userService.saveUser(user);
        InitialRegistrationMessage message = InitialRegistrationMessage.builder()
                .userId(user.getId())
                .rooms(roomService.activateRooms())
                .visitors(roomService.roomsStatus())
                .build();
        return message;
    }

}
