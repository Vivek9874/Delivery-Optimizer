package com.logistics.delivery_optimizer.Service;

import com.logistics.delivery_optimizer.Model.Order;
import com.logistics.delivery_optimizer.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private  final OrderRepository orderRepository;
    private final GeocodingService geocodingService;

    @Autowired
    public OrderService(OrderRepository orderRepository, GeocodingService geocodingService) {
        this.orderRepository = orderRepository;
        this.geocodingService = geocodingService;
    }

    public Order createOrder(Order order){

        Optional<double[]> coordinates = geocodingService.getCoordinates(order.getAddress());

        if(coordinates.isPresent()){
            double[] latLon = coordinates.get();
            order.setLatitude(latLon[0]);
            order.setLongitude(latLon[1]);
        }
        else {
            System.err.println("Geocoding failed for address: " + order.getAddress());
        }
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void deleteById(Long id) {
        if (!orderRepository.existsById(id)) {
            System.err.println("Attempted to delete a non-existent order with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}
