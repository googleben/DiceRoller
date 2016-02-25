package com.ben.ui;

import com.ben.network.Messenger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DiceUI {

    private static final String FONT = "Courier New";

    public static Messenger messenger;
    public static boolean networked = true;
    
    public static int paneIndex = 0;
    
    public static int biggest = 3;
    
    public static Stage stage;
    
    public static Log log;
    
    public static TextArea logNode;
    
    public void startMainStage() {
        start(Main.getStage());
    }
    
    public void start(Stage stage) {
        
    	logNode = new TextArea();
    	
    	log = new Log();
    	
        DiceUI.stage = stage;
        
        GridPane mainPane = new GridPane();
        GridPane pane = new GridPane();
        
        mainPane.add(pane, 0, 0);
        mainPane.add(logNode, 0, 1);
        
        Scene s = new Scene(mainPane, 350, 500);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25,25,25,25));
        
        Label die = new Label("Die");
        Label mod = new Label("Mod");
        Label times = new Label("# of Dice");
        Label roll = new Label("Roll");
        Label result = new Label("Result");
        pane.add(die, 0, paneIndex);
        pane.add(mod, 1, paneIndex);
        pane.add(times, 2, paneIndex);
        pane.add(roll, 3, paneIndex);
        pane.add(result, 4, paneIndex++);
        
        int[] startingDice = {4,6,8,12,20,100};
        
        for (int i : startingDice) {
        	addObj(pane, new Die(i));
        }
        
        Button makeDie = new Button("Make Custom Die");
        
        makeDie.setOnAction(e -> makeDie(s, pane, makeDie));
        pane.add(makeDie, 0, paneIndex);
        GridPane.setColumnSpan(makeDie, 4);
        
        stage.setScene(s);
        stage.setOnCloseRequest((e) -> System.exit(0));
        stage.show();
        
    }
    
    private static void makeDie(Scene old, GridPane p, Button b) {
        p.getChildren().remove(b);
        GridPane selPane = new GridPane();
        selPane.setAlignment(Pos.CENTER);
        selPane.setHgap(10);
        selPane.setVgap(10);
        selPane.setPadding(new Insets(25,25,25,25));
        Label l = new Label("Sides:");
        TextField sidesArea = new TextField("6");
        sidesArea.setMaxWidth(50);
        Button make = new Button("Make die!");
        make.setOnMouseClicked(e -> {
            if (isNumber(sidesArea.getText())) {
                int sides = Integer.parseInt(sidesArea.getText());
                addObj(p, new Die(sides));
                p.add(b, 0, paneIndex);
                GridPane.setColumnSpan(b, 3);
                stage.setScene(old);
                stage.setHeight(150+(50*paneIndex));
                biggest = Math.max(sidesArea.getText().length(), biggest);
            }
        });
        selPane.add(make, 0, 1);
        GridPane.setColumnSpan(make, 2);
        selPane.add(l, 0, 0); selPane.add(sidesArea, 1, 0);
        stage.setScene(new Scene(selPane, 300, 200));
    }
    
    private static void addObj(GridPane p, Die d) {
        Label num = new Label("D"+d.getSides());
        Label ans = new Label("0   ");
        TextField mod = new TextField("0");
        TextField times = new TextField("1");
        mod.setMaxWidth(60);
        times.setMaxWidth(60);
        ans.setFont(new Font(FONT, 14));
        num.setFont(new Font(FONT, 14));
        mod.setFont(new Font(FONT, 14));
        times.setFont(new Font(FONT, 14));
        Button roll = new Button("Roll!");
        roll.setFont(new Font(FONT, 14));
        roll.setOnMouseClicked(e -> {
            int add = isNumber(mod.getText()) ? Integer.parseInt(mod.getText()) : 0;
            int rolled = d.roll(isNumber(times.getText()) ? Integer.parseInt(times.getText()) : 0, add);
            ans.setText(rolled+getGap(rolled));
        });
        p.add(num, 0, paneIndex);
        p.add(mod, 1, paneIndex);
        p.add(times, 2, paneIndex);
        p.add(roll, 3, paneIndex);
        p.add(ans, 4, paneIndex++);
    }
    
    private static String getGap(int n) {
        int len = Integer.toString(n).length();
        String ans = "";
        for (int i = len; i<biggest; i++) ans+=" ";
        return ans;
    }
    
    private static boolean isNumber(String s){
        if (s.length()<1) return false;
        if (s.length()==1) return Character.isDigit(s.charAt(0));
        return (Character.isDigit(s.charAt(0)) || s.charAt(0) == '-') && isNumber(s.substring(1));
    }

}
