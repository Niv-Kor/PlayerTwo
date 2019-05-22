package com.hit.client_side;
import java.io.IOException;

import com.hit.client_side.UI.ConfigWindow;
import com.hit.client_side.UI.launcher.Launcher;
import com.hit.client_side.UI.launcher.Launcher.Substate;

public class Main
{
	public static void main(String[] args) {
		try {
			Launcher.init();
			ConfigWindow configWindow = new ConfigWindow();
			Launcher.setState(configWindow, Substate.GAME_PICKER);
		}
		catch(IOException e) {
			System.err.println("Cannot connect to the server.");
			e.printStackTrace();
		}
	}
}