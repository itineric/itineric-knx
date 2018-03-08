## What?
A Java written REST service oriented API to communicate with KNX (home automation protocol).

Dependencies:
* calimero-core
  * slf4j
* snake-yaml
* log4j2


## How?

```java
// load the configuration file (see example)
final Properties properties = new Properties();
properties.load(new FileReader("knx.properties"));

// retrieve a service using the loaded properties
final KnxService knxService = KnxServiceFactory.getKnxService(properties);

// get the raw service and write "true" on group address "1/1/0"
final KnxRawService knxRawService = knxService.getKnxRawService();
knxRawService.write("1/1/0", true);

// Retrieve the component service, components are declared in yaml configuration file, see example
final KnxComponentService knxComponentService = knxService.getKnxComponentService();

// open the living room north cover
knxComponentService.write(ComponentType.COVER, "living room north", false);
```
