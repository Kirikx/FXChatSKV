package server.auth;


import javax.annotation.Nullable;
import java.sql.SQLException;

public interface AuthService {

    void start() throws ClassNotFoundException, SQLException;
    void stop() throws SQLException;

    @Nullable
    String getNickByLoginPass(String login, String pass) throws SQLException;

}
