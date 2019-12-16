package websocket.exampe.com.db.repo;

import websocket.exampe.com.db.entities.User;
import websocket.exampe.com.db.repo.common.CustomRevisionRepository;

import java.util.List;

public interface UserRepository extends CustomRevisionRepository<User, String> {

    List<User> findUsersByIdIsNotAndOrderByNameAsc(String id);

}
