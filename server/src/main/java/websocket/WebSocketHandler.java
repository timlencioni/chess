package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final SessionManager connections = new SessionManager();
    private SqlGameDAO gameDAO;

    public void setDAO(SqlGameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        AuthData auth = Server.authDAO.getAuth(cmd.getAuthToken());
        GameData game = Server.gameDAO.getGame(cmd.getGameID());
        if (game == null) {
            sendError(session, "Invalid gameID");
        }
        else if (auth == null) {
            sendError(session, "Invalid authToken");
        }
        else {
            switch (cmd.getCommandType()) {
                case CONNECT -> connectUser(session, auth, game);
                case LEAVE -> leave(session, auth, game);
                case RESIGN -> resign(session, auth, game);
                case MAKE_MOVE -> makeMove(session, auth, game, (MakeMoveCommand) cmd);
            }
        }
    }

    private void sendError(Session session, String msg) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(msg);
        String errorJson = new Gson().toJson(errorMessage);
        session.getRemote().sendString(errorJson);
    }

    private void broadcastNotification(Session session, String notification) throws IOException {
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        String notifyJson = new Gson().toJson(notificationMessage);
        connections.broadcast(session, notifyJson);
    }

    private void sendLoad(Session session, ChessGame game) throws IOException {
        LoadMessage loadMessage = new LoadMessage(game);
        String loadJson = new Gson().toJson(loadMessage);
        session.getRemote().sendString(loadJson);
    }

    private void broadcastLoad(ChessGame game) throws IOException{
        LoadMessage loadMessage = new LoadMessage(game);
        String loadJson = new Gson().toJson(loadMessage);
        connections.broadcast(null, loadJson);
    }

    private void connectUser(Session session, AuthData auth, GameData game) throws IOException {
        connections.addSession(game.gameID(), session);

        sendLoad(session, game.game());

        String notification = String.format("%s is now %s", auth.username(), getPosition(auth, game));
        broadcastNotification(session, notification);

    }

    private Object getPosition(AuthData auth, GameData game) {
        if (game.whiteUsername().equals(auth.username())) { return "playing white."; }
        else if (game.blackUsername().equals(auth.username())) { return "playing black."; }
        else { return "observing."; }
    }

    private void leave(Session session, AuthData auth, GameData game) throws IOException {
        if (auth.username().equals(game.whiteUsername())) { gameDAO.removePlayer(game.gameID(), "WHITE"); }
        else { gameDAO.removePlayer(game.gameID(), "BLACK"); }

        connections.removeSession(game.gameID(), session);
        String notification = String.format("%s left the game", auth.username());
        broadcastNotification(session, notification);
    }

    private void resign(Session session, AuthData auth, GameData game) throws IOException {
        if (game.game().isGameOver()) {
            sendError(session, "Game is already over.");
            return;
        }

        ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
        if (userColor == null) {
            sendError(session, "You cannot resign if not playing...");
            return;
        }

        String otherUsername = null;
        if (userColor.equals(ChessGame.TeamColor.WHITE)) { otherUsername = game.blackUsername(); }
        else if (userColor.equals(ChessGame.TeamColor.BLACK)) { otherUsername = game.whiteUsername(); }

        try {
            game.game().setGameOver(true);
            gameDAO.updateGame(game);
        }
        catch (DataAccessException e) {
            sendError(session, "Cannot update game server");
        }

        String notification = String.format("%s has resigned. %s has won!", auth.username(), otherUsername);
        broadcastNotification(null, notification);

    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.blackUsername())) { return ChessGame.TeamColor.BLACK; }
        else if (username.equals(game.whiteUsername())) { return ChessGame.TeamColor.WHITE; }
        else { return null; }
    }

    private void makeMove(Session session, AuthData auth, GameData game, MakeMoveCommand command) throws IOException {
        if (game.game().isGameOver()) {
            sendError(session, "Game over, you cannot move.");
            return;
        }

        ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
        if (userColor == null) {
            sendError(session, "You cannot move as an observer!");
            return;
        }

        if (!userColor.equals(game.game().getTeamTurn())) {
            sendError(session, "Not your turn yet!");
            return;
        }

        String otherUsername = null;
        if (userColor.equals(ChessGame.TeamColor.WHITE)) { otherUsername = game.blackUsername(); }
        else if (userColor.equals(ChessGame.TeamColor.BLACK)) { otherUsername = game.whiteUsername(); }

        ChessGame.TeamColor oppColor = null;
        if (userColor.equals(ChessGame.TeamColor.WHITE)) { oppColor = ChessGame.TeamColor.BLACK; }
        else if (userColor.equals(ChessGame.TeamColor.BLACK)) { oppColor = ChessGame.TeamColor.WHITE; }

        ChessMove move = command.getMoveToMake();

        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException e) {
            sendError(session, e.getMessage());
        }

        if (game.game().isInCheckmate(oppColor)) {
            broadcastNotification(null, String.format("Checkmate! %s Wins!", auth.username()));
            game.game().setGameOver(true);
        }
        else if (game.game().isInStalemate(oppColor)) {
            broadcastNotification(null, "It's a Stalemate! No available moves.");
            game.game().setGameOver(true);
        }
        else if (game.game().isInCheck(oppColor)) {
            broadcastNotification(null, String.format("Check! %s's King is in Danger!", otherUsername));
        }
        else {
            broadcastNotification(session, String.format("%s made move %s", auth.username(), move));
        }

        try {
            gameDAO.updateGame(game);
        } catch (DataAccessException e) {
            sendError(session, "Could not execute move");
        }

        broadcastLoad(game.game());
    }

}
