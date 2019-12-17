package websocket.exampe.com.web.restСontroller;

import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import websocket.exampe.com.db.entities.Message;
import websocket.exampe.com.db.entities.User;
import websocket.exampe.com.db.repo.MessageRepository;
import websocket.exampe.com.db.repo.UserRepository;

import java.util.Comparator;
import java.util.List;

@Component
public class MessageComponent {

    private final UserRepository userRepository;

    private final MessageRepository messageRepository;

    public MessageComponent(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> getAllMessages(@PathVariable String from, @PathVariable String to) throws NotFoundException {
        User fromUser = userRepository.findUserByLogin(from);
        User toUser = userRepository.findUserByLogin(to);

        if (fromUser == null || toUser == null) {
            throw new NotFoundException("User not found");
        }

        List<Message> resultList = messageRepository.findByFromUserIdAndToUserIdOrderByPostingDateTimeAsc(fromUser.getId(), toUser.getId());
        resultList.addAll(messageRepository.findByFromUserIdAndToUserIdOrderByPostingDateTimeAsc(toUser.getId(), fromUser.getId()));
        resultList.sort(Comparator.comparing(Message::getPostingDateTime));

        return resultList;
    }

}