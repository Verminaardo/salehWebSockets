package websocket.exampe.com.web;

import com.google.gson.Gson;
import javafx.util.Pair;
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
import websocket.exampe.com.web.restСontroller.MessageComponent;

import java.io.IOException;
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
    private final MessageComponent messageComponent;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    //Мапа хранит активные ID сессии и пару-соединение "от" "кому"
    private Map<String, Pair<String, String>> sessionIdWithFromToPairMap = new HashMap<>();

    public TXTSocketHandler(MessageRepository messageRepository, UserRepository userRepository, MessageComponent messageComponent) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageComponent = messageComponent;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException, NotFoundException {

        SocketMessage value = new Gson().fromJson(message.getPayload(), SocketMessage.class);

        //Если такой пользователь уже есть разрываем соединение
        if (value.getFrom() != null && sessionIdWithFromToPairMap.values().stream().anyMatch(p -> p.getKey().equals(value.getFrom()))) {
            session.close();
            return;
        }

        //Если это первое подключение и пользователя еще нет в мапе - добавляем
        if (!sessionIdWithFromToPairMap.containsKey(session.getId())) {
            if (value.getFrom() == null || value.getTo() == null) {
                session.close();
                return;
            }
            sessionIdWithFromToPairMap.put(session.getId(), new Pair(value.getFrom(), value.getTo()));

            for (Message oldMessage : messageComponent.getAllMessages(value.getFrom(), value.getTo())) {
                sendMessage(session, oldMessage.getFromUser().getLogin(), oldMessage.getText(), oldMessage.getPostingDateTime());
            }

            return;
        }

        //При последующих соединениях идентифицируем пользователя по ID сессии
        User fromUser = userRepository.findUserByLogin(sessionIdWithFromToPairMap.get(session.getId()).getKey());

        if (fromUser == null) {
            throw new NotFoundException("User not found");
        }

        User toUser = userRepository.findUserByLogin(sessionIdWithFromToPairMap.get(session.getId()).getValue());

        if (toUser == null) {
            throw new NotFoundException("User not found");
        }

        Message newMessage = new Message();
        newMessage.setFromUser(fromUser);
        newMessage.setToUser(toUser);
        newMessage.setText(value.getMessage());
        newMessage.setPostingDateTime(LocalTime.now());
        messageRepository.save(newMessage);

        WebSocketSession otherSession = sessions.stream()
                .filter((WebSocketSession s) -> {
                    Pair<String, String> fromToPair = sessionIdWithFromToPairMap.get(s.getId());
                    //Проверяем следует ли отправлять сообщение пользователю посредствам веб сокета
                    return fromToPair.getKey().equals(toUser.getLogin()) && fromToPair.getValue().equals(fromUser.getLogin());
                })
                .findFirst().orElse(null);

        if (otherSession != null) {
            sendMessage(otherSession, fromUser.getLogin(), newMessage.getText(), LocalTime.now());
        }

        sendMessage(session, fromUser.getLogin(), newMessage.getText(), LocalTime.now());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionIdWithFromToPairMap.remove(session.getId());
        sessions.remove(session);
    }

    private boolean sessionExist(WebSocketSession session) {
        return sessions.stream().anyMatch(o -> o.getId().equals(session.getId()));
    }

    private void sendMessage(WebSocketSession session, String tag, String text, LocalTime dateTime) throws IOException {
        String timeFormat = "HH:mm:ss";
        session.sendMessage(new TextMessage(dateTime.format(DateTimeFormatter.ofPattern(timeFormat)) + "    " + tag + ": " + text));
    }
}