package Chat.Message;

import java.io.Closeable;
import java.io.IOException;

public interface IMessageService extends Closeable {

    void sendMessage(String message);

    void processRetrievedMessage(String message);

    @Override
    default void close() throws IOException {
        //Do nothing
    }
}
