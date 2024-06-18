package at.kaindorf.login.websockets.pojos.dto;

import at.kaindorf.login.websockets.pojos.Player;
import lombok.Data;

@Data
public class PlayerMessage implements Message {
    private String type;
    private String gameId;
    private Player player;
    private String content;
}
