package proj.yachoo.service.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.User;
import proj.yachoo.domain.game.Game;
import proj.yachoo.domain.game.Game.Category;
import proj.yachoo.repository.RoomRepository;

@Service
@RequiredArgsConstructor
public class GameService {
    private final RoomRepository roomRepository;

    public void createGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        Game game = new Game(room.getUsers());
        room.setGame(game);
    }

    public Game getGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        if (room != null) {
            return room.getGame();
        }
        return null;
    }

    public User getCurrentPlayer(int roomId) {
        Game game = getGame(roomId);
        if (game != null) {
            return game.getCurrentPlayer();
        }
        return null;
    }

    public void rollDice(int roomId) {
        Game game = getGame(roomId);
        if (game != null) {
            game.rollDice();
        }
    }

    public void keepDice(int roomId, int[] indexes) {
        Game game = getGame(roomId);
        if (game != null) {
            game.keepDice(indexes);
        }
    }

    public void chooseCategory(int roomId, User player, Category category) {
        Game game = getGame(roomId);
        if (game != null) {
            game.chooseCategory(player, category);
            if (game.isEnded()) {
                endGame(roomId);
            }
        }
    }

    private void endGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        if (room != null) {
            Game game = room.getGame();
            if (game != null) {
                User winner = game.getWinner();
                // TODO: 승자, 점수 noti
                room.setGame(null);
                roomRepository.save(room);
            }
        }
    }
}