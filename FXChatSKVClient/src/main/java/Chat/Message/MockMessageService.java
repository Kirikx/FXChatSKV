package Chat.Message;

import Chat.Message.IMessageService;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class MockMessageService implements IMessageService {

    private TextArea chatTextArea;

    public MockMessageService(TextArea chatTextArea) {
        this.chatTextArea = chatTextArea;
    }

    @Override
    public void sendMessage(String message) {
        System.out.printf("Message %s has been sent%n", message);
        chatTextArea.appendText(message + System.lineSeparator());
    }

    @Override
    public void processRetrievedMessage(String message) {
        throw new UnsupportedOperationException();
    }

}
