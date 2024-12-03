package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String notification;

    public NotificationMessage(String message) {
        super(ServerMessageType.ERROR);
        notification = message;
    }

    public String getNotification() { return notification; }
}
