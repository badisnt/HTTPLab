
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

We created an HTTP API that manages a list of bookings at a restaurant. The bookings are in JSON format and contain the following:

![Step 2 Screenshot 1](https://github.com/badisnt/HTTPLab/blob/main/figures/step3_1.png)

The base code for the API is from the example code for [6-http](https://github.com/HEIGVD-Course-DAI/dai-codeexamples/tree/main/6-http) of DAI. 

It supports all CRUD operations (Create, Read, Update, Delete).

Like the static server, we create a Dockerfile that creates a container for our server, and add the food-api service to the ```docker-compose.yml```.

We then ran tests of the CRUD operations by using Insomnia and sending requests to ```http://localhost:7070``` and making sure that we can Create, Read, Update and Delete the bookings stored in the server.
