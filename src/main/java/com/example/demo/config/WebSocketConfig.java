package com.example.demo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.net.http.WebSocket;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    @Lazy
    private WebSocketAuthChannelInterceptor authChannelInterceptor;

    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(authChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        // This is the endpoint clients will connect to
        // "/ws" is the common convention

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // Defines prefixes for messages that are "routed" to the broker
        // (i.e., messages to be broadcast to other clients)

        registry.enableSimpleBroker("/topic", "/user");

        // Defines the prefix for messages that are "handled" by your @MessageMapping methods
        // (i.e., messages sent from a client to your server)

        registry.setApplicationDestinationPrefixes("/app");

        // Defines the prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");

    }
}
