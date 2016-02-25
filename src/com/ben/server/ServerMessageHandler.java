package com.ben.server;

@FunctionalInterface
public interface ServerMessageHandler {
    
    void handle(Object obj);
    
}
