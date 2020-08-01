# Quick Start

## Requirements

- Java 9+
- [Prosys opc ua client](https://downloads.prosysopc.com/opc-ua-client-downloads.php)
- Maven

In you like to only run the project goto [Run the project](#run-the-project)

## Build the project

1. Goto `opcserver` forlder from command line
2. Donwload library and compile package
```bash
$ mvn clean package
```
3. Goto `opcserver\target` folder
4. Execute:
```bash
$ java -jar opcserver-1.0.0.jar 
```



## Run The project

1. Go to dist folder from command line
1. execute following

```bash
$ java -jar opcserver-1.0.0.jar 
```

The console result should be something like:

```
   _____          _         _     _                 
  / ____|        (_)       | |   (_)                
 | |       _ __   _   ___  | |_   _   _ __     __ _ 
 | |      | '__| | | / __| | __| | | | '_ \   / _` |
 | |____  | |    | | \__ \ | |_  | | | | | | | (_| |
  \_____| |_|    |_| |___/  \__| |_| |_| |_|  \__,_|
                                                                                                              
2020-08-01 16:05:15.319  INFO 4004 --- [           main] c.g.c.o.SpringBootOpcServerApplication   : Starting SpringBootOpcServerApplication on fd-macbook with PID 4004 (/Users/francesco/Documents/cristina/GitHub/opc-server-spring/opcserver/target/classes started by francesco in /Users/francesco/Documents/cristina/GitHub/opc-server-spring/opcserver)
2020-08-01 16:05:15.322  INFO 4004 --- [           main] c.g.c.o.SpringBootOpcServerApplication   : No active profile set, falling back to default profiles: default
2020-08-01 16:05:16.202  INFO 4004 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2020-08-01 16:05:16.212  INFO 4004 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2020-08-01 16:05:16.213  INFO 4004 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.35]
2020-08-01 16:05:16.311  INFO 4004 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2020-08-01 16:05:16.311  INFO 4004 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 947 ms
2020-08-01 16:05:16.361  INFO 4004 --- [           main] c.g.c.o.secureserver.SecureServer        : security temp dir: /Users/francesco/Documents/certificate/security
2020-08-01 16:05:16.365  INFO 4004 --- [           main] c.g.c.o.secureserver.KeyStoreLoader      : Loading KeyStore at /Users/francesco/Documents/certificate/security/cristina-keystore.pfx
2020-08-01 16:05:16.688  INFO 4004 --- [           main] c.g.c.o.secureserver.SecureServer        : pki dir: /Users/francesco/Documents/certificate/security/pki
2020-08-01 16:05:16.877  INFO 4004 --- [           main] o.e.milo.opcua.sdk.server.OpcUaServer    : Eclipse Milo OPC UA Stack version: 0.4.1
2020-08-01 16:05:16.877  INFO 4004 --- [           main] o.e.milo.opcua.sdk.server.OpcUaServer    : Eclipse Milo OPC UA Server SDK version: 0.4.1
2020-08-01 16:05:18.158  INFO 4004 --- [           main] o.e.m.o.s.s.namespaces.OpcUaNamespace    : Loaded 2008 nodes in 629ms.
2020-08-01 16:05:18.194  INFO 4004 --- [           main] c.g.c.o.s.SecureServerNamespace          : Startup Secure Namespace
2020-08-01 16:05:18.223  INFO 4004 --- [           main] c.g.c.o.secureserver.WeatherNamespace    : Startup Weather Namespace
2020-08-01 16:05:18.259  INFO 4004 --- [           main] c.g.c.o.simpleserver.SimpleServer        : Create OPC Server
2020-08-01 16:05:18.409  INFO 4004 --- [           main] o.e.m.o.s.s.namespaces.OpcUaNamespace    : Loaded 2008 nodes in 144ms.
2020-08-01 16:05:18.511  INFO 4004 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-08-01 16:05:18.646  INFO 4004 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
2020-08-01 16:05:18.693  INFO 4004 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-08-01 16:05:18.706  INFO 4004 --- [           main] c.g.c.o.SpringBootOpcServerApplication   : Started SpringBootOpcServerApplication in 3.696 seconds (JVM running for 4.556)
2020-08-01 16:05:18.708  INFO 4004 --- [           main] c.g.c.opcserver.conf.OpcServerConf       : Startup Simple Server
2020-08-01 16:05:18.709  INFO 4004 --- [           main] o.e.m.opcua.stack.server.UaStackServer   : Binding endpoint opc.tcp://localhost:5380 to 0.0.0.0:5380 [None/None]
2020-08-01 16:05:18.861  INFO 4004 --- [           main] c.g.c.opcserver.conf.OpcServerConf       : Startup Secure Server
2020-08-01 16:05:18.861  INFO 4004 --- [           main] o.e.m.opcua.stack.server.UaStackServer   : Binding endpoint opc.tcp://0.0.0.0:5381/cristina to 0.0.0.0:5381 [None/None]
2020-08-01 16:05:18.862  INFO 4004 --- [           main] o.e.m.opcua.stack.server.UaStackServer   : Binding endpoint opc.tcp://0.0.0.0:5381/cristina to 0.0.0.0:5381 [Basic256/Sign]
2020-08-01 16:05:18.862  INFO 4004 --- [           main] o.e.m.opcua.stack.server.UaStackServer   : Binding endpoint opc.tcp://0.0.0.0:5381/cristina to 0.0.0.0:5381 [Basic256Sha256/SignAndEncrypt]
2020-08-01 16:05:20.210  INFO 4004 --- [   scheduling-1] c.g.c.o.wather.WeatherServiceImp         : 
Call: http://api.openweathermap.org/data/2.5/weather?q=Melbourne,AU&appid=59bd13613e0851d6c2544eb72f200621&lang=it
Response -> WeatherBean [coord=CoordElement [lon=144.96, lat=-37.81], weather=[WeatherElement [id=800, main=Clear, description=cielo sereno, icon=01n]], base=stations, main=MainElement [temp=284.15, pressure=1021.0, humidity=71.0, feelsLike=276.71, tempMin=283.15, tempMax=285.37], visibility=10000, wind=WindElement [speed=9.3, deg=350], clouds=CloudsElement [all=0.0], dt=1596290459, sys=SysElement [type=1, id=9548, country=AU, sunrise=1596316761, sunset=1596353604], timezone=36000, id=2158177, name=Melbourne, code=null]
```
