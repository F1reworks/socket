package ru.vdm.socket.controller;

import static org.apache.commons.lang3.Validate.notNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

import ru.necs.domain.service.ConfigService;
import ru.necs.web.controller.spi.SPIController;
import ru.vdm.socket.SocketHandler;
import ru.vdm.socket.config.SocketConfig;

@Controller
@Import(SocketConfig.class)
public class SocketController extends SPIController {

	private static final Logger LOGGER = getLogger(SocketController.class);

	private ExecutorService executorService;

	private int concurrentThreads;
	
	@Autowired
	public void setConcurrentThreads(int concurrentThreads) {
		this.concurrentThreads = concurrentThreads;
	}

	private ServerSocket server;
	private volatile boolean isStarted = false;

	public void setServerPort(int port) throws IOException {
		this.server = new ServerSocket(port);
	}

	public void setConfigService(ConfigService service) {
		this.service = service;
	}

	public SocketController() {
	}

	@PostConstruct
	public void startServer() {
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(concurrentThreads * 2);
		executorService = new ThreadPoolExecutor(concurrentThreads, concurrentThreads, 0L, TimeUnit.MILLISECONDS, queue,
				new RejectedExecutionHandler() {

					@Override
					public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
						((SocketHandler) arg0).rejectTask();

					}
				});
		isStarted = true;
		new Thread() {
			{
				setDaemon(false);
			}

			@Override
			public void run() {
				while (isStarted) {
					Socket socket;
					try {
						socket = server.accept();
						executorService.execute(new SocketHandler(socket, service));
					} catch (IOException e) {
						LOGGER.error("Cannot start Socket server", e);
					}
				}
			}
		}.start();
	}

	@PreDestroy
	public void stopServer() throws IOException {
		isStarted = false;
		executorService.shutdownNow();
		Thread.currentThread().interrupt();
		server.close();
	}
}
