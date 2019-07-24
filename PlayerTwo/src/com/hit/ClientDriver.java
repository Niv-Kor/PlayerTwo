package com.hit;
import java.io.IOException;
import com.hit.UI.windows.ConfigWindow;
import com.hit.game_launch.Launcher;
import com.hit.game_launch.Launcher.Substate;
import javaNK.util.communication.PortGenerator;
import javaNK.util.communication.Protocol;
import javaNK.util.debugging.Logger;
import server_information.ServerData;

public class ClientDriver
{
	public static void main(String[] args) {
		Logger.configName("Client");
		PortGenerator.allocate("server_port", ServerData.PORT);
		Protocol.debugMode(true);
		
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