package websocket.exampe.com.web.model;

import lombok.Data;

@Data
public class SocketMessage {

    private String from;

    private String to;

    private String message;

}
