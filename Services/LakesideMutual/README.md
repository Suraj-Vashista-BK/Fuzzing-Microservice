# Lakeside Mutual
Lakeside Mutual is a fictitious insurance company which serves as a sample application to demonstrate microservices and domain-driven design. The company provides several digital services to its customers and its employees.
Microservice API Patterns (MAP) are applied in the application backends.

## Architecture Overview
The following diagram shows an overview of the core components that are the building blocks for the services Lakeside Mutual provides to its customers and its employees:

The following sections contain a short description of each service:

- **Customer Core**  
  The Customer Core backend is a Spring Boot application that manages the personal data about
  individual customers. It provides this data to the other backend services through an HTTP resource API.

- **Customer Self-Service Backend**  
  The Customer Self-Service backend is a Spring Boot application that
  provides an HTTP resource API for the Customer Self-Service frontend.

- **Customer Self-Service Frontend**  
  The Customer Self-Service frontend is a React application that allows users to register themselves, view their current insurance policy and change their address.

- **Customer Management Backend**  
  The Customer Management backend is a Spring Boot application that
  provides an HTTP resource API for the Customer Management frontend and the Customer Self-Service frontend. In addition, WebSockets are used to implement the chat feature to deliver chat messages in realtime between the callcenter agent using the Customer Management frontend and the Customer logged into the Self-Service frontend.

- **Customer Management Frontend**  
  The Customer Management frontend is a React application that allows Customer-Service operators to interact with customers and help them resolve issues related to Lakeside Mutual's insurance products.

- **Policy Management Backend**  
  The Policy Management backend is a Spring Boot application that provides an HTTP resource API for the Customer Self-Service frontend and the Policy Management frontend. It also sends a message (via ActiveMQ messaging) to the Risk Management Server whenever an insurance policy is created / updated.

- **Policy Management Frontend**  
  The Policy Management frontend is a Vue.js application that allows Lakeside Mutual employees to view and manage the insurance policies of individual customers.

- **Risk Management Server**  
  The Risk-Management server is a Node.js application that gathers data about customers / policies and can generate a customer data report on demand.

- **Risk Management Client**  
  The Risk-Management client is a command-line tool built with Node.js. It allows the
  professionals of Lakeside Mutual to periodically download a customer data report which helps them during risk assessment.

- **Eureka Server**  
  The Eureka Server provides a service registry. It is a regular Spring Boot application to which all other Spring services can connect to access other services. For example, the Customer Self-Service Backend uses Eureka to connect to the Customer Core. Usage of Eureka is optional.

- **Spring Boot Admin**  
  Spring Boot Admin is an open source software for managing and monitoring Spring Boot applications. It *is* a Spring Boot application too. Usage within the Lakeside Mutual services is optional and only included for convenience with all security disabled.

The backends use Domain-Driven Design (DDD) to structure their domain (business) logic and their service-internal logical layers. To do so, they use marker interfaces defined in this Domain-Driven Design Library.

## Getting started

Detailed setup instructions can be found in each application's README file. To conveniently start all applications, the `run_all_applications` scripts can be used. Alternatively, to start a minimal subset of applications, i.e., the Customer Management Applications and the Customer Core, use the `run_customer_management_applications` script.

1. Make sure you have Java 8 or higher installed.
2. Install Node. Version 12 or later is required. You can check the currently installed version by running `node --version`.
3. Install Python. We don't use Python ourselves, but some Node.js packages require native addons that are built using node-gyp, which requires Python.
4. Install Maven.
5. Run the `run_all_applications` script suitable for your platform. Note that the frontend applications might be running before the backends are ready. In that case, just reload the page in the browser.

The following table lists all the ports that have to be free for each component to work correctly. If you need to change any of these ports, please
consult the README of the corresponding component:

| Component  | Ports |
| ---------- | ----- |
| Customer Self-Service Backend | 8080 (HTTP resource API) |
| Policy Management Backend | 8090 (HTTP resource API)<br/>61613 (ActiveMQ broker)<br/>61616 (ActiveMQ broker) |
| Customer Management Backend | 8100 (HTTP resource API) |
| Customer Core | 8110 (HTTP resource API) |
| Customer Self-Service Frontend | 3000 (Web server) |
| Policy Management Frontend | 3010 (Web server) |
| Customer Management Frontend | 3020 (Web server) |
| Risk Management Server | 50051 (gRPC server) |
| Risk Management Client | - (CLI Client) |
| Eureka Server | 8761 (Admin web frontend) |
| Spring Boot Admin | 9000 (Web server) |


## Docker

All projects come with Dockerfiles that can be used to run the services as Docker containers. The docker-compose.yml builds and starts all applications in a single command. See the docker-compose.yml for more information. Note that building the images takes some time (without caches, our most recent build took 6 minutes on a development machine).

## Data Stores

Each backend service has its own data store. The Spring-JPA based applications all use the H2 relational database. By default, all data will be lost during restarts, please see the individual README files to enable durable persistency. The backend services also contain the H2 Console to browse the database. It can be found at `/console`. For example, for the Customer Core, the address is [http://localhost:8110/console](http://localhost:8110/console).