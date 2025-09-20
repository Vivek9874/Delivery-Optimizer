package com.logistics.delivery_optimizer.Controller;

import com.logistics.delivery_optimizer.Model.Order;
import com.logistics.delivery_optimizer.Service.RouteOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {


    private final RouteOptimizationService routeOptimizationService;

    @Autowired
    public RouteController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }

    @GetMapping("/optimize")
    public ResponseEntity<List<Order>> optimizeRoute() {
        List<Order> optimizedOrders = routeOptimizationService.optimizeRoute();
        if (optimizedOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(optimizedOrders);
    }
}
