package ru.vdm.socket;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ru.necs.domain.model.Key;
import ru.necs.domain.model.Value;
import ru.necs.domain.service.ConfigService;
import ru.necs.domain.spring.DomainConfig;
import ru.vdm.socket.controller.SocketController;

public class TestServerSocket {

	static {
		System.setProperty("spring.profiles.active", "h2");
	}

	@Test
	public void test() throws IOException {
		SocketController controller = new SocketController();
		controller.init(new ConfigService() {

			@Override
			public Value lookup(Key key) {
				Value v = new Value();
				v.setHash("" + key.hashCode());
				v.setValue(key.getName());
				return v;
			}
		});
	}

	@Test
	public void testSpring() {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(SocketTestConfig.class,
				DomainConfig.class);
		context.close();
	}
}
