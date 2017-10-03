package ru.vdm.socket;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ru.necs.domain.model.Key;
import ru.necs.domain.model.Value;
import ru.necs.domain.service.ConfigService;
import ru.necs.domain.spring.DomainConfig;
import ru.necs.web.controller.spi.SPIController;
import ru.vdm.socket.controller.SocketController;


public class TestServerSocket {

	static {
		System.setProperty("spring.profiles.active", "h2");
	}

	@Test
	@Ignore
	public void test() throws IOException {
		SocketController controller = new SocketController();
		controller.setConfigService(new ConfigService() {

			@Override
			public Value lookup(Key key) {
				Value v = new Value();
				v.setHash("" + key.hashCode());
				v.setValue(key.getName());
				return v;
			}
		});
		controller.setPort(7777);
		controller.startServer();
	}

	@Test
	public void testSpring() {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(SocketTestConfig.class,
				DomainConfig.class);
		List<SPIController> controller = (List<SPIController>) context.getBean("getSpiController");
		context.close();
		System.out.println("END");
	}
}
