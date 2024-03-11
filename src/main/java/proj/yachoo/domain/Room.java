package proj.yachoo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import proj.yachoo.domain.User.Status;

@Getter @Setter
@AllArgsConstructor
public class Room {

    @Getter
    public enum Status {
        EMPTY(0), STANDBY(1), FULL(2);

        private final int value;

        Status(int value) {
            this.value = value;
        }

    }

    private int id;
    private Status status;
}
