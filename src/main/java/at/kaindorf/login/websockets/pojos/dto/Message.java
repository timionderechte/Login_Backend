package at.kaindorf.login.websockets.pojos.dto;

public interface Message {
    String getType();
    String getGameId();
    String getContent();
}
