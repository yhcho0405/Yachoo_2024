package proj.yachoo.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionInfoDto {
    private String username;
    private int totalRooms;
    private int[] roomStatuses;
}