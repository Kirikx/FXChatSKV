package Chat;

import javafx.application.Platform;
import Chat.Message.IMessageService;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network implements Closeable {

    private final String serverAddress;
    private final int port;
    private final IMessageService messageService;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Network(String serverAddress, int port, IMessageService messageService) throws IOException {
        this.serverAddress = serverAddress;
        this.port = port;
        this.messageService = messageService;
    }

    private void initNetworkState(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            while (true) {
                try {
                    String message = inputStream.readUTF();
                    Platform.runLater(() -> messageService.processRetrievedMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }


    public void send(String message) {
        try {
            if (outputStream == null) {
                initNetworkState(serverAddress, port);
            }
            outputStream.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке сообщения: " + message);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
