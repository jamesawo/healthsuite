package com.hmis.server;

import com.hmis.server.init.config.AuditorAwareImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;

@EnableJpaAuditing(auditorAwareRef="auditorAware")
@SpringBootApplication
public class ServerApplication extends SpringBootServletInitializer {
	private static final Logger LOGGER= LoggerFactory.getLogger(ServerApplication.class);

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		LOGGER.info("Started ServerApplication On:  {}", new Date());
	}

}
