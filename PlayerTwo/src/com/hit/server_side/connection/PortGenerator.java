package com.hit.server_side.connection;
import java.net.DatagramSocket;
import java.net.SocketException;
import com.hit.utility.math.Range;

public class PortGenerator
{
	private static final Range<Integer> PORT_RANGE = new Range<Integer>(1, 10_000);
	public static final int GENERAL_SERVICE = 1000;
	public static final int CLIENT_FINDER = 1001;
	public static final int LAUNCHER_APPLICANT = 1002;
	
	/**
	 * Generate an available port number, ready for connection.
	 * @return a free port number.
	 */
	public static int nextPort() {
		DatagramSocket testSocket;
		int port;
		
		//test ports until one manages to connect
		while (true) {
			try {
				do port = (int) PORT_RANGE.generate();
				while (port == GENERAL_SERVICE || port == CLIENT_FINDER || port == LAUNCHER_APPLICANT);
				testSocket = new DatagramSocket(port);
				testSocket.close();
				break;
			}
			catch(SocketException e) {}
		}
		
		return port;
	}
}