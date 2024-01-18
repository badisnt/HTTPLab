
# DAI Lab 5 Report - HTTP infrastructure


The main objective of this lab is to learn to build a complete Web infrastructure. This means, we will build a server infrastructure that serves a static Web site and a dynamic HTTP API.

Below is a recap of how every step was implemented:


# Step 1: Static Web site

The static website is based on an html template from Free-CSS for a [restaurant website](https://www.free-css.com/free-css-templates/page240/italian-restaurant).

The folder ```static``` contains:
- The website template under the folder ```ristorante```
- The ```Dockerfile``` based on the Nginx:latest image copies the site template and nginx configuration into the image.
- ```nginx.cong``` is the default config file copied from the container, which we then modify to add the following server configurations:
    - ```listen```: listen on port 80
    - ```location```: specify the root directory for the website and the default index file, which helps handling requests
    - ```server_name```: set the server name 

To build and run the image, we use the following commands:
1. ```docker build -t static``` : 
build the image "static"

2. ```docker run -d -p 8080:80 static``` : 
run the container and map 8080 to 80 inside the container

When the image is running, we can access the static website on our browser on ```http://localhost:8080/```:

![Step 1 Screenshot](https://github.com/badisnt/HTTPLab/blob/main/figures/step1.png)

# Step 2: Docker Compose

For this step we create a ```docker-compose.yml``` file in which we specify only 1 service. 

This will be a container base on our image ```static``` based on the Dockerfile contained in the build context which is the folder ```static```. 

We also specify the port mapping like in Step 1.

To build and run, we use the following command: ```docker compose up -d```

Then we can test our website is accessible on ```http://localhost:8080/``` like in Step 1.

To stop our container, we can use the following command: ```docker compose down```

# Step 3: HTTP API server

In the folder ```food-api``` we created an HTTP API that manages a list of bookings at a restaurant. The bookings are in JSON format and contain the following:

![Step 3 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step3_1.png)

The base code for the API is from the example code for [6-http](https://github.com/HEIGVD-Course-DAI/dai-codeexamples/tree/main/6-http) of DAI. 

It supports all CRUD operations (Create, Read, Update, Delete).

However, we still need to implement input verification to verify that dates and times for reservations are valid. If part of the input is not valid on a PUT/POST request, we can just return a 4xx error and not modify the database.

Like the static server, we create a Dockerfile that creates a container for our server, and add the food-api service to the ```docker-compose.yml```.

We then ran tests of the CRUD operations by using Insomnia and sending requests to ```http://localhost:7070``` and making sure that we can Create, Read, Update and Delete the bookings stored in the server.

# Step 4: Reverse proxy with Traefik

We start by creating a new service in our ```docker-compose.yml``` file. 

We start with the [basic configuration](https://doc.traefik.io/traefik/getting-started/quick-start/) and configure it to work with docker by modifying our [other services](https://doc.traefik.io/traefik/routing/providers/docker/) to enable Traefik, specify the requests to relay and expose the appropriate ports.

We then use ```docker compose up -d``` to build and run.

We can now access both servers on our browser through ```http://localhost:80/``` and ```http://localhost:80/api/bookings``` respectively. 

The Traefik dashboard can be accessed through ```http://localhost:8080/``` to check the routing configuration:

![Step 4 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step4_1.png)

# Step 5: Scalability and load balancing

In the ```docker-compose.yml``` file, we add the following for each of our 2 servers:
```
deploy:
    replicas: <nb of instances>
```

If we want to change the number of instances while the container is running, we can run the following command:

```
docker compose -d --scale <static>=<nb of instances> --scale <food-api>=<nb of instances>
```

To check scalability is working, we can go to the dashboard and look at the services. They should each have the number of instances on the right:

![Step 5 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step5_1.png)

To check that Traefik is handling the load balancing: 
- We can check the logs using ```docker-compose logs -f <static / food-api>```. Upon making multiple requests, we can see that they are distributed to the instances in a round-robin manner.
- We can also use Insomnia to add a new booking to our ```food-api``` server then make GET requests. The new booking only appears if the request is routed to the same server that handled the POST request. Since our database is stored in memory and we don't have sticky sessions, this is proof that Traefik is routing the requests to the different instances to balance the load.

# Step 6: Load balancing with round-robin and sticky sessions
### Sticky Sessions
In the ```docker-compose.yml``` file we add the following labels to our food-api service:
```
- "traefik.http.services.api.loadbalancer.sticky.cookie=true"
- "traefik.http.services.api.loadbalancer.sticky.cookie.name=peanut-butter-cookie"
```
This enables the use of cookies for sticky sessions, and gives a name to our cookie so we can make sure it is working later in our Insomnia testing.

To verify that this is working, we build and run as usual then use Insomnia. 

We notice that upon our first GET request, the cookie is set which allows the load balancer to identify which instance to relay to later.

![Step 6 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step6_1.png)


We can then do a POST request, followed by GET request. We notice that the new booking is returned by GET. This proves that the same instance is handling the request since the data is stored in memory and not in a database.

### Round-robin

We don't change anything about our implementation of the static server.

We can make multiple requests to the static server and then consult the docker compose logs to verify that the requests are relayed to the instances in a round-robin manner.

# Step 7: Securing Traefik with HTTPS

We start by generating the encryption key and certificate using the following openssl command in the ```certificates``` folder:
```
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout key.pem -out cert.pem
```
In our ```docker-compose.yml``` file, we mount the volume ```certificates``` to ```/etc/traefik/certificates``` in the container.

We then create a ```traefik.yaml``` configuration file for Traefik, that we also mount as a volume in the container. This file contains the necessary configuration:
- **providers**: Traefik will read configurations from Docker
- **entrypoints**: the entrypoints for http(80) and https (443)
- **api**: enable the dashboard and insecure access for testing
- **tls**: configure the tls certificates to be used

Finally, in ```docker-compose.yml``` and for each server, we add the labels to activate the HTTPS entrypoint and set TLS to true.

We should now be able to access both of our servers through ```https://localhost/``` and ```https://localhost/api/bookings``` respectively.

In the dashboard, we can also see that the entrypoints for our 2 servers are HTTPS 443.

![Step 7 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step7_1.png)

# Optional step 1: Management UI

We will use an existing service: Portainer.

In our ```docker-compose.yml``` file, we add a new service based on the Portainer image, and onfigure it to port 9000.

After building and running, we can access the UI through ```localhost:9000```. 

We need to start by creating a password for the admin user. Then we can login to the UI Dashboard.

Here, we can see the list of containers running in our infrastructure.

![Step 8 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step8_1.png)

We can select a container and: 
- We can use the  ```Stop```/```Start``` buttons using the Stop or Start buttons. 
- We can use the ```Duplicate/Edit``` button to create a new instance then deploy it.  
- We can use the ```Remove``` button to remove an instance.

![Step 8 Screenshot 2](https://github.com/badisnt/HTTPLab/blob/main/figures/step8_2.png)

# Optional step 2: Integration API - static Web site

We have modified the ```index.html``` static website template. 
- We added a container for the information that we will pull from the server. For now this is placed on a random spot on the website, but it can be changed to allow a more user-friendly display.
- We added JavaScript code to periodically make GET requests to the API server and update the content on the page in the html container we created. Since we do not have a lot of experience with JavaScript, we based our script on a function generated by ChatGPT, that we then adapted to our specific case.

![Step 9 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step9_1.png)
