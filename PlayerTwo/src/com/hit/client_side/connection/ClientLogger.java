package com.hit.client_side.connection;
import java.time.LocalDateTime;

public class ClientLogger
{
	private static final String PREFIX = "Client: ";
	
	public static void print(String msg) {
		LocalDateTime now = LocalDateTime.now();
		String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + ":" + now.getNano();
		System.out.println(time + " " + PREFIX + msg);
	}
}