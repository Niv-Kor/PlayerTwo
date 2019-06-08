package com.hit.players;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.networking.ClientProtocol;
import com.hit.players.Avatar.AvatarType;

import javaNK.util.files.FontHandler;
import javaNK.util.files.FontHandler.FontStyle;
import javaNK.util.math.RNG;
import javaNK.util.math.Range;

public class PlayerStatus
{
	private static final int MAX_DIGITS = 8;
	private static final Font NICKNAME_FONT = FontHandler.load("Itim", FontStyle.PLAIN, 14);
	private static final Range<Integer> NUMBERS = new Range<Integer>(48, 57);
	private static final Range<Integer> UPPER_CASE = new Range<Integer>(65, 90);
	private static final Range<Integer> LOWER_CASE = new Range<Integer>(97, 122);
	private static final Range<Integer> UNDERSCORE = new Range<Integer>(95, 95);
	private static final String[] RANDOM_NAMES = {"Lorene", "Dale", "Noah",
												  "Ruben", "Maria", "Jasper",
												  "Tania", "Lindsey", "Napoleon",
												  "Jacqualine", "Rolland", "Patrica",
												  "Keen", "Grace", "Tommy",
												  "Curtis", "Queency", "Frankie",
												  "Gia", "Ali"};
	
	private String nickname;
	private Avatar avatar;
	private boolean isHuman, generatedName;
	private List<Range<Integer>> legalCharacters;
	private ClientProtocol clientSideProtocol;
	
	public PlayerStatus(boolean human) throws IOException {
		this.isHuman = human;
		this.clientSideProtocol = new ClientProtocol();
		
		if (!human) {
			this.avatar = new Avatar(AvatarType.COMPUTER);
			this.nickname = (String) RNG.select(RANDOM_NAMES);
		}
		else {
			this.avatar = new Avatar(AvatarType.PLAYER);
			generateName();
		}
	}
	
	/**
	 * Set a nickname for the player.
	 * The new name goes through a series of verifications,
	 * allowing only the pre-defined legal characters to be a part of it.
	 * @param n - The new name
	 */
	public void setNickname(String n) {
		String newName = "";
		
		//accept only the legal characters of the argument
		for (int i = 0; i < n.length(); i++) {
			Character ch = n.charAt(i);
			
			for (int j = 0; j < legalCharacters.size(); j++) {
				if (legalCharacters.get(j).intersects((int) ch)) {
					newName = newName.concat(ch.toString());
					break;
				}
			}
		}
		
		nickname = new String(newName);
		generatedName = false;
	}
	
	/**
	 * @param a - The new avatar
	 */
	public void setAvatar(Avatar a) { avatar = a; }
	
	/**
	 * Generate a name that goes by the template of: "Guest_XXXXXXXX".
	 */
	public void generateName() {
		//init list of lagal characters
		if (legalCharacters == null) {
			legalCharacters = new ArrayList<Range<Integer>>();
			legalCharacters.add(NUMBERS);
			legalCharacters.add(UPPER_CASE);
			legalCharacters.add(LOWER_CASE);
			legalCharacters.add(UNDERSCORE);
		}
		
		nickname = new String("Guest_" + buildRandomStream(MAX_DIGITS, false));
		generatedName = true;
	}
	
	private String buildRandomStream(int length, boolean containLetters) {
		String stream = "";
		
		for (int i = 0; i < length; i++) {
			if (containLetters)	stream = stream + (char) RNG.select(legalCharacters).generate();
			else stream = stream + (char) legalCharacters.get(0).generate();
		}
		
		return stream;
	}
	
	/**
	 * Get a small panel that contains the player's avatar and nickname 
	 * @param dim - The dimension of the panel
	 * @return a small panel with the player's avatar and nickname.
	 */
	public JPanel getPlayerGauge(Dimension dim) {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel gauge = new JPanel(new GridBagLayout());
		gauge.setPreferredSize(dim);
		gauge.setOpaque(false);
		
		//name
		JLabel nameLab = new JLabel(nickname);
		nameLab.setForeground(Color.WHITE);
		nameLab.setFont(NICKNAME_FONT);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.bottom = 5;
		gauge.add(nameLab, constraints);
		
		//avatar
		Avatar avatarCopy = new Avatar(avatar);
		avatarCopy.setSelectColor(new Color(255, 204, 0, 200)); //golden
		avatarCopy.setUnselectColor(new Color(0, 0, 0, 0)); //transparent;
		avatarCopy.select(false);
		
		constraints.gridy = 1;
		constraints.insets.top = 0;
		constraints.insets.bottom = 0;
		gauge.add(avatarCopy, constraints);
		
		return gauge;
	}
	
	/**
	 * @return the nickname of the player.
	 */
	public String getNickname() { return nickname; }
	
	/**
	 * @return true if the player is human.
	 */
	public boolean isHuman() { return isHuman; }
	
	/**
	 * @return true if the player has not chosen a nickname for himself
			   and is using the "Guest_XXXXXXXX" template nickname.
	 */
	public boolean isGuest() { return generatedName; }
	
	/**
	 * @return the player's avatar.
	 */
	public Avatar getAvatar() { return avatar; }
	
	/**
	 * @return the player's protocol with the server.
	 */
	public ClientProtocol getProtocol() { return clientSideProtocol; }
}