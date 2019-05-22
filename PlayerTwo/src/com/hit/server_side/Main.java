package com.hit.server_side;
import java.io.IOException;
import com.hit.server_side.connection.GeneralService;
import com.hit.server_side.connection.ServerLogger;
import com.hit.server_side.game_controlling.ServerSideController;

public class Main
{
	public static boolean debug = false;

	public static void main(String[] args) {
		try {
			ServerLogger.print("Server: started...");
			GeneralService.init();
			ServerSideController.init();
			ServerSideController.find();
		}
		catch(IOException e) {
			ServerLogger.print("Cannot set up the server.");
			e.printStackTrace();
		}
	}
}