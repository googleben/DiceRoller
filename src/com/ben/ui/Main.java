package com.ben.ui;
import java.util.NoSuchElementException;
import java.util.Scanner;
import com.ben.client.Client;
import com.ben.server.Server;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	
    public int port = 2111;

	private static Stage stage;
    
	public static void main(String... args) {
		launch();
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		Main.stage = stage;
	}

	public void start(Stage stage) {
		
	    Main.stage = stage;
	    
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25,25,25,25));
		
		TextField ip = new TextField("127.0.0.1:2111");
		TextField name = new TextField("User");
		
		Button server = new Button("Start Server");
		server.setOnMouseClicked(e -> {
		    port = (int)splitIP(ip.getText())[1];
			begin("server", name.getText());
		});
		
		Button client = new Button("Start Client");
		client.setOnMouseClicked(e -> {
		    Object[] split = splitIP(ip.getText());
		    port = (int)split[1];
			begin("client", name.getText(), (String)split[0]);
		});
		
		Button local = new Button("Start Local");
		local.setOnMouseClicked(e->begin("local", ""));
		
		pane.add(ip, 0, 0);
		pane.add(name, 0, 1);
		pane.add(server,0,2);
		pane.add(client,0,3);
		pane.add(local, 0, 4);
		stage.setScene(new Scene(pane));
		stage.setTitle("Dice");
		stage.show();
		
	}
	
	/*
	 * args:
	 * args[0]: "server", "client", or "local" NON CASE SENSITIVE local not implemented
	 * args[1]: name for Die class
	 * args[2]: IP for client
	 */
	private void begin(String... args) {
	    Die.name = args[1];
	    if (args[0].equalsIgnoreCase("server")) {
	        Server server = new Server(port);
	        server.addHandler(Main::LogHandler);
	        DiceUI.messenger = server;
	    } else if (args[0].equalsIgnoreCase("client")) {
	        Client client = new Client(args[2], port);
	        client.addHandler(Main::LogHandler);
	        DiceUI.messenger = client;
	    } else {
	        DiceUI.networked = false;
	    }
	    new DiceUI().startMainStage();
	    
	}
	
	private static void LogHandler(Object o) {
	    if (o instanceof String) {
            String s = (String)o;
            DiceUI.log.add(s, false);
        }
	}
	
	private static Object[] splitIP(String s) {
	    String[] split = s.split(":");
	    return new Object[] { isIP(split[0]) ? split[0] : "127.0.0.1", split.length>1 ? Integer.parseInt(split[1]) : 2111 };
	}
	
	/**
	 * @param ip A string that should be in format "ip:port" or "ip"
	 * @return An Object[2] array: Object[0] = the IP or 127.0.0.1, Object[1] = the port or 2111`
	 */
	private static boolean isIP(String ip) {
	    Scanner s = new Scanner(ip);
	    for (int i = 0; i<4; i++) {
	        try {
	            int next = s.nextInt();
	            if (next>255 || next<0) {
	                s.close();
	                return false;
	            }
	        } catch(NoSuchElementException e) {
	            s.close();
	            return false;
	        }
	    }
	    s.close();
	    return true;
	}
	
}
