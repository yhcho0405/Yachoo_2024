package proj.yachoo.service.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.User;
import proj.yachoo.domain.game.Game;
import proj.yachoo.domain.game.Game.Category;
import proj.yachoo.repository.RoomRepository;

class GameServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private GameService gameService;

    @Mock
    private Room room;

    @Mock
    private User user1;

    @Mock
    private User user2;

    @Mock
    private Game game;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 게임_생성_테스트() {
        List<User> users = Arrays.asList(user1, user2);
        when(room.getUsers()).thenReturn(users);

        gameService.createGame(room.getId());

        verify(room).setGame(any(Game.class));
        verify(roomRepository).save(room);
    }

    @Test
    void getGame_테스트() {
        when(roomRepository.findById(1)).thenReturn(room);
        when(room.getGame()).thenReturn(game);

        Game retrievedGame = gameService.getGame(1);

        assertNotNull(retrievedGame);
        assertEquals(game, retrievedGame);
    }

    @Test
    void 주사위_굴리기_테스트() {
        when(roomRepository.findById(1)).thenReturn(room);
        when(room.getGame()).thenReturn(game);

        gameService.rollDice(1);

        verify(game).rollDice();
    }

    @Test
    void 주사위_킵_테스트() {
        when(roomRepository.findById(1)).thenReturn(room);
        when(room.getGame()).thenReturn(game);

        int[] indexes = {0, 2, 4};
        gameService.keepDice(1, indexes);

        verify(game).keepDice(indexes);
    }

    @Test
    void 족보_선택_테스트() {
        when(roomRepository.findById(1)).thenReturn(room);
        when(room.getGame()).thenReturn(game);

        gameService.chooseCategory(1, user1, Category.ONES);

        verify(game).chooseCategory(user1, Category.ONES);
    }

    @Test
    void 게임_종료_테스트() {
        when(roomRepository.findById(1)).thenReturn(room);
        when(room.getGame()).thenReturn(game);
        when(game.isEnded()).thenReturn(true);

        gameService.chooseCategory(1, user1, Category.ONES);

        verify(room).setGame(null);
        verify(roomRepository).save(room);
    }
}