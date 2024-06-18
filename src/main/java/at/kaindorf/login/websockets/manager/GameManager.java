package at.kaindorf.login.websockets.manager;

import at.kaindorf.login.websockets.pojos.Game;
import at.kaindorf.login.websockets.pojos.GameState;
import at.kaindorf.login.websockets.pojos.Player;
import at.kaindorf.login.websockets.pojos.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GameManager {

    private final Map<String, Game> games;
    protected final Map<Player, String> waitingPlayers;


    public GameManager() {
        games = new ConcurrentHashMap<>();
        waitingPlayers = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to add a player to an existing Tic-Tac-Toe game, or creates a new game if no open games are available.
     *
     * @param player the name of the player
     * @return the Tic-Tac-Toe game the player was added to
     */
    public synchronized Game joinGame(Player player) {
        if (games.values().stream().anyMatch(game -> game.getPlayer1().getName().equals(player.getName()) || (game.getPlayer2() != null && game.getPlayer2().getName().equals(player.getName())))) {
            return games.values().stream().filter(game -> game.getPlayer1().getName().equals(player.getName()) || game.getPlayer2().getName().equals(player.getName())).findFirst().get();
        }

        player.setPoints(0);

        for (Game game : games.values()) {
            if (game.getPlayer1() != null && game.getPlayer2() == null) {
                player.setPos(new Position(500,550));
                game.setPlayer2(player);
                game.setGameState(GameState.PLAYING);
                return game;
            }
        }

        player.setPos(new Position(100,550));
        Game game = new Game(player, null);
        game.setGameState(GameState.WAITING_FOR_PLAYER);
        games.put(game.getGameId(), game);
        waitingPlayers.put(player, game.getGameId());


        return game;
    }

    /**
     * Removes a player from their Tic-Tac-Toe game. If the player was the only player in the game,
     * the game is removed.
     *
     * @param player the name of the player
     */
    public synchronized Game leaveGame(Player player) {
        String gameId = getGameByPlayer(player.getName()) != null ? getGameByPlayer(player.getName()).getGameId() : null;
        if (gameId != null) {
            waitingPlayers.remove(player);
            Game game = games.get(gameId);
            if (player.equals(game.getPlayer1())) {
                if (game.getPlayer2() != null) {
                    game.setPlayer1(game.getPlayer2());
                    game.setPlayer2(null);
                    game.setGameState(GameState.WAITING_FOR_PLAYER);
                    waitingPlayers.put(game.getPlayer1(), game.getGameId());
                } else {
                    games.remove(gameId);
                    return null;
                }
            } else if (player.equals(game.getPlayer2())) {
                game.setPlayer2(null);
                game.setGameState(GameState.WAITING_FOR_PLAYER);
                waitingPlayers.put(game.getPlayer1(), game.getGameId());
            }
            return game;
        }
        return null;
    }

    /**
     * Returns the Tic-Tac-Toe game with the given game ID.
     *
     * @param gameId the ID of the game
     * @return the Tic-Tac-Toe game with the given game ID, or null if no such game exists
     */
    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    /**
     * Returns the Tic-Tac-Toe game the given player is in.
     *
     * @param player the name of the player
     * @return the Tic-Tac-Toe game the given player is in, or null if the player is not in a game
     */
    public Game getGameByPlayer(String player) {
        return games.values().stream().filter(game -> game.getPlayer1().equals(player) || (game.getPlayer2() != null &&
                game.getPlayer2().equals(player))).findFirst().orElse(null);
    }

    /**
     * Removes the Tic-Tac-Toe game with the given game ID.
     *
     * @param gameId the ID of the game to remove
     */
    public void removeGame(String gameId) {
        games.remove(gameId);
    }
}
