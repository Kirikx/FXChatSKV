package server.client;

import server.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

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

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create client handler", e);
        }

    }

    private void readMessages() throws IOException {
        while (true) {
            String clientMessage = in.readUTF();
            System.out.printf("Message '%s' from client %s%n", clientMessage, clientName);
            if (clientMessage.equals("/end")) {
                return;
            }
            myServer.broadcastMessage(clientName + ": " + clientMessage);
        }
    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMessage(clientName + " is offline");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket!");
            e.printStackTrace();
        }
    }

    // "/auth login password"
    private void authentication() throws IOException {
        String clientMessage = in.readUTF();
        if (clientMessage.startsWith("/auth")) {
            String[] loginAndPasswords = clientMessage.split("\\s+");
            String login    = loginAndPasswords[1];
            String password = loginAndPasswords[2];

            String nick = myServer.getAuthService().getNickByLoginPass(login, password);
            if (nick == null) {
                sendMessage("Неверные логин/пароль");
                return;
            }

            if (myServer.isNickBusy(nick)) {
                sendMessage("Учетная запись уже используется");
                return;
            }

            sendMessage("/authok " + nick);
            clientName = nick;
            myServer.broadcastMessage(clientName + " is online");
            myServer.subscribe(this);
        }
    }

    public void sendMessage(String message)  {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Failed to send message to user " + clientName + " : " + message);
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

}
