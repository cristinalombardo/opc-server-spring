package com.github.cristinalombardo.opcserver.conf;

import java.util.concurrent.ExecutionException;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.cristinalombardo.opcserver.secureserver.SecureServer;
import com.github.cristinalombardo.opcserver.simpleserver.SimpleServer;

@Configuration
public class OpcServerConf {

	private Log logger = LogFactory.getLog(OpcServerConf.class); 

	@Bean("simple-server")
	public SimpleServer simpleServer(
			@Value("${opc.simple-server.bind-address:localhost}") String bindAddress,
			@Value("${opc.simple-server.bind-port:4850}") Integer bindPort
			) throws InterruptedException, ExecutionException {

		return new SimpleServer(bindAddress, bindPort);
	}


}
