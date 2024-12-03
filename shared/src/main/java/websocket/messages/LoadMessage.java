package websocket.messages;

import chess.ChessGame;

public class LoadMessage extends ServerMessage{
    private ChessGame gameToLoad;

    public LoadMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        gameToLoad = game;
    }

    public ChessGame getGameToLoad() { return gameToLoad; }
}
