package com.github.cristinalombardo.opcserver.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.cristinalombardo.opcserver.simpleserver.SimpleServer;

@Controller
public class ServerInfoController {
	
	@Autowired
	@Qualifier("simple-server")
	private SimpleServer server;
	
	@GetMapping("/")
	public ResponseEntity<String> getServerStatus() {
		return new ResponseEntity<String>(server.getDiagnosticsSummary().toString() + " Server is running", HttpStatus.OK);
	}

}
