package com.github.cristinalombardo.opcserver.secureserver;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeObserver;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.cristinalombardo.opcserver.wather.WeatherService;

/**
 * 
 * @author Cristina Lombardo
 * This class implements the namespace to add object to the defined server
 *
 */
@Component("weather-namespace")
public class WeatherNamespace extends ManagedNamespace {

	private final Log logger = LogFactory.getLog(getClass()); 

	private static final String ROOT_NODE_NAME = "ROOT-FOLDER";

	//Allow a client to subscribe a value
	private final SubscriptionModel subscriptionModel;

	@Value("${weather.apikey}")
	private String apiKey;

	@Value("${weather.apiurl}")
	private String apiUrl;

	@Value("${weather.callinterval}")
	private Integer refresh;


	@Autowired
	private WeatherService weatherService;

	//Inject on the Constructor the instance of SecureServer present into the IoC Container
	@Autowired
	public WeatherNamespace(SecureServer secureServer) {
		super(secureServer.getServer(), "urn:com:github:cristinalombardo:secure-server:weather");
		this.subscriptionModel = new SubscriptionModel(secureServer.getServer(), this);

	}

	//Startup this namespace after Construction
	@PostConstruct
	@Override
	protected void onStartup() {
		super.onStartup();

		logger.info("Startup Weather Namespace");

		this.subscriptionModel.startup();

		// Create Root Folder Identifier
		NodeId rootNodeId = newNodeId(ROOT_NODE_NAME);

		//Create Root folder
		UaFolderNode rootFolder = new UaFolderNode(getNodeContext(),   
				rootNodeId, 
				newQualifiedName(ROOT_NODE_NAME), 
				LocalizedText.english(ROOT_NODE_NAME)
				);

		//Add folder to node manager
		this.getNodeManager().addNode(rootFolder);

		//Add the reference to root node
		rootFolder.addReference(new Reference(
				rootFolder.getNodeId(),
				Identifiers.Organizes,
				Identifiers.ObjectsFolder.expanded(),
				false
				));

		this.creteaConfObject(rootFolder);

		this.createWeatherElements(rootFolder);

	}

	private void creteaConfObject(UaFolderNode rootFolder) {
		// Define a new ObjectType called "ConfObject".
		String objectPath = "ObjectTypes/ConfObject";
		UaObjectTypeNode objectTypeNode = UaObjectTypeNode.builder(getNodeContext())
				.setNodeId(newNodeId(objectPath))
				.setBrowseName(newQualifiedName("ConfObject"))
				.setDisplayName(LocalizedText.english("ConfObject"))
				.setIsAbstract(false)
				.build();
		
		// Tell the ObjectTypeManager about our new type.
		// This let's us use NodeFactory to instantiate instances of the type.
		getServer().getObjectTypeManager().registerObjectType(
				objectTypeNode.getNodeId(),
				UaObjectNode.class,
				UaObjectNode::new
				);

		// Add the inverse SubtypeOf relationship.
		objectTypeNode.addReference(new Reference(
				objectTypeNode.getNodeId(),
				Identifiers.HasSubtype,
				Identifiers.BaseObjectType.expanded(),
				false
				));

		// Add type definition and declarations to address space.
		getNodeManager().addNode(objectTypeNode);
		

		// "ApiKey" , "ApiUrl" and "Refresh" are members.
		{
			String name = "apiKey";
			
			UaVariableNode apiKey = UaVariableNode.builder(getNodeContext())
					.setNodeId(newNodeId(objectPath + "." + name))
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY))
					.setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Key to access Open Weather API"))
					.setDataType(Identifiers.String)
					.setTypeDefinition(Identifiers.BaseDataVariableType)
					.build();
	
			apiKey.addReference(new Reference(
					apiKey.getNodeId(),
					Identifiers.HasModellingRule,
					Identifiers.ModellingRule_Mandatory.expanded(),
					true
					));
	
