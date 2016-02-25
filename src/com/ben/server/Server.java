package com.ben.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.ben.network.Messenger;

public class Server implements Messenger {
    private ArrayList<ConnectionToClient> clientList;
    private LinkedBlockingQueue<Object> messages;
    private LinkedBlockingQueue<ConnectionToClient> messagesFrom;
    private ArrayList<ServerMessageHandler> messageHandlers;
    private ServerSocket serverSocket;
    public boolean sendWhenReceived = true;

    public Server(int port) {
        clientList = new ArrayList<>();
        messages = new LinkedBlockingQueue<>();
        messagesFrom = new LinkedBlockingQueue<>();
        try { serverSocket = new ServerSocket(port); } catch (IOException e1) { e1.printStackTrace(); }
        messageHandlers = new ArrayList<>();
        
        System.out.println("construct");
        
        Thread accept = new Thread() {
            public void run(){
            	System.out.println("Accepting...");
                while(true){
                    try{
                        Socket s = serverSocket.accept();
                        System.out.println("Accepted!");
                        clientList.add(new ConnectionToClient(s));
                    }
                    catch(IOException e){ e.printStackTrace(); }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();

        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object obj = messages.take();
                        ConnectionToClient from = messagesFrom.take();
                        
                        if (sendWhenReceived) {
                            clientList.stream().filter(c -> !(c.socket.equals(from.socket))).forEach(c -> c.write(obj));
                        }
                        for (ServerMessageHandler h : messageHandlers) h.handle(obj);
                    }
                    catch(InterruptedException ignored){ }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }
    
    public void addHandler(ServerMessageHandler h)  {
        messageHandlers.add(h);
    }

    private class ConnectionToClient {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;
        ConnectionToClient This;

        ConnectionToClient(Socket socket) throws IOException {
            this.This = this;
            this.socket = socket;
            ConnectionToClient thing = this;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                        	Object obj;
                            try { obj = in.readObject(); }
                            catch(SocketException e) {
                            	clientList.remove(This);
                            	break;
                            }
                            messages.put(obj);
                            messagesFrom.put(thing);
                        }
                        catch(IOException | ClassNotFoundException | InterruptedException e){ e.printStackTrace(); }
                    }
                }
            };

            read.setDaemon(true); // terminate when main ends
            read.start();
        }

        public void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }
    }
    
    public void sendMessage(String s) {
    	sendToAll(s);
    }

    public void sendToOne(int index, Object message)throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(Object message){
        for(ConnectionToClient client : clientList)
            client.write(message);
    }
    
}
