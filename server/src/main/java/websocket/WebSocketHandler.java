package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
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
            ErrorMessage errorMessage = new ErrorMessage("Invalid gameID");
            String errorJson = new Gson().toJson(errorMessage);
            session.getRemote().sendString(errorJson);
        }
        else if (auth == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid authToken");
            String errorJson = new Gson().toJson(errorMessage);
            session.getRemote().sendString(errorJson);
        }
        else {
            switch (cmd.getCommandType()) {
                case CONNECT -> connectUser(session, auth, game);
                case LEAVE -> leave(session, auth, game);
                case RESIGN -> resign(session, auth, game);
            }
        }
    }

    private void connectUser(Session session, AuthData auth, GameData game) throws IOException {
        connections.addSession(game.gameID(), session);

        LoadMessage loadMessage = new LoadMessage(game.game());
        String loadJson = new Gson().toJson(loadMessage);
        session.getRemote().sendString(loadJson); // FIXME Error on Normal Connect test

        String notification = String.format("%s is now %s", auth.username(), getPosition(auth, game));
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        String notifyJson = new Gson().toJson(notificationMessage);
        connections.broadcast(session, notifyJson);

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
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        String jsonMsg = new Gson().toJson(notificationMessage);
        connections.broadcast(session, jsonMsg);
    }

    private void resign(Session session, AuthData auth, GameData game) throws IOException {

    }
}
