package proj.yachoo.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageDto {
    private String username;
    private String message;
}
