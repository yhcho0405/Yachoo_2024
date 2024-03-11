package proj.yachoo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class User {

    public enum Status {
        ONLINE, OFFLINE
    }

    private int id;
    private Status status;
}
