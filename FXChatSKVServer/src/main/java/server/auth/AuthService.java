package server.auth;


import javax.annotation.Nullable;

public interface AuthService {

    void start();
    void stop();

    @Nullable
    String getNickByLoginPass(String login, String pass);

}
