package com.github.cristinalombardo.opcserver;

import java.util.concurrent.ExecutionException;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.github.cristinalombardo.opcserver.conf.OpcServerConf;
import com.github.cristinalombardo.opcserver.secureserver.SecureServer;
import com.github.cristinalombardo.opcserver.simpleserver.SimpleServer;

@SpringBootApplication
@EnableScheduling
public class SpringBootOpcServerApplication {
	
	private Log logger = LogFactory.getLog(OpcServerConf.class); 
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		//Start Spring Boot Application to create IoT Container
		SpringApplication.run(SpringBootOpcServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			

			logger.info("Startup Simple Server");
			//Get Simple OpcUa Server from IoT container
			SimpleServer simpleServer = (SimpleServer) ctx.getBean("simple-server");

			//Start Simple OpcUa Server
			simpleServer.startServer().get(); 
			
			
			logger.info("Startup Secure Server");
			//Get Secure Server OpcUa Server from IoT container
			SecureServer secureServer = (SecureServer) ctx.getBean("secure-server");
			
			//Start Secure OpcUa Server
			secureServer.startServer().get();

			
		};
	}



}