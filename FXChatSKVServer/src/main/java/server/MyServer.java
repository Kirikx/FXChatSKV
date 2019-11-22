package server;

import server.auth.AuthService;
import server.auth.BaseAuthService;
import server.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyServer {

    private static final int PORT = 8189;

    private final AuthService authService = new BaseAuthService();

    private List<ClientHandler> clients = new ArrayList<>();


    public MyServer() throws SQLException, ClassNotFoundException {
        System.out.println("Сервер запущен");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService.start();
            while (true) {
                System.out.println("Ожидание подключения клиентов...");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился!");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера. Причина: " + e.getMessage());
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    public synchronized void addClient(ClientHandler clientHandler) {

        clients.add(clientHandler);
    }


    public synchronized void deleteClient(ClientHandler clientHandler) {

        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(nick)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler... unfilteredClients) {
        List<ClientHandler> unfiltered = Arrays.asList(unfilteredClients);
        for (ClientHandler client : clients) {
            if (!unfiltered.contains(client)) {
                client.sendMessage(message);
            }
        }

    }

    public synchronized void sendPrivateMessage(String receivedLogin, String message) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(receivedLogin)) {
                client.sendMessage(message);
                break;
            }
        }
    }
}
