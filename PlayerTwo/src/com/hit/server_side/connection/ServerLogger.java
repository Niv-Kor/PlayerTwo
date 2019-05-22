package com.hit.server_side.connection;
import java.time.LocalDateTime;

public class ServerLogger
{
	private static final String PREFIX = "Server: ";
	
	public static void print(String msg) {
		LocalDateTime now = LocalDateTime.now();
		String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + ":" + now.getNano();
		System.out.println(time + " " + PREFIX + msg);
	}
}