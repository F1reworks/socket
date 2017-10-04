package ru.vdm.socket.config;

import static org.slf4j.LoggerFactory.getLogger;

import static ru.vdm.socket.config.SocketConfig.Prop.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

public class SocketConfig {

	private static final Logger LOGGER = getLogger(SocketConfig.class);

	private static Properties props = new Properties();
	static {
		try (InputStream source = SocketConfig.class.getClassLoader().getResourceAsStream("server.properties")) {
			props.load(source);
		} catch (IOException e) {
			LOGGER.error("Cannot init socket server properties", e);
		}
	}

	public static int getPort() {
		return getIntProp(PORT);
	}

	public static int getThreads() {
		return getIntProp(THREADS);
	}

	private static int getIntProp(Prop prop) {
		return Integer.parseInt(props.getProperty(prop.propName));
	}

	enum Prop {
		THREADS("threads"), PORT("port");

		private final String propName;

		private Prop(String propName) {
			this.propName = propName;
		}
	}
}
