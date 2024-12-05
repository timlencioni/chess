package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String notification;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        notification = message;
    }

    public String getNotification() { return notification; }
}
