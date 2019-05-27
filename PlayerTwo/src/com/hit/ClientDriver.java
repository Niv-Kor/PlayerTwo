package com.hit;
import java.io.IOException;

import com.hit.UI.windows.ConfigWindow;
import com.hit.game_launch.Launcher;
import com.hit.game_launch.Launcher.Substate;

import javaNK.util.networking.PortGenerator;

public class ClientDriver
{
	public static void main(String[] args) {
		PortGenerator.allocate("server_port", 5080);
		
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