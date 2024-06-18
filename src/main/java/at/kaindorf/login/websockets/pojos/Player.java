package at.kaindorf.login.websockets.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Player {
    private String name;
    private Position pos;
    private Integer points;
}