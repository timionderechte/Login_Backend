package at.kaindorf.login.websockets.pojos.dto;

import at.kaindorf.login.websockets.pojos.Game;
import at.kaindorf.login.websockets.pojos.GameState;
import at.kaindorf.login.websockets.pojos.Player;
import at.kaindorf.login.websockets.pojos.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameMessage implements Message {
    private String type;
    private String gameId;
    private Player player1;
    private Player player2;
    private Player winner;
    private String content;
    private Position move;
    private GameState gameState;
    private String sender;
    private Integer points;


    public GameMessage(Game game) {
        this.gameId = game.getGameId();
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.winner = game.getWinner();
        this.gameState = game.getGameState();
    }
}
