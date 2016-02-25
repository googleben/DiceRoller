package com.ben.client;

@FunctionalInterface
public interface ClientMessageHandler {
    
    void handle(Object obj);
    
}
