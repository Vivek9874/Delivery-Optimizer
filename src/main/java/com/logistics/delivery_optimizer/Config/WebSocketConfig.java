package com.logistics.delivery_optimizer.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configuration code here
        /*This line enables a simple message broker and sets the destination prefix for messages.
        The message is broadcasted to all connected clients subscribed to the "/topic" destination i.e name of the broadcast channel is topic*/
        config.enableSimpleBroker("/topic");
        //This line acts like an inbox for the server where all the messages sent from clients to the server are prefixed with "/app".
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        /*the below line registers the "/ws" endpoint that clients will use to connect to the WebSocket server.
        withSockJS() enables fallback options for browsers that don’t support WebSocket.*/
        registry.addEndpoint("/ws").setAllowedOrigins("*"); //.withSockJS(); // we don't use SockJS here, because we aren't interacting with browsers for this websocket
    }
}
