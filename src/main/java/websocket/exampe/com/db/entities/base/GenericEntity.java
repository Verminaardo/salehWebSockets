package websocket.exampe.com.db.entities.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Базовый класс для всех сущностей в системе.
 */
@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class GenericEntity implements Serializable {

    /**
     * Идентификатор сущности
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(length = 36)
    @NotNull
    protected String id;

    @Override
    public String toString() {
        return "GenericEntity[id=" + id + "]";
    }
}