			apiKey.setValue(new DataValue(new Variant(this.apiKey)));
			objectTypeNode.addComponent(apiKey);
			
			getNodeManager().addNode(apiKey);
		}
		
		{
			String name = "apiUrl";
			
			UaVariableNode apiUrl = UaVariableNode.builder(getNodeContext())
					.setNodeId(newNodeId(objectPath + "." + name))
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY))
					.setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Open Weather API URL"))
					.setDataType(Identifiers.String)
					.setTypeDefinition(Identifiers.BaseDataVariableType)
					.build();
	
			apiUrl.addReference(new Reference(
					apiUrl.getNodeId(),
					Identifiers.HasModellingRule,
					Identifiers.ModellingRule_Mandatory.expanded(),
					true
					));
	
			apiUrl.setValue(new DataValue(new Variant(this.apiUrl)));
			objectTypeNode.addComponent(apiUrl);
			
			getNodeManager().addNode(apiUrl);
		}
		
		{
			String name = "refresh";
			
			UaVariableNode refresh = UaVariableNode.builder(getNodeContext())
					.setNodeId(newNodeId(objectPath + "." + name))
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY))
					.setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Number of milliseconds after while the WeatherService refresh the current weather"))
					.setDataType(Identifiers.Int32)
					.setTypeDefinition(Identifiers.BaseDataVariableType)
					.build();
	
			refresh.addReference(new Reference(
					refresh.getNodeId(),
					Identifiers.HasModellingRule,
					Identifiers.ModellingRule_Mandatory.expanded(),
					true
					));
	
			refresh.setValue(new DataValue(new Variant(this.refresh)));
			objectTypeNode.addComponent(refresh);
			
			getNodeManager().addNode(refresh);
		}



		try {
			String objectName = "ConfObject";
			UaObjectNode confObject = (UaObjectNode) getNodeFactory().createNode(
					newNodeId(rootFolder.getNodeId().getIdentifier() + "/" + objectName),
					objectTypeNode.getNodeId()
					);
			confObject.setBrowseName(newQualifiedName(objectName));
			confObject.setDisplayName(LocalizedText.english(objectName));

			// Add forward and inverse references from the root folder.
			rootFolder.addOrganizes(confObject);

			confObject.addReference(new Reference(
					confObject.getNodeId(),
					Identifiers.Organizes,
					rootFolder.getNodeId().expanded(),
					false
					));
		} catch (UaException e) {
			logger.error("Error creating ConfObject instance: " + e.getMessage(), e);
		}

	}

	private void createWeatherElements(UaFolderNode rootFolder) {

		//Create weather Folder
		String weatherFolderPath = rootFolder.getNodeId().getIdentifier() + "/Weather";
		UaFolderNode weatherFolder = new UaFolderNode(
				getNodeContext(),
				newNodeId(weatherFolderPath),
				newQualifiedName("Weather"),
				LocalizedText.english("Weather")
				);

		getNodeManager().addNode(weatherFolder);
		rootFolder.addOrganizes(weatherFolder);

		//City Name
		{
			String name = "city";
			UaVariableNode cityChanger = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
					.setNodeId(newNodeId(weatherFolderPath+ "/" + name))
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_WRITE))
					.setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name))
					.setDataType(Identifiers.String)
					.setTypeDefinition(Identifiers.BaseDataVariableType)
					.build();

			cityChanger.setValue(new DataValue(new Variant(this.weatherService.getCity())));

			cityChanger.getFilterChain().addLast(new RestrictedAccessFilter(identity -> {
				return AccessLevel.READ_WRITE;
			}));

			cityChanger.addAttributeObserver(new AttributeObserver() {
				@Override
				public void attributeChanged(UaNode node, AttributeId attributeId, Object value) {
					if(value instanceof DataValue) {
						DataValue data = (DataValue) value;
						logger.info("Write City to get: "+ data.getValue().getValue() + " ");

						if(data.getValue().getValue() instanceof String[]) {
							String[] cityArray = (String[]) data.getValue().getValue();

							if(cityArray.length == 2) {
								weatherService.setCity(cityArray[0] + "," + cityArray[1]);
							} else {
								throw new RuntimeException("The size of the string vector must be 2");
							}

						} else {
							throw new RuntimeException("The data value must be String[]");
						}


					}


				}
			});

			getNodeManager().addNode(cityChanger);
			weatherFolder.addOrganizes(cityChanger);
		}

		// Forecast phrase
		{
			String nametemp = "forecast";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode temp = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(weatherFolderPath + "/" + nametemp)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(nametemp)) //setup the name
					.setDisplayName(LocalizedText.english(nametemp))
					.setDescription(LocalizedText.english("Forecast phrase"))
					.setDataType(Identifiers.String) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			temp.setValue(new DataValue(variant));

			temp.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(this.weatherService.getCurrentWeather().getWeather().get(0).getDescription())) 
							)
					);
			getNodeManager().addNode(temp);

			weatherFolder.addOrganizes(temp);
		}

		//Forecast language
		{
			String name = "language";
			UaVariableNode languageChanger = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
					.setNodeId(newNodeId(weatherFolderPath+ "/" + name))
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_WRITE))
					.setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name))
					.setDataType(Identifiers.String)
					.setTypeDefinition(Identifiers.BaseDataVariableType)
					.build();

			languageChanger.setValue(new DataValue(new Variant(this.weatherService.getLang())));

			languageChanger.getFilterChain().addLast(new RestrictedAccessFilter(identity -> {
				return AccessLevel.READ_WRITE;
			}));

			languageChanger.addAttributeObserver(new AttributeObserver() {
				@Override
				public void attributeChanged(UaNode node, AttributeId attributeId, Object value) {
					System.out.println(attributeId + " " + value.toString());
					if(value instanceof DataValue) {
						DataValue data = (DataValue) value;
						logger.info("Write Lang: "+ data.getValue().getValue());
						weatherService.setLang(data.getValue().getValue().toString());
					}


				}
			});

			getNodeManager().addNode(languageChanger);
			weatherFolder.addOrganizes(languageChanger);
		}


		this.createTemperatureFolder(weatherFolder);

		this.createWindFolder(weatherFolder);	


	}


	private void createTemperatureFolder(UaFolderNode weatherFolder){

		//Create temperature Folder
		String temperatureFolderPath = weatherFolder.getNodeId().getIdentifier() + "/Temperature";
		UaFolderNode temperatureFolder = new UaFolderNode(
				getNodeContext(),
				newNodeId(temperatureFolderPath),
				newQualifiedName("Temperature"),
				LocalizedText.english("Temperature")
				);

		getNodeManager().addNode(temperatureFolder);
		weatherFolder.addOrganizes(temperatureFolder);

		// City Temperature
		{
			String nametemp = "temp";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode temp = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(temperatureFolderPath + "/" + nametemp)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(nametemp)) //setup the name
					.setDisplayName(LocalizedText.english(nametemp))
					.setDescription(LocalizedText.english("Provide city temperature °C"))
					.setDataType(Identifiers.Double) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			temp.setValue(new DataValue(variant));

			temp.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(Math.round(this.weatherService.getCurrentWeather().getMain().getTempAsCentigrate())))
							)
					);

			getNodeManager().addNode(temp);


			temperatureFolder.addOrganizes(temp);
		}


		{
			String name = "feels_like";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode feelsLike = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(temperatureFolderPath + "/" + name)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(name)) //setup the name
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Provide city temperature Feels °C"))
					.setDataType(Identifiers.Double) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			feelsLike.setValue(new DataValue(variant));

			feelsLike.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(Math.round(this.weatherService.getCurrentWeather().getMain().getFeelsLikeAsCentigrate())))
							)
					);

			getNodeManager().addNode(feelsLike);


			temperatureFolder.addOrganizes(feelsLike);
		}

		{
			String name = "min";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode min = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(temperatureFolderPath + "/" + name)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(name)) //setup the name
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Provide city Min Temperature of the day °C"))
					.setDataType(Identifiers.Double) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			min.setValue(new DataValue(variant));

			min.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(Math.round(this.weatherService.getCurrentWeather().getMain().getTempMinAsCentigrate())))
							)
					);

			getNodeManager().addNode(min);


			temperatureFolder.addOrganizes(min);
		}

		{
			String name = "max";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode max = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(temperatureFolderPath + "/" + name)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(name)) //setup the name
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Provide city Max Temperature of the day °C"))
					.setDataType(Identifiers.Double) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			max.setValue(new DataValue(variant));

			max.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(Math.round(this.weatherService.getCurrentWeather().getMain().getTempMaxAsCentigrate())))
							)
					);

			getNodeManager().addNode(max);


			temperatureFolder.addOrganizes(max);
		}


	}

	private void createWindFolder(UaFolderNode weatherFolder) {

		//Create Wind Folder
		String windFolderPath = weatherFolder.getNodeId().getIdentifier() + "/Wind";
		UaFolderNode windFolder = new UaFolderNode(
				getNodeContext(),
				newNodeId(windFolderPath),
				newQualifiedName("Wind"),
				LocalizedText.english("Wind")
				);

		getNodeManager().addNode(windFolder);
		weatherFolder.addOrganizes(windFolder);

		// City Wind Speed
		{
			String name = "speed";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode speed = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(windFolderPath + "/" + name)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(name)) //setup the name
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Provide city wind speed in m/s"))
					.setDataType(Identifiers.Double) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			speed.setValue(new DataValue(variant));

			speed.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(Math.round(this.weatherService.getCurrentWeather().getWind().getSpeed())))
							)
					);

			getNodeManager().addNode(speed);


			windFolder.addOrganizes(speed);
		}

		// City Wind Deg
		{
			String name = "deg";
			//Define the node content as variable value
			Variant variant = new Variant(0);

			//Define the node as Variable Node
			UaVariableNode deg = new UaVariableNode.UaVariableNodeBuilder(
					getNodeContext())
					.setNodeId(newNodeId(windFolderPath + "/" + name)) //Define the node name
					.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_ONLY)) //Define that the node is accessible only in read
					.setBrowseName(newQualifiedName(name)) //setup the name
					.setDisplayName(LocalizedText.english(name))
					.setDescription(LocalizedText.english("Provide city wind deg"))
					.setDataType(Identifiers.Int32) //setup the type
					.setTypeDefinition(Identifiers.BaseDataVariableType) //Setup the definition as variable types
					.build();

			deg.setValue(new DataValue(variant));

			deg.getFilterChain().addLast(
					AttributeFilters.getValue(
							ctx ->
							new DataValue(new Variant(this.weatherService.getCurrentWeather().getWind().getDeg()))
							)
					);

			getNodeManager().addNode(deg);


			windFolder.addOrganizes(deg);
		}

	}

	//Shutdown this namespace
	@Override
	protected void onShutdown() {
		logger.info("Shutdown Secure Namespace");
		subscriptionModel.shutdown();

		super.onShutdown();
	}

	@Override
	public void onDataItemsCreated(List<DataItem> dataItems) {
		logger.info("Create subscription ");
		this.subscriptionModel.onDataItemsCreated(dataItems);

	}

	@Override
	public void onDataItemsModified(List<DataItem> dataItems) {
		logger.info("Modify Subscription");
		this.subscriptionModel.onDataItemsModified(dataItems);

	}

	@Override
	public void onDataItemsDeleted(List<DataItem> dataItems) {
		logger.info("Delete Subscription");
		this.subscriptionModel.onDataItemsDeleted(dataItems);
	}

	@Override
	public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
		logger.info("Monitor Subscription");
		this.subscriptionModel.onMonitoringModeChanged(monitoredItems);

	}

}
