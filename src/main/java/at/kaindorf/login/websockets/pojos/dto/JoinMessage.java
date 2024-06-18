package at.kaindorf.login.websockets.pojos.dto;

import at.kaindorf.login.websockets.pojos.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JoinMessage{
    private String type;
    private String gameId;
    private Player player;
    private String content;

}
