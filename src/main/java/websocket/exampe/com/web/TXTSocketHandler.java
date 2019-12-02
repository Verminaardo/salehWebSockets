package websocket.exampe.com.web;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import websocket.exampe.com.db.entities.Message;
import websocket.exampe.com.db.repo.MessageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TXTSocketHandler extends TextWebSocketHandler {

    //todo: Создать сервисную прослойку и разделить на модули
    private final MessageRepository messageRepository;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public TXTSocketHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        String username = "User " + session.getId();

        Message newMessage = new Message();
        newMessage.setText(message.getPayload());
        newMessage.setUser(username);
        newMessage.setPostingDateTime(LocalDateTime.now());
        messageRepository.save(newMessage);

        for(WebSocketSession webSocketSession : sessions) {
            sendMessage(webSocketSession, username, message.getPayload(), LocalDateTime.now());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        List<Message> messages = messageRepository.findByOrderByPostingDateTimeAsc();
        for(Message message : messages) {
            sendMessage(session, message.getUser(), message.getText(), message.getPostingDateTime());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private void sendMessage(WebSocketSession session, String tag, String text, LocalDateTime dateTime) throws IOException {
        String timeFormat = "HH:mm:ss";
        session.sendMessage(new TextMessage(dateTime.format(DateTimeFormatter.ofPattern(timeFormat)) + "    " + tag + ": " + text));
    }
}