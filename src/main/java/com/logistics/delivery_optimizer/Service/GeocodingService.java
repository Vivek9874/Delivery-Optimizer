package com.logistics.delivery_optimizer.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GeocodingService {

    private final WebClient webClient;

    @Autowired
    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://nominatim.openstreetmap.org/search").build();
    }

    public Optional<double[]> getCoordinates(String address) {
        String queryUrl = "?q=" + address.replace(" ", "+") + "&format=json&limit=1";

        Mono<JsonNode> responseMono = webClient.get()
                .uri(queryUrl)
                .retrieve()
                .bodyToMono(JsonNode.class);

        JsonNode jsonResponse = responseMono.block();

        if (jsonResponse != null && jsonResponse.isArray() && !jsonResponse.isEmpty()) {
            JsonNode firstResult = jsonResponse.get(0);
            double lat = firstResult.get("lat").asDouble();
            double lon = firstResult.get("lon").asDouble();
            return Optional.of(new double[]{lat, lon});
        }
        return Optional.empty();
    }
}