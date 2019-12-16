package websocket.exampe.com.db.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import websocket.exampe.com.db.entities.base.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "User")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends GenericEntity {

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

}
