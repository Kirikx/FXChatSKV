package server.auth;

import java.sql.*;

import static java.lang.String.format;

public class BaseAuthService implements AuthService {

    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;
    static final String ERROR = "Не удача!";

//    private static class Entry {
//        private String login;
//        private String password;
//        private String nick;
//
//        public Entry(String login, String password, String nick) {
//            this.login = login;
//            this.password = password;
//            this.nick = nick;
//        }
//    }
//
//    private final List<Entry> entries = Arrays.asList(
//            new Entry("login1", "pass1", "nick1"),
//            new Entry("login2", "pass2", "nick2"),
//            new Entry("login3", "pass3", "nick3")
//    );

    @Override
    public void start() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:D:\\FXChatSKV\\FXChatSKVServer\\src\\main\\resources\\auth.db");
        statmt = conn.createStatement();
        conn.setAutoCommit(false);
        System.out.println("База Подключена!");

        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() throws SQLException {
        conn.close();
        statmt.close();
        resSet.close();
        System.out.println("Соединения закрыты");

        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        String query =
                String.format("SELECT nick FROM auth WHERE login ='%s' AND pass ='%s'", login, pass);
        resSet = statmt.executeQuery(query);

        if (resSet.next()) {
            return resSet.getString(1);
        }


//       for (Entry entry : entries) {
//            if (entry.login.equals(login) && entry.password.equals(pass)) {
//                return entry.nick;
//            }
//        }

        return null;
    }

    @Override
    public String rename(String login, String pass, String newnick) throws SQLException {
        String query =
                String.format("UPDATE auth SET nick = '%s' WHERE login = '%s'AND pass = '%s';", newnick, login, pass);
        statmt.executeUpdate(query);
        return newnick;
    }


}

