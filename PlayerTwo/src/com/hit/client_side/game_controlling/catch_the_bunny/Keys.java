package com.hit.client_side.game_controlling.catch_the_bunny;
import java.awt.event.KeyEvent;

public enum Keys {
	UP(KeyEvent.VK_W),
	DOWN(KeyEvent.VK_S),
	LEFT(KeyEvent.VK_A),
	RIGHT(KeyEvent.VK_D);
	
	private int keyCode;
	
	private Keys(int key) {
		this.keyCode = key;
	}
	
	/**
	 * Get key using the key code it uses.
	 * @param code - KeyEvent constant
	 * @return the corrent key, or null if not found.
	 */
	public static Keys getKey(int code) {
		for (Keys k : values())
			if (k.keyCode == code) return k;
		
		return null;
	}
}