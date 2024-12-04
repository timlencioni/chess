package websocket;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final SessionManager connections = new SessionManager();

    @OnWebSocketConnect
    public void connect(Session session) {
        connections.addSession(0, session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        UserData user = Server.userDAO.getUser(cmd.getAuthToken());
        GameData game = Server.gameDAO.getGame(cmd.getGameID());
        switch (cmd.getCommandType()) {
            case CONNECT -> connectUser(session, user, game);
            case LEAVE -> doNothingForNow();
        }
    }

    private void connectUser(Session session, UserData user, GameData game) throws IOException {
        connections.addSession(game.gameID(), session);

        LoadMessage loadMessage = new LoadMessage(game.game());
        String jsonMsg = new Gson().toJson(loadMessage);
        session.getRemote().sendString(jsonMsg);

        String notification = String.format("%s is now %s", user.username(), getPosition(user, game));
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        jsonMsg = new Gson().toJson(notificationMessage);
        connections.broadcast(session, jsonMsg);

    }

    private Object getPosition(UserData user, GameData game) {
        if (game.whiteUsername().equals(user.username())) { return "playing white."; }
        else if (game.blackUsername().equals(user.username())) { return "playing black."; }
        else { return "observing."; }
    }

    private void doNothingForNow() {}

}
