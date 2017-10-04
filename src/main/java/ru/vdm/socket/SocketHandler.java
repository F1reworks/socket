package ru.vdm.socket;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import ru.necs.domain.model.Key;
import ru.necs.domain.model.Value;
import ru.necs.domain.service.ConfigService;

public class SocketHandler implements Runnable {

	private static final Logger LOGGER = getLogger(SocketHandler.class);

	private final Socket socket;

	private final ConfigService service;

	public SocketHandler(Socket socket, ConfigService service) {
		this.socket = socket;
		this.service = service;
	}

	private final Function<String, Key> requestMapper = (source -> {
		Key key = new Key();
		key.setName(source);
		return key;
	});
	private final Function<Value, String> responseMapper = (result -> result.toString());

	@Override
	public void run() {
		Validate.isTrue(socket.isConnected());
		Validate.isTrue(!socket.isClosed());
		try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			String input = bufReader.readLine();
			LOGGER.info(String.format("Get input: %s", input));
			try {
				Value result = service.lookup(requestMapper.apply(input));
				bufWriter.write(responseMapper.apply(result));
			} catch (EntityNotFoundException e) {
				bufWriter.write(String.format("No such entity: %s", input));
				LOGGER.warn(e.getMessage(), e);
			}
			bufWriter.flush();
		} catch (IOException e) {
			LOGGER.error("Socket io exception", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				LOGGER.error("Cannot close socket", e);
			}
		}
	}

	public void rejectTask() {
		try (BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			bufWriter.write("TOO BUSY!!!");
			bufWriter.flush();
		} catch (IOException e) {
			LOGGER.error("Reject socket task Exception", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				LOGGER.error("Cannot close socket during task reject", e);
			}
		}
	}
}
