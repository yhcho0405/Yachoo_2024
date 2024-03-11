package proj.yachoo.dto.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@JsonSerialize
@JsonDeserialize
public class InitialRegistrationMessage {
    private int userId;
    private int rooms;
    private List<Integer> visitors;
}
