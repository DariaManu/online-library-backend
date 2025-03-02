<a id="readme-top"></a>

# Online Library Backend

<details>
    <summary>Table of Contents</summary>
    <ol>
        <li>
            <a href="#about-the-project">About the Project</a>
        </li>
        <li>
            <a href="#getting-started">Getting Started</a>
            <ul>
                <li><a href="#prerequisites">Prerequisites</a></li>
                <li><a href="#isntallation">Installation</a></li>
            </ul>
        </li>
        <li><a href="#usage">Usage</a></li>
        <li>
            <a href="#detailing-the-services">Detailing the Services</a>
            <ul>
                <li><a href="#service-registry">service-registry</a></li>
                <li><a href="#api-gateway">api-gateway</a></li>
                <li><a href="#user-service">user-service</a></li>
                <li><a href="#book-service">book-service</a></li>
                <li><a href="#notification-service">notification-service</a></li>
                <li><a href="#aws-lambda-function">AWS Lambda Function</a></li>
            </ul>
        </li>
    </ol>
</details>

## About the Project

This project was an assignment for the Service Oriented Architectures course, during 
my second year of master's studies. The goal was to capture the characteristics 
of microservices based architectures, and show the use of certain frameworks and 
technologies that are frequently used in such systems. Thus, the Online 
Library web application project focuses more on technical aspects rather than 
having a large number of functionalities. Users of the application can create an 
account and access the web page with their credentials. After logging in, they are
redirected to the main page (and currently only page), where all the available books
are listed. Through the simple click of a button, they can download locally any book
they wish to read. Administrators of the system are responsible for adding new books,
and they can also check what the most popular genre amongst the uploaded books is
(the genre with the most number of downloads).

This repository includes only the backend services of the project, for the frontend 
project, check the [online-library-frontend](https://github.com/DariaManu/online-library-frontend) 
repository. If you want to run only the backend services, you are free to do so.

For a more visual presentation of the services, check the [diagrams](https://github.com/DariaManu/online-library-backend/tree/main/diagrams) directory.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Prerequisites

There are two main ways through which you can run the project. Depending on your choice, 
make sure you have the following installed locally on your computer.

<ol>
    <li>
        <p><b>Running the services on Docker</b></p>
        <p>
            This is the recommended way, as it is the simplest and fastest. All you have to
            do is to visit the <a href="https://www.docker.com/">Docker</a> website, and 
            follow the instructions to download Docker Desktop.
        </p>
    </li>
    <br>
    <li>
        <p><b>Running the services locally</b></p>
        <p>
            If you choose this approach, you will need the following:
            <ul>
                <li>Java 21</li>
                <li>PostgreSQL (but you can also use a different database of
                your choice)</li>
                <li>A running instance of RabbitMQ</li>
                <li>A running instance of Kafka</li>
            </ul>
        </p>
    </li>
</ol>

You can also choose to run locally only the java services and the database instance, and have
RabbitMQ and Kafka run inside Docker containers. To do so, you must install Docker Desktop,
and run the following command for RabbitMQ
```shell
docker run -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management
```
and the following command for Kafka
```shell
docker run -d --name=kafka -p 9092:9092 apache/kafka
```

To determine the most popular book genre, an HTTP call is made to and AWS Lambda Function. For 
security reasons, the Function URL is passed as an environment variable and is not included in this
repository. If you wish to use the most popular book genre feature, you must create your own Lambda
Function (Check details in the <a href="#aws-lambda-function">AWS Lambda Function</a> subsection). 

The body of the function contains all the logged book downloads. The function 

### Installation

1. Clone the repo
```shell
git clone https://github.com/DariaManu/online-library-backend
```

2. Open the project in your preferred IDE (IntelliJ for example)
3. Depending on how you chose to run the project, you will have to make the following changes
   
    3.a If you chose to run the project in Docker
    * Open the docker-compose.yml file located in the base directory
    * Make any changes you deem necessary to the environment variables or port values (if the ports overlap with other applications running on your machine)
    * Make sure that at least one instance of the user-service service runs on port 8080
    * If you do not wish to run the frontend of the application, remove the ol-navbar-component and ol-frontend sections

    3.b If you choose to run the project locally
    * Create three databases in PostgreSQL (or the DB of your choice): olb-user-service, olb-book-service and olb-notification-service
    * Start RabbitMQ and Kafka
    * Each service uses environment variables for its configuration, so make sure you set them either
    in the Run/Debug Configurations panel, or in the command line. You can check the application.yml file
    of each module to see what environment variables you need to add (or the docker-compose.yml file)
    * If you chose to use another DB, change the <b>spring.datasource.driver-class-name</b> and the <b>spring.jpa.properties.hibernate.dialect</b>
   properties in user-service, book-service and notification-service accordingly


## Usage

In order to use the project

* Start Docker Desktop, open a terminal window in the project's root directory 
(where the docker-compose.yml file is located) and run
    ```shell
    docker compose up
    ```

or

* Run each service either from within the IDE, or using the locally installed maven, 
or using the mvnw/mvnw.cmd files that come with the project. Remember, you must start RabbitMQ and Kafka
before starting the services. Also, it is important to start the service-registry first, and the api-gateway
service last.

Congratulations, the services should now be up and running. 

## Detailing the Services

The following section briefly explains what each service is responsible for.

### service-registry

### api-gateway

### user-service

### book-service

### notification-service

### AWS Lambda Function

As mentioned previously, if you want to use the most popular book genre functionality,
you must create your own Lambda Function. After creating the function, generate a Function URL and add
it as environment variable for the notification-service. Below is my implementation of the function. You 
use copy it and use it for yourself, or create a new implementation.
```javascript
export const handler = async (event) => {
  let data = JSON.parse(event.body);

  const frequencyMap = new Map();
  for (let book of data) {
    frequencyMap.set(book.genre, (frequencyMap.get(book.genre) || 0) + 1);
  }
  console.log(frequencyMap);

  let mostPopularGenre = ""; 
  let maxFrequency = 0;
  for (let [genre, frequency] of frequencyMap) {
    if (frequency > maxFrequency) {
      mostPopularGenre = genre;
      maxFrequency = frequency;
    }
  }

  let responseBody = {
    "mostPopularGenre": mostPopularGenre
  };
  
  const response = {
    statusCode: 200,
    body: JSON.stringify(responseBody)
  };
  return response;
};
```