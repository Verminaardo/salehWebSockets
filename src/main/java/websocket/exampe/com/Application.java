package websocket.exampe.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}

	private static Class<Application> applicationClass = Application.class;

}
