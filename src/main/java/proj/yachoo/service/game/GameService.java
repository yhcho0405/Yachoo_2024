package proj.yachoo.service.game;

import org.springframework.stereotype.Service;
import proj.yachoo.domain.Room;
import proj.yachoo.domain.User;
import proj.yachoo.domain.game.Game;
import proj.yachoo.domain.game.Game.Category;
import proj.yachoo.repository.RoomRepository;

@Service
public class GameService {
    private final RoomRepository roomRepository;

    public GameService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void createGame(Room room) {
        Game game = new Game(room.getUsers());
        room.setGame(game);
        roomRepository.save(room);
    }

    public Game getGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        if (room != null) {
            return room.getGame();
        }
        return null;
    }

    public void startGame(int roomId) {
        Game game = getGame(roomId);
        if (game != null) {
            game.rollDice();
        }
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
                // Perform any necessary actions when the game ends
                // e.g., update player stats, send notifications, etc.
                room.setGame(null);
                roomRepository.save(room);
            }
        }
    }
}