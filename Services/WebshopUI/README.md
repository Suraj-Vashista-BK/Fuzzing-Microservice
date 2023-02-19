# Service-Oriented Modifiability Experiment

To provide empirical support for the modifiability of service-based systems this repo contains two functionally equivalent WebShop systems. The system is implemented in very basic fashion and provides CRUD operations for e.g. `customers`, `products`, or `orders`. For simplicity, no persistence of data is implemented, i.e. after restarting a service all changes to data will be reset.

Several tasks have to be performed on the systems within a certain timeframe. Both effectiveness and efficiency should be measured for each version.

To build and start services and components, several scripts are available (see `_scripts` folder in each workspace). The `.sh` scripts should work for both Linux and Mac and should also work with GitBash or Cygwin on Windows. Alternatively, there is also a folder with `.bat` scripts for the Windows command line (`_scripts/win`).


## Prerequisites to run the project:

- Make sure a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) >=1.8 is installed and that the `JAVA_HOME` variable is set accordingly.
- Make sure [Maven](https://maven.apache.org/download.cgi) >=3.5.0 is installed and `mvn` is available from the command line.
- Make sure [Node.js](https://nodejs.org/en/download) >=8.0.0 is installed and that `npm` is available from the command line.
- For Version 2 of the system: make sure [Apache Kafka](https://kafka.apache.org/downloads) >=1.1.0 is installed. The Windows build scripts for Kafka and Zookeeper currently expect all related files to reside in `C:\dev\apache-kafka`. If you installed Kafka somewhere else, be sure to adjust `_scripts\win\1_start-zookeeper.bat` and `_scripts\win\2_start-kafka.bat` with your custom path. In the provided Ubuntu VM, both Zookeeper and Kafka are already installed and run as services. So you won't need start scripts.
- Install a Java IDE (recommended: [IntelliJ](https://www.jetbrains.com/idea/download)
- Install a Web IDE (recommended: [Visual Studio Code](https://code.visualstudio.com/download))
- Install a modern web browser (recommended: [Mozilla Firefox](https://www.mozilla.org/en-US/firefox))

## Description of the WebShop
It consists of several RESTful Java services that communicate via HTTP requests.

### Directory and Folder Structure

There are three special folders in the root directory:

- `_docs`: This folder holds general documentation about the system, namely an architecture diagram of the initial state, another one for the final state, and a document that briefly describes each component in the system.
- `_exercises`: This folder holds the 3 concrete exercise descriptions that have to be performed on the system.
- `_scripts`: This folder holds 2 versions of scripts to build and run the components in the system, `.sh` files for Linux/Mac and `.bat` files for Windows.

Moreover, every component in the system has a separate folder in this directory. Apart from the `WebUI`, they are all RESTful services, which is indicated by the `Srv` part of their folder name. Every Java service in this workspace is a Maven project and is defined via its `pom.xml` file. Maven commands like `mvn clean install` are used in the various files in the `_scripts` folder to build the executable `.jar` file for each service. The details for this are documented in the `README.md` file of each service.

### RESTful Services Implementation

The services use the [Dropwizard](http://www.dropwizard.io/1.3.1/docs/) Java framework to provide their RESTful HTTP APIs. They have two configuration files (`pom.xml` and `config.yml`) and all follow the same package structure:

- `webshop.{serviceDomain}`  
  The top-level package of the service that holds the `ServiceApplication` class which is the entry point and the `ServiceConfiguration` class that provides getters and setters for all custom parameters in the `config.yml` file. These two classes will not be modified during the exercises.
- `webshop.{serviceDomain}.api`  
  The `api` package holds all model classes of the service, i.e. domain entities as well as request and response classes. Some of these classes may also be present in other services, because they operate on the same entities.
- `webshop.{serviceDomain}.db`  
  The `db` package holds a repository class that provides operations to retrieve, store, update, or delete the domain entities the service is responsible for. It acts as the sole access point to a persistent database, although none is present in this prototype implementation.
- `webshop.{serviceDomain}.health`  
  The `health` package holds a health check class for operational/administrative purposes. It will not be modified during the exercises.
- `webshop.{serviceDomain}.resources`  
  The `resources` package holds a resource class that specifies all provided REST operations of the service, i.e. its interface. It uses annotations to indicate the path, e.g. `@Path("/products")`, and the HTTP method, e.g. `@GET`. Most changes will have to be performed in these resource classes.

## How to run

Start the services by executing all `*srv-build-and-run.sh` files in `_scripts` (once a service has been built, its `*srv-run.sh` script can be used for future runs). Start the WebUI as well via `web-ui-build-and-run.sh`. Then, in your browser, navigate to `http://localhost:5000` and check if everything is working as expected. Play around with some of the buttons. Navigate to some of the `GET` resources of some services in your browser, e.g. `http://localhost:8050/products` or `http://localhost:8000/customers`, and have a look at the JSON responses.