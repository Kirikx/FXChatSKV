package server.client;

import server.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {

    public static final int TIMEOUT = 15 * 1000;
    public static final String AUTHOK = "/authok";
    public static final String AUTHNO = "/authno";

    private MyServer myServer;
    private String clientName;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, MyServer myServer) {
        try {
            this.socket = socket;
            this.myServer = myServer;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка!!!", e);
        }

    }

    private void readMessages() throws IOException {
        while (true) {
            String clientMessage = in.readUTF();
            System.out.printf("Сообщение '%s' от клиента %s%n", clientMessage, clientName);
            if (clientMessage.equals("/end")) {
                return;
            } else if (clientMessage.startsWith("/w")) {
                String[] search = clientMessage.split("\\s+");
                if (search.length < 3) {
                    sendMessage("Пользователь для приватного сообщения не определен! " + clientMessage);
                    continue;
                }
                String searchName = search[1];
                String message = clientMessage.substring(clientMessage.indexOf(searchName + " "));

                myServer.sendPrivateMessage(searchName, clientName + ": " + message);
            } else {
                myServer.broadcastMessage(clientName + ": " + clientMessage, this);
            }
        }
    }

    private void closeConnection() {
        myServer.deleteClient(this);
        myServer.broadcastMessage(clientName + " офлайн");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия подключения!");
            e.printStackTrace();
        }
    }

    private void authentication() throws IOException {
        while (true) {
            Timer timeoutTimer = new Timer(true);
            timeoutTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            if (clientName == null) {
                                System.out.println("Аутентификация не пройдена за заданное время");
                                sendMessage(AUTHNO + " Истекло время ожидания подключения!");
                                Thread.sleep(100);
                                socket.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, TIMEOUT);
            String clientMessage = in.readUTF();
            if (clientMessage.startsWith("/auth")) {
                String[] loginAndPasswords = clientMessage.split("\\s+");
                String login = loginAndPasswords[1];
                String password = loginAndPasswords[2];

                String nick = myServer.getAuthService().getNickByLoginPass(login, password);
                if (nick == null) {
                    sendMessage("Неверный логин/пароль");
                    continue;
                }

                if (myServer.isNickBusy(nick)) {
                    sendMessage("Учетная запись уже используется");
                    continue;
                }

                sendMessage(AUTHOK + nick);
                clientName = nick;
                myServer.broadcastMessage(clientName + " онлайн");
                myServer.addClient(this);
                break;
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Ошибка отправки сообшения клиенту " + clientName + " : " + message);
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

}
