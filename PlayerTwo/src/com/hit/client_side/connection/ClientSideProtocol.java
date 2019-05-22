package com.hit.client_side.connection;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hit.client_side.UI.launcher.ClientSideGame;

public class ClientSideProtocol
{
	/**
	 * This class creates a mini-protocol that's seemlessly sending acks to the main protocol
	 * over and over again, in order to prevent its starvation.
	 * It assures that the main protocol will never wait an infinite amount of time for a single message,
	 * by regularly sending it meaningless acks that will be handled and thrown away. 
	 * 
	 * @author Niv Kor
	 */
	private class Waker extends ClientSideProtocol
	{
		private static final double ACK_REPEAT = 0.2;
		
		private Timer timer;
		private TimerTask task;
		private boolean started;
		
		/**
		 * @see constructor ServerProtocol(boolean initWaker)
		 * @param tiredProt - The main protocol to regularly wake
		 */
		public Waker(ClientSideProtocol tiredProt) throws IOException {
			super(false);
			
			//retrieve the tired protocol's port as the target port
			this.target = tiredProt.getPort();			
			
			this.timer = new Timer();
			this.task = new TimerTask() {
				@Override
				public void run() {
					try { send("wakeup:");	}
					catch(IOException e) { e.printStackTrace(); }
				}
			};
		}
		
		/**
		 * Start sending signals to the main protocol.
		 */
		public void start() {
			if (started) return;
			
			timer.schedule(task, 0, (int) (ACK_REPEAT * 1000));
			started = true;
		}
	}
	
	protected static List<HashMap<String, String[]>> buffers = new ArrayList<HashMap<String, String[]>>();
	protected InetAddress serverAddress;
	protected DatagramSocket socket;
	protected Integer port, target;
	protected Waker waker;
	
	/**
	 * @param initWaker - True to initialize a wake protocol that will prevent this protocol's starvation.
	 * 					  For a safe functioning of the protocol, 'true' is highly recommended.
	 * 
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientSideProtocol(boolean initWaker) throws IOException {
		this.serverAddress = InetAddress.getLocalHost();
		this.port = PortGenerator.nextPort();
		connect();
		
		if (initWaker) {
			Waker waker = new Waker(this);
			waker.start();
		}
	}
	
	/**
	 * @param port - The port this protocol will use
	 * @param targetPort - The port this protocol will communicate with
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientSideProtocol(Integer port, Integer targetPort) throws IOException {
		this(true);
		this.target = targetPort;
		disconnect();
		this.port = port;
		connect();
	}
	
	/**
	 * Create a socket and connect to the host.
	 * 
	 * @throws SocketException when the socket is already binded.
	 */
	private void connect() throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	/**
	 * Close the socket.
	 */
	private void disconnect() {
		socket.close();
	}
	
