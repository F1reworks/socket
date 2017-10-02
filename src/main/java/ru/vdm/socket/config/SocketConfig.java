package ru.vdm.socket.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ru.necs.domain.service.ConfigService;
import ru.vdm.socket.controller.SocketController;

@Configuration
@ComponentScan({ "ru.vdm.socket.controller", "ru.necs.domain" })
public class SocketConfig {
	@Bean
	public SocketController socketController(final ConfigService service) throws IOException {
		return new SocketController(service, 7777, 2);
	}
}
