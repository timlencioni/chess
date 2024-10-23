package handler;

public class GameException extends Exception {

    private int errorNum;

    public GameException(String message, int errorNum) {

        super(message);
        this.errorNum = errorNum;
    }

    public int getErrorNum() { return this.errorNum; }
}
