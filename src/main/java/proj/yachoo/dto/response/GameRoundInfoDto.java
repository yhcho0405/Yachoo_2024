package proj.yachoo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameRoundInfoDto {
    private int round;
    private String name1;
    private String name2;
    private String currentPlayer;
}
