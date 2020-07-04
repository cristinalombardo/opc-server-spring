package com.github.cristinalombardo.opcserver.secureserver;

import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_X509;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("secure-server")
public class SecureServer {

	private final Log logger = LogFactory.getLog(getClass()); 

	private final OpcUaServer server;
	
	public SecureServer(
			@Value("${opc.secure-server.bind-address:localhost}") String bindAddress, 
			@Value("${opc.secure-server.bind-port:4851}") Integer bindPort) throws Exception {
		//Create directory for store certificate
		File securityTempDir = new File(
				System.getProperty("user.home") +"/Documents/cristina/", 
				"security");

		if (
				!securityTempDir.exists() //Verify if dir exists 
				&&
				!securityTempDir.mkdirs() //Try to create the directory
				) {
			throw new Exception("unable to create security dir: " + securityTempDir);
		}

		logger.info("security temp dir: " + securityTempDir.getAbsolutePath());


		try {

			//Get or Create keystore loader (The loader have ServerCetificate and Server KeyPair
			KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);

			//Create certificate manager with loaded keystore
			DefaultCertificateManager certificateManager = new DefaultCertificateManager(
					loader.getServerKeyPair(),
					loader.getServerCertificateChain()
					);

			File pkiDir = securityTempDir.toPath().resolve("pki").toFile();
			DefaultTrustListManager trustListManager = new DefaultTrustListManager(pkiDir);
			LoggerFactory.getLogger(getClass()).info("pki dir: {}", pkiDir.getAbsolutePath());

			//Create certificate validator
			DefaultServerCertificateValidator certificateValidator =
					new DefaultServerCertificateValidator(trustListManager);


			//Add identity validator to validate users acess
			UsernameIdentityValidator identityValidator = new UsernameIdentityValidator(
					true,
					authChallenge -> {
						String username = authChallenge.getUsername();
						String password = authChallenge.getPassword();

						boolean userOk = "user".equals(username) && "password1".equals(password);
						boolean adminOk = "admin".equals(username) && "password2".equals(password);
						boolean cristinaOk = "cristina".equals(username) && "cristina".equals(password);

						return userOk || adminOk || cristinaOk;
					}
					);

			X509IdentityValidator x509IdentityValidator = new X509IdentityValidator(c -> true);

			// Get the first certificate of the collection
			X509Certificate certificate = certificateManager.getCertificates()
					.stream()
					.findFirst()
					.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError, "no certificate found"));

			// The configured application URI must match the one in the certificate(s)
			String applicationUri = CertificateUtil
					.getSanUri(certificate)
					.orElseThrow(() -> new UaRuntimeException(
							StatusCodes.Bad_ConfigurationError,
							"certificate is missing the application URI"));

			//Endpoint configurations
			Set<EndpointConfiguration> endpointConfigurations = createEndpointConfigurations(certificate, bindAddress, bindPort);

			//Create the server configuration
			@SuppressWarnings({ "rawtypes", "unchecked" })
			OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
					.setApplicationUri(applicationUri)
					.setApplicationName(LocalizedText.english("Eclipse Milo OPC UA Cristina Server"))
					.setEndpoints(endpointConfigurations)
					.setBuildInfo(
							new BuildInfo(
									"urn:com:github:cristinalombardo:secure-server",
									"cristinalombardo",
									"eclipse milo cristina server",
									OpcUaServer.SDK_VERSION,
									"1.0", DateTime.now()))
					.setCertificateManager(certificateManager)
					.setTrustListManager(trustListManager)
					.setCertificateValidator(certificateValidator)
					.setIdentityValidator(new CompositeValidator(identityValidator, x509IdentityValidator))
					.setProductUri("urn:com:github:cristinalombardo:secure-server")
					.build();

			//Create the server
			server = new OpcUaServer(serverConfig);
			
			
			//Create namespace to add object to this server
//			this.namespace = new SecureServerNamespace(server, "urn:com:github:cristinalombardo:secure-server:indinf");



		} catch (Exception e) {
			throw new Exception("Error while creating server -> server not created");
		}


	}

	private Set<EndpointConfiguration> createEndpointConfigurations(X509Certificate certificate, String bindAddress, Integer bindPort) {
		Set<EndpointConfiguration> endpointConfigurations = new LinkedHashSet<>();




		//Create a builder of Endpoint configuration
		EndpointConfiguration.Builder builder = EndpointConfiguration.newBuilder()
				.setBindAddress(bindAddress)
				.setHostname(bindAddress)
				.setPath("/cristina")
				.setCertificate(certificate)
				.addTokenPolicies(
						USER_TOKEN_POLICY_ANONYMOUS,
						USER_TOKEN_POLICY_USERNAME,
						USER_TOKEN_POLICY_X509);


		EndpointConfiguration.Builder noSecurityBuilder = builder.copy()
				.setSecurityPolicy(SecurityPolicy.None)
				.setSecurityMode(MessageSecurityMode.None);

		EndpointConfiguration.Builder signedBuilder = builder.copy()
				.setSecurityPolicy(SecurityPolicy.Basic256)
				.setSecurityMode(MessageSecurityMode.Sign);

		EndpointConfiguration.Builder securityBuilder = builder.copy()
				.setSecurityPolicy(SecurityPolicy.Basic256Sha256)
				.setSecurityMode(MessageSecurityMode.SignAndEncrypt);


		//OPC.TCP None / None
		endpointConfigurations.add(buildTcpEndpoint(noSecurityBuilder, bindPort));

		//OPC.TCP Sign Basic256
		endpointConfigurations.add(buildTcpEndpoint(signedBuilder, bindPort));

		// OPC.TCP Basic256Sha256 / SignAndEncrypt
		endpointConfigurations.add(buildTcpEndpoint(securityBuilder, bindPort));


		//		/*
		//		 * It's good practice to provide a discovery-specific endpoint with no security.
		//		 * It's required practice if all regular endpoints have security configured.
		//		 *
		//		 * Usage of the  "/discovery" suffix is defined by OPC UA Part 6:
		//		 *
		//		 * Each OPC UA Server Application implements the Discovery Service Set. If the OPC UA Server requires a
		//		 * different address for this Endpoint it shall create the address by appending the path "/discovery" to
		//		 * its base address.
		//		 */
		//
		//		EndpointConfiguration.Builder discoveryBuilder = builder.copy()
		//				.setPath("/cristina/discovery")
		//				.setSecurityPolicy(SecurityPolicy.None)
		//				.setSecurityMode(MessageSecurityMode.None);
		//
		//		endpointConfigurations.add(buildTcpEndpoint(discoveryBuilder, bindPort));

		return endpointConfigurations;
	}

	private static EndpointConfiguration buildTcpEndpoint(EndpointConfiguration.Builder base, Integer bindPort) {
		return base.copy()
				.setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
				.setBindPort(bindPort)
				.build();
	}

	public OpcUaServer getServer() {
		return server;
	}
	
	public CompletableFuture<OpcUaServer> startServer() {
		//Startup the server
		return this.server.startup();
	}

}
