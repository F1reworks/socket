package ru.vdm.socket;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ru.necs.domain.spring.DomainConfig;
import ru.necs.web.controller.spi.SPIConfig;

@Configuration
@Import({DomainConfig.class,SPIConfig.class})
@ComponentScan({"ru.necs.web.controller", "ru.necs.web.orika"})
public class SocketTestConfig {

}
