package proj.yachoo.domain.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import proj.yachoo.domain.User;
import proj.yachoo.domain.game.Game.Category;

class GameTest {
    private Game game;

    @Mock
    private User user1;

    @Mock
    private User user2;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        List<User> players = Arrays.asList(user1, user2);
        game = new Game(players);
    }

    @Test
    void 주사위_굴리기_테스트() {
        game.rollDice();
        int[] dice = game.getDice();
        assertEquals(5, dice.length);
        for (int value : dice) {
            assertTrue(value >= 1 && value <= 6);
        }
    }

    @Test
    void 주사위_킵_테스트() {
        int[] indexes = {0, 2, 4};
        game.keepDice(indexes);
        boolean[] keptDice = game.getKeptDice();
        assertTrue(keptDice[0]);
        assertFalse(keptDice[1]);
        assertTrue(keptDice[2]);
        assertFalse(keptDice[3]);
        assertTrue(keptDice[4]);
    }

    @Test
    void 킵_주사위_초기화_테스트() {
        int[] indexes = {0, 2, 4};
        game.keepDice(indexes);
        game.resetKeptDice();
        boolean[] keptDice = game.getKeptDice();
        for (boolean kept : keptDice) {
            assertFalse(kept);
        }
    }

    @Test
    void 족보_선택_테스트() {
        game.setDice(new int[]{1, 2, 3, 4, 5});
        game.chooseCategory(user1, Category.SMALL_STRAIGHT);
        assertEquals(1, game.getCurrentPlayerIndex());
        assertEquals(30, game.getScores().get(user1));
    }

    @Test
    void 턴_넘기기_테스트() {
        game.nextPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());
        game.nextPlayer();
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(2, game.getCurrentRound());
    }

    @Test
    void 승자_계산_테스트() {
        game.getScores().put(user1, 100);
        game.getScores().put(user2, 150);
        assertEquals(user2, game.getWinner());
    }

    @Test
    void 게임_종료_테스트() {
        for (int i = 0; i < 26; i++)
            game.nextPlayer();
        assertTrue(game.isEnded());
    }
}