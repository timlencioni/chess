package handler;

public class UserException extends Exception {

    int errorNum;

    public UserException(String message, int errorNum) {

        super(message);
        this.errorNum = errorNum;
    }

    public int getErrorNum() { return this.errorNum; }
}
