package proj.yachoo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionInfoDto {
    private String username;
    private int totalRooms;
    private int[] roomStatuses;
}