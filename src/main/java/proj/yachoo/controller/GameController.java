package proj.yachoo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import proj.yachoo.domain.User;
import proj.yachoo.dto.response.GameRoundInfoDto;
import proj.yachoo.dto.response.RoomListDto;
import proj.yachoo.service.NotificationService;
import proj.yachoo.service.RoomService;
import proj.yachoo.service.game.GameService;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final RoomService roomService;
    private final GameService gameService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/start")
    public void gameStartHandler(StompHeaderAccessor headerAccessor) {
        User user = (User)headerAccessor.getSessionAttributes().get("user");
        int roomId = user.getRoomId();
//        Room room = roomService.getRoomById(user.getRoomId());

        messagingTemplate.convertAndSend(
                "/topic/game/" + roomId + "/round",
                new GameRoundInfoDto(
                        gameService.getGame(roomId).getCurrentRound(),
                        gameService.getGame(roomId).getPlayers().get(0).getUsername(),
                        gameService.getGame(roomId).getPlayers().get(1).getUsername(),
                        gameService.getCurrentPlayer(roomId).getUsername()
                )
        );
    }

}
