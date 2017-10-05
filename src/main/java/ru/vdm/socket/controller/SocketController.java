package ru.vdm.socket.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import org.slf4j.Logger;

import ru.necs.domain.service.ConfigService;
import ru.necs.web.controller.spi.SPIController;

public class SocketController extends SPIController {

	private static final Logger LOGGER = getLogger(SocketController.class);

	private SocketServer socketServer;

	@Override
	public void init(ConfigService service) {
		socketServer = new SocketServer(service);
		new Thread(socketServer) {{setDaemon(false);}}.start();
	}

	@Override
	public void destroy() {
		try {
			socketServer.stopServer();
		} catch (IOException e) {
			LOGGER.warn("Cannot stop Socket server", e);
		}
	}
}
