package websocket.exampe.com.db.repo;

import org.springframework.stereotype.Repository;
import websocket.exampe.com.db.entities.Message;
import websocket.exampe.com.db.repo.common.CustomRevisionRepository;

import java.util.List;

@Repository
public interface MessageRepository extends CustomRevisionRepository<Message, String> {

    List<Message> findByOrderByPostingDateTimeAsc();

}
