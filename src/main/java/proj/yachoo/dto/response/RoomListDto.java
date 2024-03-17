package proj.yachoo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomListDto {
    private int totalRooms;
    private int[] roomStatuses;

}
