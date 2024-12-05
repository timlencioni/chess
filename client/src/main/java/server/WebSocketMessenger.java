package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;



public class WebSocketMessenger extends Endpoint {

    Session session;
    ServerFacade server;

    public WebSocketMessenger(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    handleMessage(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void handleMessage(ServerMessage serverMessage) {

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> handleLoad(serverMessage);
            case NOTIFICATION -> handleNotification(serverMessage);
            case ERROR -> handleError(serverMessage);
        }
    }

    public void setServer(ServerFacade server) { this.server = server; }

    private void handleLoad(ServerMessage serverMessage) {
        ChessGame game = ((LoadMessage) serverMessage).getGame();
        server.setCurrGame(game); //FIXME: Is this working the way I think it is???
    }

    private void handleNotification(ServerMessage message) {
        String msg = ((NotificationMessage) message).getNotification(); // FIXME: Throwing and ignoring error...
        System.out.print(ERASE_LINE + '\r');
        System.out.printf("\n%s\n", msg);
    }

    private void handleError(ServerMessage serverMessage) {
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}


}
