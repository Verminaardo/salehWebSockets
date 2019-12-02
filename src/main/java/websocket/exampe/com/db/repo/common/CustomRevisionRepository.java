package websocket.exampe.com.db.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CustomRevisionRepository<T extends Object, ID extends Serializable> extends JpaRepository<T, ID> {
}
