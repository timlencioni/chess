package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public final ConcurrentHashMap<Integer, HashSet<Session>> sessions = new ConcurrentHashMap<>();

    public void addSession(int gameID, Session session) {
        HashSet<Session> sessionSet;
        if (sessions.containsKey(gameID)) {
            sessionSet = sessions.get(gameID);
        }
        else {
            sessionSet = new HashSet<>();
        }
        sessionSet.add(session);
        sessions.put(gameID, sessionSet);

    }

    public void removeSession(int gameID, Session session) {
        if (sessions.containsKey(gameID)) {
            sessions.get(gameID).remove(session);
        }
    }

    public void broadcast(Session excludedSession, int gameID, String message) throws IOException {
        var removeMap = new HashMap<Integer, Session>();

        HashSet<Session> sessionSet = sessions.get(gameID);
        for (Session session : sessionSet)
            if (session.isOpen() && !session.equals(excludedSession)) {
                session.getRemote().sendString(message);

            } else if (!session.isOpen()) {
                removeMap.put(gameID, session);
            }


        removeSession(gameID, removeMap.get(gameID));
    }

}
