package ru.vdm.socket.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.necs.domain.service.ConfigService;
import ru.vdm.socket.controller.SocketController;

@Configuration
public class SocketConfig {
	
	@Bean
	public int setConcurrentThreads() {
		return 2;
	}
	
	@Bean
	public SocketController socketController(final ConfigService service) throws IOException {
		SocketController socketController = new SocketController();
		socketController.setConcurrentThreads(2);
		socketController.setServerPort(7777);
		socketController.setConfigService(service);
		return socketController;
	}
}
