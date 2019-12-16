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
import websocket.exampe.com.web.model.SocketMessage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        SocketMessage value = new Gson().fromJson(message.getPayload(), SocketMessage.class);

        if (value.getFrom() != null && sessionIdWithUserLoginMap.containsValue(value.getFrom())) {
            session.close();
        }

        if (!sessionIdWithUserLoginMap.containsKey(session.getId())) {
            sessionIdWithUserLoginMap.put(session.getId(), value.getFrom());
            return;
        }

        User fromUser = userRepository.findUserByLogin(sessionIdWithUserLoginMap.get(session.getId()));

        if (fromUser == null) {
            throw new NotFoundException("User not found");
        }

        User toUser = userRepository.findUserByLogin(value.getTo());

        if (toUser == null) {
            throw new NotFoundException("User not found");
        }

        Message newMessage = new Message();
        newMessage.setFromUser(fromUser);
        newMessage.setToUser(toUser);
        newMessage.setText(value.getMessage());
        newMessage.setPostingDateTime(LocalTime.now());
        messageRepository.save(newMessage);

        WebSocketSession otherSession = sessions.stream().filter((s) -> sessionIdWithUserLoginMap.get(s.getId()).equals(toUser.getLogin())).findFirst().orElse(null);

        if (otherSession != null) {
            sendMessage(otherSession, fromUser.getLogin(), newMessage.getText(), LocalDateTime.now());
        }

        sendMessage(session, fromUser.getLogin(), newMessage.getText(), LocalDateTime.now());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionIdWithUserLoginMap.remove(session.getId());
        sessions.remove(session);
    }

    private boolean sessionExist(WebSocketSession session) {
        return sessions.stream().anyMatch(o -> o.getId().equals(session.getId()));
    }

    private void sendMessage(WebSocketSession session, String tag, String text, LocalDateTime dateTime) throws IOException {
        String timeFormat = "HH:mm:ss";
        session.sendMessage(new TextMessage(dateTime.format(DateTimeFormatter.ofPattern(timeFormat)) + "    " + tag + ": " + text));
    }
}