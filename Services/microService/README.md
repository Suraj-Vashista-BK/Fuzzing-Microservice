# Microservice Sample

This project creates a VM with the complete microservice demo system in Docker containers inside a Vagrant VM. The services are implemented in Java using Spring and Spring Cloud.

It uses three microservices:

* Order to process orders.
* Customer to handle customer data.
* Catalog to handle the items in the catalog.

## Technologies

* Eureka for Lookup
* Ribbon for Load Balancing. See the classes CatalogClient and CustomerClient in com.ewolff.microservice.order.clients in the microservice-demo-order project.
* Hystrix is used for resilience. See CatalogClient in com.ewolff.microservice.order.clients in the microservice-demo-order project . Note that the CustomerClient won't use Hystrix. This way you can see how a crash of the Customer microservices makes the Order microservice useless.
* Hystrix has a dashboard. Turbine can be used to combine the data from multiple sources. However, this does not work at the moment.
* Zuul is used to route HTTP requests from the outside to the different services.
* Spring Cloud Config isn't used. It is disabled with spring.cloud.config.enabled=false in the bootstrap f

## Step-by-step instruction to run the Project

### Installation

* The example is implemented in Java . The examples need to be compiled, so you need to install a JDK (Java Development Kit). A JRE (Java Runtime Environment) is not sufficient. You should be able to execute java and javac on the command line.
*
* The example run in Docker Containers. You need to install Docker Community Edition, see https://www.docker.com/community-edition/ . You should be able to run docker after the installation.

* The example need a lot of RAM. You should configure Docker to use 4 GB of RAM otherwise Docker containers might be killed due to lack of RAM.

* After installing Docker you should also be able to run docker-compose. If this is not possible, you might need to install it separately. See https://docs.docker.com/compose/install/ .

### Build

Change to the directory microservice-demo and run `./mvnw` clean package or `./mvnw.cmd clean package` (Windows).

### Run the containers

First you need to build the Docker images. Change to the directory docker and run `docker-compose build`

You can access:

* The application through Zuul at http://localhost:8080/
* The Eureka dashboard at http://localhost:8761/
* The Hystrix dashboard at http://localhost:8989/

You can terminate all containers using docker-compose down.