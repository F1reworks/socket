package ru.vdm.socket.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import ru.necs.domain.service.ConfigService;
import ru.vdm.socket.SocketHandler;
import ru.vdm.socket.config.SocketConfig;

public class SocketServer implements Runnable {

	private static final Logger LOGGER = getLogger(SocketServer.class);

	private volatile boolean isStopped = false;

	private ConfigService service;

	private ExecutorService executorService;

	public SocketServer(ConfigService service) {
		this.service = service;
	}

	@Override
	public void run() {
		try (ServerSocket server = new ServerSocket(SocketConfig.getPort())) {
			BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(SocketConfig.getThreads() * 2);
			executorService = new ThreadPoolExecutor(SocketConfig.getThreads(), SocketConfig.getThreads(), 0L,
					TimeUnit.MILLISECONDS, queue,
					(socketHandler, threadPool) -> ((SocketHandler) socketHandler).rejectTask());
			while (!isStopped) {
				Socket socket;
				socket = server.accept();
				executorService.execute(new SocketHandler(socket, service));
			}
		} catch (IOException e) {
			LOGGER.error("Connection reset, may be shutdown in progress", e);
		}
	}

	public void stopServer() {
		this.isStopped = true;
		executorService.shutdownNow();
	}
}
