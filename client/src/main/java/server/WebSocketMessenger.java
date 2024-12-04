package server;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;



public class WebSocketMessenger extends Endpoint {

    Session session;

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
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    handleNotification(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void handleNotification(NotificationMessage notification) {
        String msg = notification.getNotification();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}


}
