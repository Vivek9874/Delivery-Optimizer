package com.logistics.delivery_optimizer.Controller;

import com.logistics.delivery_optimizer.Model.Order;
import com.logistics.delivery_optimizer.Service.RouteOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {


    private final RouteOptimizationService routeOptimizationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RouteController(RouteOptimizationService routeOptimizationService, SimpMessagingTemplate messagingTemplate) {
        this.routeOptimizationService = routeOptimizationService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/optimize")
    public ResponseEntity<List<Order>> optimizeRoute() {
        List<Order> optimizedOrders = routeOptimizationService.optimizeRoute();
        if (optimizedOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(optimizedOrders);
    }

    @PatchMapping("/update-status/{orderId}" )
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId){
        Order updatedOrder = routeOptimizationService.updateOrderStatus(orderId, Order.Status.DELIVERED);
        System.out.println("Updated Order object: " + updatedOrder);

        //check if the order exists and was updated
        if(updatedOrder != null){
            //bypassing the Order object completely to test the channel.
            /*String testPayload = "{\"status\": \"SUCCESS\", \"message\": \"This is a test message from the server\"}";
            System.out.println("Attempting to send test payload to /topic/route-updates");
            messagingTemplate.convertAndSend("/topic/route-updates", testPayload);*/

            messagingTemplate.convertAndSend("/topic/order-updates", updatedOrder);
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }
}