	/**
	 * Send a String message to the target port.
	 * 
	 * @param msg - The message to send
	 * @throws IOException when the target port is unavailable for sending messages to.
	 */
	public void send(String msg) throws IOException {
		byte[] data = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, target);
		socket.send(packet);
	}
	
	/**
	 * Receive a message from the target port.
	 * This is a dangerous method and can cause starvation if not handled carefully.
	 * 
	 * @return an array of the message parts, where the first part is the request string.
	 * @throws IOException when the target port is unavailable for receiving messages from.
	 */
	private String[] receive() throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		String message = new String(packet.getData(), 0, packet.getLength());
		return message.split(":");
	}
	
	/**
	 * Wait for a specific packet from the target port.
	 * In this method, whenever receiving a message that's incompatible with the request,
	 * it's not thrown away, but rather waiting in a buffer where it can be found later.
	 * If any of the requests get a compatible answer, that's enough to exit the method. 
	 * 
	 * @param prefix - Array of all the requests from the target port
	 * @return any of the requests sent (whichever message is recieved first).
	 * @throws IOException when the target port is unavailable for receiving messages from.
	 */
	public String[] waitFor(String[] prefix) throws IOException {
		//open local buffer
		HashMap<String, String[]> localBuffer = new HashMap<String, String[]>();
		buffers.add(localBuffer);
		return waitFor(prefix, localBuffer);
	}
	
	/**
	 * @see waitFor(String[] prefix)
	 * @param localBuffer - Sent to this private overloading method by the public version
	 */
	private String[] waitFor(String[] prefix, HashMap<String, String[]> localBuffer) throws IOException {
		String[] answer = receive();
		
		//received an answer - check if it's compatible with one of the requests
		for (String pre : prefix) {
			//the answer is compatible - return it
			if (answer[0].equals(pre.split(":")[0])) {
				
				//spread the local buffer's content to all other buffers and delete it
				for (HashMap<String, String[]> otherBuffer : buffers)
					if (otherBuffer != localBuffer) otherBuffer.putAll(localBuffer);
				
				buffers.remove(localBuffer);
				return answer;
			}
			
			//the answer is useless - spread it to all other buffers
			for (HashMap<String, String[]> otherBuffer : buffers)
				if (otherBuffer != localBuffer && !otherBuffer.containsKey(answer[0]))
					otherBuffer.put(answer[0], answer);
		}
		
		//check if locsl buffer has the compatible answer
		for (String pre : prefix)
			if (localBuffer.containsKey(pre))
				return localBuffer.get(pre);
		
		return waitFor(prefix, localBuffer);
	}
	
	/**
	 * Send a message to the target port and wait until a compatible answer is received.
	 * As long as the answer is not received, nag the target port and send it the request over and over again.
	 *  
	 * @param msg - The request from the target port
	 * @return an answer for the request.
	 * @throws IOException when the target port is unavailable.
	 */
	public String[] request(String msg) throws IOException {
		//open local buffer
		HashMap<String, String[]> localBuffer = new HashMap<String, String[]>();
		buffers.add(localBuffer);
		
		return stubbornRequest(msg, localBuffer);
	}
	
	/**
	 * @see request(String msg)
	 * @param localBuffer - Sent to this private overloading method by the public version
	 */
	private String[] stubbornRequest(String responseTo, HashMap<String, String[]> localBuffer) throws IOException {
		String expectedKey = responseTo.split(":")[0];
		
		send(responseTo);
		String[] answer = receive();
		
		//check that the answer is compatible with the request
		if (answer[0].equals(expectedKey)) {
			//spread the local buffer's content to all other buffers and delete it
			for (HashMap<String, String[]> otherBuffer : buffers)
				if (otherBuffer != localBuffer) otherBuffer.putAll(localBuffer);
			
			buffers.remove(localBuffer);
			return answer;
		}
		else {
			//the answer is useless - spread it to all other buffers
			for (HashMap<String, String[]> otherBuffer : buffers)
				if (otherBuffer != localBuffer && !otherBuffer.containsKey(answer[0]))
					otherBuffer.put(answer[0], answer);
			
			//check if local buffer has the compatible answer
			if (localBuffer.containsKey(expectedKey))
				return localBuffer.get(expectedKey);
			
			//try again
			return stubbornRequest(responseTo, localBuffer);
		}
	}
	
	/**
	 * Connect to the server that's responsible for the game.
	 * @param game - The current game the client wants to play
	 * @throws IOException when the server port is unavailable.
	 */
	public void connectServer(ClientSideGame game) throws IOException {
		target = PortGenerator.CLIENT_FINDER;
		target = Integer.parseInt(request("hello:" + game.name() + ":" + port + ":")[1]); //assign with new target
	}
	
	/**
	 * Disconnect from the server  that's responsible for the game.
	 * @param game - The current game the client wants to leave
	 * @throws IOException when the server port is unavailable.
	 */
	public void disconnectServer(ClientSideGame game) throws IOException {
		target = PortGenerator.CLIENT_FINDER;
		send("bye:" + game.name() + ":" + port + ":");
		disconnect();
	}
	
	/**
	 * @return the port this protocol uses.
	 */
	public int getPort() { return port; }
	
	/**
	 * @return the target port this protocol communicates with.
	 */
	public int getTargetPort() { return target; }
}