package websocket.exampe.com.web;

import com.google.gson.Gson;
import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import websocket.exampe.com.db.entities.Message;
import websocket.exampe.com.db.entities.User;
import websocket.exampe.com.db.repo.MessageRepository;
import websocket.exampe.com.db.repo.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TXTSocketHandler extends TextWebSocketHandler {

    //todo: Создать сервисную прослойку и разделить на модули
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private Map<String, String> sessionIdWithUserLoginMap = new HashMap<>();

    public TXTSocketHandler(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException, NotFoundException {
        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        User fromUser = userRepository.findUserByLogin(value.get("from"));

        if(!sessionIdWithUserLoginMap.containsKey(session.getId())) {
            sessionIdWithUserLoginMap.put(session.getId(), fromUser.getLogin());
        }

        User toUser = userRepository.findUserByLogin(value.get("to"));

        if (fromUser == null || toUser == null) {
            throw new NotFoundException("User not found");
        }

        Message newMessage = new Message();
        newMessage.setFromUser(fromUser);
        newMessage.setToUser(toUser);
        newMessage.setText(value.get("message"));
        newMessage.setPostingDateTime(LocalDateTime.now());
        messageRepository.save(newMessage);

        WebSocketSession otherSession = sessions.stream().filter((s) -> s.getId().equals(toUser.getLogin())).findFirst().orElse(null);

        if (otherSession != null) {
            sendMessage(otherSession, fromUser.getLogin(), newMessage.getText(), LocalDateTime.now());
        }

        sendMessage(session, fromUser.getLogin(), newMessage.getText(), LocalDateTime.now());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        List<Message> messages = messageRepository.findByOrderByPostingDateTimeAsc();
        for(Message message : messages) {
            sendMessage(session, "tempTag", message.getText(), message.getPostingDateTime());
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