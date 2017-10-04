package ru.vdm.socket.controller;

import ru.necs.domain.service.ConfigService;
import ru.necs.web.controller.spi.SPIController;

public class SocketController extends SPIController {

	private SocketServer socketServer;
	
	@Override
	public void init(ConfigService service) {
		socketServer = new SocketServer(service);
		new Thread(socketServer){{setDaemon(false);}}.start();
	}

	@Override
	public void destroy() {
		socketServer.stopServer();
	}
}
