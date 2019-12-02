package websocket.exampe.com.db.repo;

import org.springframework.stereotype.Repository;
import websocket.exampe.com.db.entities.Message;
import websocket.exampe.com.db.repo.common.CustomRevisionRepository;

@Repository
public interface MessageRepository extends CustomRevisionRepository<Message, String> {
}
