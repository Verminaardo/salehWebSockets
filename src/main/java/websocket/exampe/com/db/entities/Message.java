package websocket.exampe.com.db.entities;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import org.springframework.data.web.SortDefault;
import websocket.exampe.com.db.entities.base.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Message")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Message extends GenericEntity {

    @Column(name = "user", nullable = false)
    @NotNull
    private String user;

    @Column(name = "text", nullable = false)
    @NotNull
    private String text;

    @Column(name = "time", nullable = false)
    @NotNull
    private LocalDateTime postingDateTime;

}
