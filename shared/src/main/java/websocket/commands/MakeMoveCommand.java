package websocket.commands;

import chess.ChessMove;

/*
This is the User Command which needs to take in an additional argument.
All others will be able to use the UserGameCommand straight from the supplied class.
 */

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove moveToMake;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        moveToMake = move;
    }

    public ChessMove getMoveToMake() { return moveToMake; }
}
