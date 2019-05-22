package com.hit.client_side.game_controlling;
import java.io.IOException;
import com.hit.server_side.game_algo.GameBoard.GameMove;

public class VisibleProcessThread extends ProcessThread
{
	public VisibleProcessThread(ClientSideController controller) {
		super(controller);
	}
	
	public void makeMove(GameMove move) throws IOException {
		protocol.send("move:" + move.getRow() + ":" + move.getColumn() + ":");
		delay(100);
	}
	
	public void makeCompMove() throws IOException {
		protocol.send("compmove:");
		delay(100);
	}
	
	public void placeComp(GameMove move) throws IOException {
		protocol.send("placecomp:" + move.getRow() + ":" + move.getColumn() + ":");
		delay(100);
	}
	
	public char getSign() throws IOException {
		return protocol.request("getsign:")[1].charAt(0);
		
	}
	
	public char getOtherPlayerSign() throws IOException {
		return protocol.request("p2getsign:")[1].charAt(0);
	}
	
	public void tryEndgame() throws IOException {
		protocol.send("tryend:");
		delay(100);
	}
	
	private void delay(long millisec) {
		try { Thread.sleep(millisec); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
}