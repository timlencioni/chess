package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public final ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    public void addSession(int gameID, Session session) {
        sessions.replace(gameID, session);
    }

    public void broadcast(Session excludedSession, String message) throws IOException {
        var removeList = new ArrayList<Integer>();
        for (var gameID : sessions.keySet()) {
            Session session = sessions.get(gameID);
            if (session.isOpen()) {
                if (!session.equals(excludedSession)) {
                    session.getRemote().sendString(message);
                }
            } else {
                removeList.add(gameID);
            }
        }

        // Clean up any connections that were left open.
        for (var gameID : removeList) {
            sessions.remove(gameID);
        }
    }
}
