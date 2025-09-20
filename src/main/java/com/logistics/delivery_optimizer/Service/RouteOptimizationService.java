package com.logistics.delivery_optimizer.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.logistics.delivery_optimizer.Model.Order;
import com.logistics.delivery_optimizer.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteOptimizationService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Autowired
    public RouteOptimizationService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClient = webClientBuilder.baseUrl("http://router.project-osrm.org/table/v1/driving/").build();
        //this.webClient = webClientBuilder.baseUrl("https://api.openrouteservice.org/v2/directions/driving-car").build();
    }

    //local blinkit dark store
    private final double HUB_LATITUDE = 19.387436500246842;
    private final double HUB_LONGITUDE = 72.82887130020333;

    public List<Order> optimizeRoute(){

        List<Order> pendingOrders = orderRepository.findByStatus(Order.Status.PENDING);
        if(pendingOrders.isEmpty()){
            System.out.println("No pending orders to optimize.");
            return new ArrayList<>();
        }

        //Osrm API expects coordinates in longitude,latitude format
        StringBuilder coordinatesBuilder = new StringBuilder();
        //Add hub coordinates first
        coordinatesBuilder.append(HUB_LONGITUDE).append(",").append(HUB_LATITUDE);
        for(Order order : pendingOrders){
            coordinatesBuilder.append(";")
                    .append(order.getLongitude())
                    .append(",")
                    .append(order.getLatitude());
        }

       /* Mono<JsonNode> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(coordinatesBuilder.toString())//put the coordinates in the path
                        .queryParam("sources", "0")
                        .queryParam("destinations", String.join(",", getDestinationIndices(pendingOrders)))
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class);*/

        Mono<JsonNode> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(coordinatesBuilder.toString())
                        .queryParam("annotations", "duration,distance")
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class);

        JsonNode response = responseMono.block();
        System.out.println("OSRM API Response: " + response.toPrettyString());


        //Greedy algorithm to determine route based on distance matrix
        List<Order> optimizedRoute = new ArrayList<>();
        if(response != null && response.get("code") .asText().equals("Ok")){
            //durations is a field returned by the ORSM API.
            //It contains a matrix of travel times between the source and destinations, which we can set using Indices of the Longitude & Latitude pairs passed in the URL
            JsonNode durationsMatrix = response.get("durations");
            System.out.println("OSRM API Durations Matrix Response: " + durationsMatrix.toPrettyString());
            System.out.println("OSRTM API Distance Matrix Response: " + response.get("distances").toPrettyString());

            int currentPositionIndex = 0; // Start from the hub, which is at index 0
            List<Order> unvisitedOrders = new ArrayList<>(pendingOrders); // Create a modifiable list of pending orders
            while(!unvisitedOrders.isEmpty()){
                //set the minimumDurarion to a very high value initially, so that any real duration will be lower
                double minDuration = Double.MAX_VALUE;
                Order nextOrder = null;
                int nextPointIndex = -1; //-1 indicates no next point found yet

                for(Order order: unvisitedOrders){
                    int orderIndex = pendingOrders.indexOf(order) + 1; // +1 because hub is at index 0
                    double duration = durationsMatrix.get(currentPositionIndex).get(orderIndex).asDouble();
                    if(duration < minDuration) {
                        minDuration = duration;
                        nextOrder = order;
                        nextPointIndex = orderIndex;
                    }
                }
                if(nextOrder != null){
                    optimizedRoute.add(nextOrder);
                    unvisitedOrders.remove(nextOrder);
                    currentPositionIndex = nextPointIndex;
                } else {
                    System.err.println("No next order found, something went wrong in the optimization logic.");
                    break;
                }
            }
            //now set the status of the order to ASSIGNED, after the completion of the optimization
            for(Order order : optimizedRoute){
                order.setStatus(Order.Status.ASSIGNED);
                orderRepository.save(order);
            }
        }
        return  optimizedRoute;
    }

    private List<String> getDestinationIndices(List<Order> pendingOrders) {
        List<String> indices = new ArrayList<>();
        for(int i=1; i<= pendingOrders.size(); i++){
            indices.add(String.valueOf(i));
        }
        return indices;
    }
}
