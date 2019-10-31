package Chat.Message;

import Chat.ControllerChat;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import Chat.Network;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerMessageService implements IMessageService {

    private static final String HOST_ADDRESS_PROP = "server.address";
    private static final String HOST_PORT_PROP = "server.port";
    public static final String STOP_SERVER_COMMAND = "/end";

    private String hostAddress;
    private int hostPort;

    private final TextArea MessageArea;
    private ControllerChat controllerChat;
    private boolean needStopServerOnClosed;
    private Network network;

    public ServerMessageService(ControllerChat controllerChat, boolean needStopServerOnClosed) {
        this.controllerChat = controllerChat;
        this.MessageArea = controllerChat.MessageArea;
        this.needStopServerOnClosed = needStopServerOnClosed;
        initialize();
    }

    private void initialize() {
        readProperties();
        startConnectionToServer();
    }

    private void startConnectionToServer() {
        try {
            this.network = new Network(hostAddress, hostPort, this);
        } catch (IOException e) {
            throw new ServerConnectionException("Ошибка подключения к серверу", e);
        }
    }

    private void readProperties() {
        Properties serverProperties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/ini.properties")) {
            serverProperties.load(inputStream);
            hostAddress = serverProperties.getProperty(HOST_ADDRESS_PROP);
            hostPort = Integer.parseInt(serverProperties.getProperty(HOST_PORT_PROP));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения из файла", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение порта", e);
        }
    }

    @Override
    public void sendMessage(String message) {
        network.send(message);
    }

    @Override
    public void processRetrievedMessage(String message) {
        if (message.startsWith("/authok")) {
            setVisibleChat();

        }
        else if (controllerChat.authPanel.isVisible()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Аутентификация не пройдена!");
            alert.setContentText(message);
            alert.showAndWait();
        }
        else {
            MessageArea.appendText("Сервер: " + message + System.lineSeparator());
        }
    }

    private void setVisibleChat() {
        controllerChat.authPanel.setVisible(false);
        controllerChat.MessageArea.setVisible(true);
        controllerChat.Message.setVisible(true);
        controllerChat.Send.setVisible(true);
    }

    @Override
    public void close() throws IOException {
        if (needStopServerOnClosed) {
            sendMessage(STOP_SERVER_COMMAND);
        }
        network.close();
    }
}
