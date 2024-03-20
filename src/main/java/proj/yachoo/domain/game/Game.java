package proj.yachoo.domain.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import proj.yachoo.domain.User;

@Getter
public class Game {
    private static final int NUM_DICE = 5;
    private static final int NUM_ROUNDS = 13;

    private String id;
    private List<User> players;
    private int currentPlayerIndex;
    private int currentRound;
    private int[] dice;
    private boolean[] keptDice;
    private Map<User, Map<Category, Integer>> categories;
    private Map<User, Integer> scores;
    private boolean isEnded;

    public Game(String id, List<User> players) {
        this.id = id;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentRound = 1;
        this.dice = new int[NUM_DICE];
        this.keptDice = new boolean[NUM_DICE];
        this.categories = new HashMap<>();
        this.scores = new HashMap<>();
        this.isEnded = false;

        for (User player : players) {
            categories.put(player, new HashMap<>());
            scores.put(player, 0);
        }
    }

    public void rollDice() {
        Random random = new Random();
        for (int i = 0; i < NUM_DICE; i++) {
            if (!keptDice[i]) {
                dice[i] = random.nextInt(6) + 1;
            }
        }
    }

    public void keepDice(int[] indexes) {
        for (int index : indexes) {
            if (index >= 0 && index < NUM_DICE) {
                keptDice[index] = true;
            }
        }
    }

    public void resetKeptDice() {
        for (int i = 0; i < NUM_DICE; i++) {
            keptDice[i] = false;
        }
    }

    public void chooseCategory(User player, Category category) {
        int score = calculateScore(player, category);
        updateScore(player, category, score);
        nextPlayer();
    }

    private int calculateScore(User player, Category category) {
        int score = 0;
        switch (category) {
            case ONES:
                score = calculateNumberScore(1);
                break;
            case TWOS:
                score = calculateNumberScore(2);
                break;
            case THREES:
                score = calculateNumberScore(3);
                break;
            case FOURS:
                score = calculateNumberScore(4);
                break;
            case FIVES:
                score = calculateNumberScore(5);
                break;
            case SIXES:
                score = calculateNumberScore(6);
                break;
            case THREE_OF_A_KIND:
                score = calculateOfAKindScore(3);
                break;
            case FOUR_OF_A_KIND:
                score = calculateOfAKindScore(4);
                break;
            case FULL_HOUSE:
                score = calculateFullHouseScore();
                break;
            case SMALL_STRAIGHT:
                score = calculateStraightScore(4);
                break;
            case LARGE_STRAIGHT:
                score = calculateStraightScore(5);
                break;
            case YACHT:
                score = calculateYachtScore();
                break;
            case CHANCE:
                score = calculateChanceScore();
                break;
        }
        return score;
    }

    private int calculateNumberScore(int number) {
        int score = 0;
        for (int value : dice) {
            if (value == number) {
                score += value;
            }
        }
        return score;
    }

    private int calculateOfAKindScore(int ofAKind) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int value : dice) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        for (int frequency : frequencyMap.values()) {
            if (frequency >= ofAKind) {
                return sumDice();
            }
        }
        return 0;
    }

    private int calculateFullHouseScore() {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int value : dice) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        boolean hasThree = false;
        boolean hasTwo = false;
        for (int frequency : frequencyMap.values()) {
            if (frequency == 3) {
                hasThree = true;
            } else if (frequency == 2) {
                hasTwo = true;
            }
        }
        return (hasThree && hasTwo) ? 25 : 0;
    }

    private int calculateStraightScore(int length) {
        Map<Integer, Boolean> valueMap = new HashMap<>();
        for (int value : dice) {
            valueMap.put(value, true);
        }
        int maxLength = 0;
        int currentLength = 0;
        for (int i = 1; i <= 6; i++) {
            if (valueMap.containsKey(i)) {
                currentLength++;
                maxLength = Math.max(maxLength, currentLength);
            } else {
                currentLength = 0;
            }
        }
        return (maxLength >= length) ? (length == 4 ? 30 : 40) : 0;
    }

    private int calculateYachtScore() {
        int firstValue = dice[0];
        for (int i = 1; i < NUM_DICE; i++) {
            if (dice[i] != firstValue) {
                return 0;
            }
        }
        return 50;
    }

    private int calculateChanceScore() {
        return sumDice();
    }

    private int sumDice() {
        int sum = 0;
        for (int value : dice) {
            sum += value;
        }
        return sum;
    }

    private void updateScore(User player, Category category, int score) {
        categories.get(player).put(category, score);
        scores.put(player, scores.get(player) + score);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            nextRound();
        }
    }

    private void nextRound() {
        currentRound++;
        if (currentRound > NUM_ROUNDS) {
            endGame();
        }
    }

    private void endGame() {
        isEnded = true;
    }

    public User getWinner() {
        int maxScore = 0;
        User winner = null;
        for (User player : players) {
            int score = scores.get(player);
            if (score > maxScore) {
                maxScore = score;
                winner = player;
            }
        }
        return winner;
    }

    public enum Category {
        ONES, TWOS, THREES, FOURS, FIVES, SIXES,
        THREE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE,
        SMALL_STRAIGHT, LARGE_STRAIGHT, YACHT, CHANCE
    }
}