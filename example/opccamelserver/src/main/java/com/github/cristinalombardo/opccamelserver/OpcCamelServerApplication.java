package com.github.cristinalombardo.opccamelserver;

import org.apache.camel.component.milo.server.MiloServerComponent;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OpcCamelServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpcCamelServerApplication.class, args);
	}

	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {			
			MiloServerComponent server = ctx.getBean(MiloServerComponent.class);
			
			System.out.println(server.getStatus());
			
			//Customize server configuration
			//......
			//
			
			//Start Camel Server
			CamelSpringBootApplicationController applicationController =
					ctx.getBean(CamelSpringBootApplicationController.class);
			applicationController.run();
			
		};
	}
}
