package model;

import java.util.UUID;

public record AuthData(String userName, String authToken) {

    public static AuthData getNewAuthData(String userName) {

        return new AuthData(UUID.randomUUID().toString(), userName);
    }
}