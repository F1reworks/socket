package ru.vdm.socket.controller;

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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;

import ru.necs.domain.service.ConfigService;
import ru.necs.web.controller.spi.SPIController;
import ru.vdm.socket.SocketHandler;
import ru.vdm.socket.config.SocketConfig;

public class SocketController extends SPIController {

	private static final Logger LOGGER = getLogger(SocketController.class);

	private ExecutorService executorService;

	protected ConfigService service;

	private int concurrentThreads;

	public void setConcurrentThreads(int concurrentThreads) {
		this.concurrentThreads = concurrentThreads;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setConfigService(ConfigService service) {
		this.service = service;
	}

	private ServerSocket server;

	private int port;
	private volatile boolean isStarted = false;

	public SocketController() {
	}

	public void startServer() {
		synchronized (this) {
			if (!isStarted) {
				try {
					server = new ServerSocket(port);
					isStarted = true;
				} catch (IOException e) {
					LOGGER.error("Cannot start server", e);
				}

			} else {
				// already started
				LOGGER.warn("Try to start already running server");
				return;
			}
		}

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(concurrentThreads * 2);
		executorService = new ThreadPoolExecutor(concurrentThreads, concurrentThreads, 0L, TimeUnit.MILLISECONDS, queue,
				new RejectedExecutionHandler() {

					@Override
					public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
						((SocketHandler) arg0).rejectTask();

					}
				});

		new Thread() {
			@Override
			public void run() {
				while (isStarted) {
					Socket socket;
					try {
						socket = server.accept();
						executorService.execute(new SocketHandler(socket, service));
					} catch (IOException e) {
						LOGGER.error("Connection reset may be shutdown in progress", e);
					}
				}
			}
		}.start();
	}

	@Override
	public void destroy() {
		isStarted = false;
		executorService.shutdownNow();
		Thread.currentThread().interrupt();
		try {
			server.close();
		} catch (IOException e) {
			LOGGER.error("Cannot close server connection", e);
		}
	}

	@Override
	public void init(ConfigService service) {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(SocketConfig.class);
		this.concurrentThreads = (int) context.getBean("threads");
		this.port = (int) context.getBean("port");
		this.service = service;
		context.close();
		startServer();
	}
}
