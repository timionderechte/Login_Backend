package at.kaindorf.login.websockets.pojos;

public enum GameState {
    WAITING_FOR_PLAYER("Waiting for player."),
    PLAYER1_WON("Player 1 won."),
    PLAYER2_WON("Player 2 won."),
    TIE("Tie."),
    PLAYING("Match is ongoing");

    String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
