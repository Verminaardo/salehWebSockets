package websocket.exampe.com.web.restСontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import websocket.exampe.com.db.entities.User;
import websocket.exampe.com.db.repo.UserRepository;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/{login}/getAll")
    public List<User> userLogin(@PathVariable String login) {
        User user = userRepository.findUserByLogin(login);

        if (user == null) {
            user = new User();
            user.setLogin(login);
            user = userRepository.save(user);
        }

        return userRepository.findUsersByLoginIsNotLikeOrderByLoginAsc(user.getLogin());
    }

}