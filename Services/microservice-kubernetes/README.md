# Microservice Kubernetes Sample

This project is code wise exactly same to [microservice](https://github.com/Suraj-Vashista-BK/microservice).

However, this demo uses Kubernetes as Docker environment. Kubernetes also support service discovery and load balancing. An Apache httpd as a reverse proxy routes the calls to the services.

This project creates a complete microservice demo system in Docker containers. The services are implemented in Java using Spring and Spring Cloud.

It uses three microservices:
* `Order` to process orders.
* `Customer` to handle customer data.
* `Catalog` to handle the items in the catalog.

## How to run

### Installation
* Install minikube. Minikube is a Kubernetes environment in a virtual machine that is easy to use and install. It is not meant for production but to test Kubernetes or for developer environments.
* Install kubectl. This is the command line interface for Kubernetes.

### Build
Change to the directory microservice-kubernetes-demo and run ./mvnw clean package or ./mvnw.cmd clean package (Windows).

### Run the containers

* Create a Minikube instance with `minikube start --memory=4000`. This
  will set the memory of the Kubernetes VM to 4.000 MB - which should
  be enough for most experiments:

```
[~/microservice-kubernetes]minikube start --memory=4000
Starting local Kubernetes v1.7.5 cluster...
Starting VM...
Getting VM IP address...
Moving files into cluster...
Setting up certs...
Connecting to cluster...
Setting up kubeconfig...
Starting cluster components...
Kubectl is now configured to use the cluster.
```
* If you created your own Docker images: Ensure that the environment
  variable `DOCKER_ACCOUNT` is set to the name of the account on Docker
  Hub you created.

* Run `kubernetes-deploy.sh` in the directory
  `microservice-kubernetes-demo` :

```
[~/microservice-kubernetes/microservice-kubernetes-demo]./kubernetes-deploy.sh
deployment "apache" created
service "apache" exposed
deployment "catalog" created
service "catalog" exposed
deployment "customer" created
service "customer" exposed
deployment "order" created
service "order" exposed
```

An alternative is to use the command `kubectl apply -f
microservices.yaml` . This command takes the description of the
services and deployments from the file `microservices.yaml` and
creates them if they do not already exist. The YAML uses the images
from the Docker Hub account `ewolff`. You will need to modify the YAML
if you want to use different images.

That deploys the images. It creates Pods. Pods might contain one or
many Docker containers. In this case each Pod contains just one
Docker container.

Also services are created. Services have a clusterwide unique IP
adress and a DNS entry. Service can use many Pods to do load
balancing. To actually view the services:

* Run `kubectl get services` to see all services:

```
[~/microservice-kubernetes/microservice-kubernetes-demo]kubectl get services
NAME                CLUSTER-IP   EXTERNAL-IP   PORT(S)          AGE
apache              10.0.0.90    <pending>     80:31214/TCP     46s
catalog             10.0.0.219   <pending>     8080:30161/TCP   46s
customer            10.0.0.163   <pending>     8080:30620/TCP   45s
kubernetes          10.0.0.1     <none>        443/TCP          3m
order               10.0.0.21    <pending>     8080:30616/TCP   45s
```
* Run `kubectl describe services` for more
  details. This also works for pods (`kubectl describe pods`) and
  deployments (`kubectl describe deployments`).

```
[~/microservice-kubernetes/microservice-kubernetes-demo]kubectl describe services
...

Name:			order
Namespace:		default
Labels:			run=order
Annotations:		<none>
Selector:		run=order
Type:			LoadBalancer
IP:			10.0.0.21
Port:			<unset>	8080/TCP
NodePort:		<unset>	30616/TCP
Endpoints:		172.17.0.7:8080
Session Affinity:	None
Events:			<none>
```

* You can also get a list of the pods:

```
[~/microservice-kubernetes/microservice-kubernetes-demo]kubectl get pods
NAME                                READY     STATUS    RESTARTS   AGE
apache-3412280829-k5z5p             1/1       Running   0          2m
catalog-269679894-60dr0             1/1       Running   0          2m
customer-1984516559-1ffjk           1/1       Running   0          2m
order-2204540131-nks5s              1/1       Running   0          2m
```

* Run `kubectl port-forward deployment/apache 8081:80` to create a
  proxy to the Apache httpd server on your local machine. Then open
  `http://localhost:8081` to see the web page of the Apache httpd
  server in the web browser.

* To remove all services and deployments run `kubernetes-remove.sh`:

```
[~/microservice-kubernetes/microservice-kubernetes-demo]./kubernetes-remove.sh 
service "apache" deleted
service "catalog" deleted
service "customer" deleted
service "order" deleted
deployment "apache" deleted
deployment "catalog" deleted
deployment "customer" deleted
deployment "order" deleted
```

This skript must be executed before a new version of the pods can be deployed.
