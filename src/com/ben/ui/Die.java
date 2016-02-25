package com.ben.ui;

import java.util.Random;

public class Die {
	
	public static String name;
	
	static {
		name = "";
	}
    
    private Random random;
    
    private int sides;
    
    public Die(int sides) {
        this.sides = sides;
        this.random = new Random();
    }
    public Die() {
        this(6);
    }
    
    public int roll() {
    	int roll = random.nextInt(sides)+1;
    	if (name.equals("")) {
    		appendToLog("A d"+sides+" is rolled for a "+roll+".");
    	} else {
    		appendToLog(name+" rolls a d"+sides+" for a "+roll+".");
    	}
    	return roll;
    }
    
    public int roll(int mod) {
        int roll = random.nextInt(sides)+1;
        String plus = (mod!=0) ? roll+(mod>0 ? " + " : " - ")+(mod>0 ? mod : Math.abs(mod))+" = "+(roll+mod) : ""+roll;
        if (name.equals("")) {
            appendToLog("A d"+sides+" is rolled for a "+plus+".");
        } else {
            appendToLog(name+" rolls a d"+sides+" for a "+plus+".");
        }
        return roll+mod;
    }
    public int roll(int times, int mod) {
    	boolean neg = false;
    	if (times==1) return roll(mod);
    	if (times==0) return 0;
    	if (times<0) { neg = true; times*=-1; }
    	
    	if (name.equals("")) appendToLog("\n"+times+" d"+sides+"s are rolled:");
    	else appendToLog("\n"+name+" rolls "+times+" d"+sides+"s:");
    	
    	int total = 0;
    	int[] rolls = new int[times];
    	for (int i = 0; i<times; i++) {
    		
	    	int roll = random.nextInt(sides)+1;
	    	String plus = (mod!=0) ? roll+(mod>0 ? " + " : " - ")+(mod>0 ? mod : Math.abs(mod))+" = "+(roll+mod) : ""+roll;
	    	if (name.equals("")) {
	    	    appendToLog("A d"+sides+" is rolled for a "+plus+".");
	    	} else {
	    	    appendToLog(name+" rolls a d"+sides+" for a "+plus+".");
	    	}
	    	total+=roll+mod;
	    	rolls[i] = roll+mod;
    	}
    	String exp = "";
    	for (int i = 0; i<rolls.length; i++) exp+=((i==0 ? "" : " + ")+rolls[i]);
    	appendToLog(exp+" = "+total);
    	return neg ? -total : total;
    }
    
    public int getSides() {
        return sides;
    }
    
    private void appendToLog(String s) {
    	DiceUI.log.add(s);
    }
    
}
