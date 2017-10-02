package ru.vdm.socket;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ru.necs.domain.model.Key;
import ru.necs.domain.model.Value;
import ru.necs.domain.service.ConfigService;
import ru.necs.domain.spring.DomainConfig;
import ru.vdm.socket.config.SocketConfig;
import ru.vdm.socket.controller.SocketController;

public class TestServerSocket {

	@Test
	@Ignore
	public void test() throws IOException {
		SocketController controller = new SocketController(new ConfigService() {

			@Override
			public Value lookup(Key key) {
				Value v = new Value();
				v.setHash("" + key.hashCode());
				v.setValue(key.getName());
				return v;
			}
		}, 7777, 1);
		controller.startServer();
	}

	static {
        System.setProperty("spring.profiles.active", "h2");
    }
	
	@Test
	public void testSpring() {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(SocketConfig.class, DomainConfig.class);
		context.close();
		
	}
	
}
