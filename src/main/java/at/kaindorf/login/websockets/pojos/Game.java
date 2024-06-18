package at.kaindorf.login.websockets.pojos;

import lombok.Data;

import java.util.UUID;

@Data
public class Game {
    private String gameId;
    private Player player1;
    private Player player2;
    private Player winner;
    private GameState gameState;

    public Game(Player player1, Player player2) {
        this.gameId = UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
    }

    public Position makeMove(String player, Position move) {
        if (player.equals(player1.getName())) {
            player1.setPos(move);
            return player1.getPos();
        } else {
            player2.setPos(move);
            return player2.getPos();
        }
    }

    public void setPoints(String player, int points) {
        if (player.equals(player1.getName())) {
            player1.setPoints(points);
        } else {
            player2.setPoints(points);
        }
    }
}
