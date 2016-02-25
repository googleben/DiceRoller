package com.ben.ui;

public class Log {
	
	public Log() {
	}
	
	public void add(String s) {
		DiceUI.logNode.appendText(s+"\n");
		if (DiceUI.networked) DiceUI.messenger.sendMessage(s);
	}
	public void add(String s, boolean send) {
		DiceUI.logNode.appendText(s+"\n");
		if (send && DiceUI.networked) DiceUI.messenger.sendMessage(s);
	}
	
}
