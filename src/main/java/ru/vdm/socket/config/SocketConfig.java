package ru.vdm.socket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfig {
	
	@Bean("threads")
	public int setConcurrentThreads() {
		return 2;
	}
	
	@Bean("port")
	public int setPort() {
		return 7777;
	}
}
