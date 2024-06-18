package at.kaindorf.login.websockets.controller;

import at.kaindorf.login.websockets.manager.GameManager;
import at.kaindorf.login.websockets.pojos.Game;
import at.kaindorf.login.websockets.pojos.GameState;
import at.kaindorf.login.websockets.pojos.Position;
import at.kaindorf.login.websockets.pojos.dto.GameMessage;
import at.kaindorf.login.websockets.pojos.dto.JoinMessage;
import at.kaindorf.login.websockets.pojos.dto.PlayerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameManager gameManager = new GameManager();
    private final Random random = new Random();

    @MessageMapping("/game.join")
    @SendTo("/topic/game.state")
    public Object joinGame(@Payload JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {

        Game game = gameManager.joinGame(message.getPlayer());

        if (game == null) {
            GameMessage errorMessage = new GameMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Não foi possível entrar no jogo. Talvez o jogo já esteja cheio ou ocorreu um erro interno.");
            return errorMessage;
        }


        headerAccessor.getSessionAttributes().put("gameId", game.getGameId());
        headerAccessor.getSessionAttributes().put("player", message.getPlayer());

        GameMessage gameMessage = gameToMessage(game);
        gameMessage.setType("game.joined");
        return gameMessage;
    }

    
    @MessageMapping("/game.leave")
    public void leaveGame(@Payload PlayerMessage message) {
        Game game = gameManager.leaveGame(message.getPlayer());
        if (game != null) {
            GameMessage gameMessage = gameToMessage(game);
            gameMessage.setType("game.left");
            messagingTemplate.convertAndSend("/topic/game." + game.getGameId(), gameMessage);
        }
    }

    
    @MessageMapping("/game.move")
    public void makeMove(@Payload GameMessage message) {

        String player = message.getSender();
        String gameId = message.getGameId();
        Position move = message.getMove();
        Game game = gameManager.getGame(gameId);


        if (game.getGameState().equals(GameState.WAITING_FOR_PLAYER)) {
            GameMessage errorMessage = new GameMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game is waiting for another player to join.");
            messagingTemplate.convertAndSend("/topic/game.state" , errorMessage);
        }else{
            game.makeMove(player, move);

            GameMessage gameStateMessage = new GameMessage(game);
            gameStateMessage.setType("game.move");

            messagingTemplate.convertAndSend("/topic/game.state" , gameStateMessage);
        }
    }

    @MessageMapping("/game.points")
    public void addPoint(@Payload GameMessage message) {
        String player = message.getSender();
        String gameId = message.getGameId();
        Integer points = message.getPoints();
        Game game = gameManager.getGame(gameId);

        game.setPoints(player,points);

        GameMessage gameStateMessage = new GameMessage(game);
        gameStateMessage.setType("game.points");

        messagingTemplate.convertAndSend("/topic/game.state" , gameStateMessage);
    }

    @EventListener
    public void SessionDisconnectEvent(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
            System.out.println(sessionAttributes);

            String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
            String player = headerAccessor.getSessionAttributes().get("player").toString();
            Game game = gameManager.getGame(gameId);
            if (game != null) {
                if (game.getPlayer1().getName().equals(player)) {
                    game.setPlayer1(null);
                    if (game.getPlayer2() != null) {
                        game.setGameState(GameState.PLAYER2_WON);
                        game.setWinner(game.getPlayer2());
                    } else {
                        gameManager.removeGame(gameId);
                    }
                } else if (game.getPlayer2() != null && game.getPlayer2().equals(player)) {
                    game.setPlayer2(null);
                    if (game.getPlayer1() != null) {
                        game.setGameState(GameState.PLAYER1_WON);
                        game.setWinner(game.getPlayer1());
                    } else {
                        gameManager.removeGame(gameId);
                    }
                }
                GameMessage gameMessage = gameToMessage(game);
                gameMessage.setType("game.gameOver");
                messagingTemplate.convertAndSend("/topic/game.state" , gameMessage);
                gameManager.removeGame(gameId);
            }
        }
    }

    @Scheduled(fixedRate = 250)
    public void sendRandomNumbersLeft() {
        int randomNumber = random.nextInt(7);

        messagingTemplate.convertAndSend("/topic/numbersleft", randomNumber);
    }

    @Scheduled(fixedRate = 250)
    public void sendRandomNumbersRight() {
        int randomNumber = random.nextInt(7);

        messagingTemplate.convertAndSend("/topic/numbersright", randomNumber);
    }

    @Scheduled(fixedRate = 1000)
    public void timer() {
        messagingTemplate.convertAndSend("/topic/timer", 1);
    }


    private GameMessage gameToMessage(Game game) {
        GameMessage message = new GameMessage();
        message.setGameId(game.getGameId());
        message.setPlayer1(game.getPlayer1());
        message.setPlayer2(game.getPlayer2());
        message.setGameState(game.getGameState());
        message.setWinner(game.getWinner());
        return message;
    }
}

