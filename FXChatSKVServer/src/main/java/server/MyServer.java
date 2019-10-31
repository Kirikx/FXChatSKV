package server;

import server.auth.AuthService;
import server.auth.BaseAuthService;
import server.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private static final int PORT = 8189;

    private final AuthService authService = new BaseAuthService();

    private List<ClientHandler> clients = new ArrayList<>();

    public MyServer() {
        System.out.println("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService.start();
            while (true) {
                System.out.println("Awaiting client connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client has connected");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера. Причина: " + e.getMessage());
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
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

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}
