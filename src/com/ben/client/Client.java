package com.ben.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.ben.network.Messenger;

public class Client implements Messenger {
    private ConnectionToServer server;
    private LinkedBlockingQueue<Object> messages;
    private Socket socket;
    private ArrayList<ClientMessageHandler> messageHandlers;
    
    
    public Client(String IPAddress, int port) {
        try { socket = new Socket(IPAddress, port); } catch(IOException e) { e.printStackTrace(); }
        messages = new LinkedBlockingQueue<>();
        try { server = new ConnectionToServer(socket); } catch(IOException e) { e.printStackTrace(); }
        messageHandlers = new ArrayList<>();

        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        for (ClientMessageHandler h : messageHandlers) h.handle(message);
                    }
                    catch(InterruptedException ignored){ }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }
    
    public void addHandler(ClientMessageHandler h) {
        messageHandlers.add(h);
    }

    private class ConnectionToServer {
        ObjectInputStream in;
        ObjectOutputStream out;
        private Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.setSocket(socket);
            
            out = new ObjectOutputStream(this.getSocket().getOutputStream());
            in = new ObjectInputStream(this.getSocket().getInputStream());
            
            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            messages.put(obj);
                        }
                        catch(IOException | ClassNotFoundException | InterruptedException e){ e.printStackTrace(); }
                    }
                }
            };

            read.setDaemon(true);
            read.start();
        }

        private void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}


    }
    
    public void sendMessage(String s) {
    	send(s);
    }

    public void send(Object obj) {
        server.write(obj);
    }
}