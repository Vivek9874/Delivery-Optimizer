
-----

# Delivery Optimizer

## Description

This project is a real-time logistics and delivery optimization system designed to streamline the delivery process. It takes pending orders, calculates the most efficient delivery route, and provides real-time updates on the status of each order. The application is built with Java and the Spring Boot framework, leveraging external APIs for geocoding and route optimization.

## Features

* **Order Management**: Create, retrieve, and manage delivery orders through a RESTful API.
* **Automatic Geocoding**: Automatically converts delivery addresses into geographic coordinates (latitude and longitude) using the Nominatim (OpenStreetMap) API.
* **Route Optimization**: Utilizes the Open Source Routing Machine (OSRM) API to calculate the optimal delivery route for all pending orders based on travel duration.
* **Greedy Algorithm**: Implements a greedy algorithm to determine the sequence of deliveries in the optimized route.
* **Real-time Updates**: Pushes real-time order status updates to clients using WebSockets.
* **Status Tracking**: Orders progress through various statuses: `PENDING`, `ASSIGNED`, and `DELIVERED`.

## Technologies Used

* **Java 21**
* **Spring Boot 3**
    * Spring Web
    * Spring Data JPA
    * Spring WebFlux
    * Spring WebSocket
* **Hibernate**
* **MySQL**
* **Maven**
* **Lombok**
* **External APIs**:
    * Nominatim (OpenStreetMap) for geocoding
    * OSRM (Open Source Routing Machine) for route optimization

## Getting Started

### Prerequisites

* Java 21 or later
* Maven
* MySQL

### Installation

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/vivek9874/delivery-optimizer.git
    cd delivery-optimizer
    ```

2.  **Configure the database:**

    * Open `src/main/resources/application.properties`.
    * Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties with your MySQL database credentials.
    * Ensure you have a database named `DeliveryOptimizer` or change the name in the `spring.datasource.url` property.

3.  **Build and run the application:**

    ```bash
    ./mvnw spring-boot:run
    ```

    The application will start on the default port `8080`.

## API Endpoints

### Order Management

* `POST /api/orders`: Create a new order.

    * **Request Body**:
      ```json
      {
        "customerName": "John Doe",
        "address": "1600 Amphitheatre Parkway, Mountain View, CA",
        "slaMinutes": 60
      }
      ```

* `GET /api/orders`: Get all orders.

* `GET /api/orders/{id}`: Get an order by its ID.

* `DELETE /api/orders/{id}`: Delete an order by its ID.

### Route Optimization

* `GET /api/routes/optimize`: Trigger the route optimization process for all pending orders.
* `PATCH /api/routes/update-status/{orderId}`: Update the status of an order to `DELIVERED`.

### WebSocket Endpoint

* Connect to `/ws` to receive real-time order status updates.
* Subscribe to the `/topic/order-updates` topic to get messages when an order's status is updated.