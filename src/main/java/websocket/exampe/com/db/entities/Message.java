package websocket.exampe.com.db.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import org.springframework.data.web.SortDefault;
import websocket.exampe.com.db.entities.base.GenericEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Message")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Message extends GenericEntity {

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Column(name = "text", nullable = false)
    @NotNull
    private String text;

    @Column(name = "time", nullable = false)
    @NotNull
    private LocalDateTime postingDateTime;

}
