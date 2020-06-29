package com.github.cristinalombardo.opcserver.secureserver;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final String ROOT_NODE_NAME = "WEATHER-FOLDER";

	//Allow a client to subscribe a value
	private final SubscriptionModel subscriptionModel;
	
	@Autowired
	private WeatherService weatherService;

	@Autowired
	public WeatherNamespace(SecureServer secureServer) {
		super(secureServer.getServer(), "urn:com:github:cristinalombardo:secure-server:weather");
		this.subscriptionModel = new SubscriptionModel(secureServer.getServer(), this);
		
	}

	//Startup this namespace
	@PostConstruct
	@Override
	protected void onStartup() {
		super.onStartup();
		
		logger.info("Startup Weather Namespace");
		
		this.subscriptionModel.startup();

		// Create Root Folder Identifier
		NodeId rootNodeId = newNodeId(ROOT_NODE_NAME);

		//Create Root folder
		UaFolderNode rootNode = new UaFolderNode(getNodeContext(),   
				rootNodeId, 
				newQualifiedName(ROOT_NODE_NAME), 
				LocalizedText.english(ROOT_NODE_NAME)
			);

		//Add folder to node manager
		this.getNodeManager().addNode(rootNode);

		//Add the reference to root node
		rootNode.addReference(new Reference(
				rootNode.getNodeId(),
				Identifiers.Organizes,
				Identifiers.ObjectsFolder.expanded(),
				false
				));
		
		this.createWeatherObject(rootNode);
	}
	
	private void createWeatherObject(UaFolderNode rootFolder) {
        // Define a new ObjectType called "MyObjectType".
        UaObjectTypeNode objectTypeNode = UaObjectTypeNode.builder(getNodeContext())
            .setNodeId(newNodeId("ObjectTypes/WeatherObject"))
            .setBrowseName(newQualifiedName("WeatherObject"))
            .setDisplayName(LocalizedText.english("WeatherObject"))
            .setIsAbstract(false)
            .build();

        // "Foo" and "Bar" are members. These nodes are what are called "instance declarations" by the spec.
        UaVariableNode city = UaVariableNode.builder(getNodeContext())
            .setNodeId(newNodeId("ObjectTypes/WeatherObject.City"))
            .setAccessLevel(AccessLevel.toValue(AccessLevel.READ_WRITE))
            .setBrowseName(newQualifiedName("City"))
            .setDisplayName(LocalizedText.english("City"))
            .setDataType(Identifiers.String)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .build();

        city.addReference(new Reference(
        		city.getNodeId(),
            Identifiers.HasModellingRule,
            Identifiers.ModellingRule_Mandatory.expanded(),
            true
        ));

        city.setValue(new DataValue(new Variant(this.weatherService.getCity())));
        objectTypeNode.addComponent(city);

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
        getNodeManager().addNode(city);

        // Use NodeFactory to create instance of MyObjectType called "MyObject".
        // NodeFactory takes care of recursively instantiating MyObject member nodes
        // as well as adding all nodes to the address space.
        try {
            UaObjectNode myObject = (UaObjectNode) getNodeFactory().createNode(
                newNodeId(ROOT_NODE_NAME  + "/WeatherObject"),
                objectTypeNode.getNodeId()
            );
            myObject.setBrowseName(newQualifiedName("WeatherObject"));
            myObject.setDisplayName(LocalizedText.english("WeatherObject"));

            // Add forward and inverse references from the root folder.
            rootFolder.addOrganizes(myObject);

            myObject.addReference(new Reference(
                myObject.getNodeId(),
                Identifiers.Organizes,
                rootFolder.getNodeId().expanded(),
                false
            ));
        } catch (UaException e) {
            logger.error("Error creating MyObjectType instance: "+ e.getMessage(), e);
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
