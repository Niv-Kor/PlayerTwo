package com.hit.UI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hit.UI.fixed_panels.GuidePanel.Flow;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Launcher.Substate;
import com.hit.players.Avatar;
import com.hit.players.Participant;
import com.hit.players.PlayerStatus;
import com.hit.players.Avatar.AvatarType;

import javaNK.util.math.Percentage;

public class IdentificationState extends ConfigState
{
	/**
	 * A listener for Avatar objects, that supports selecting an avatar
	 * with the user's mouse, while all the others get automatically deselected.
	 * @author Niv Kor
	 */
	private static class AvatarListener implements MouseListener
	{
		private Avatar avatar;
		private Avatar[] allAvatars;
		
		public AvatarListener(Avatar[] all, int index) {
			this.allAvatars = all;
			this.avatar = all[index];
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			Participant.PLAYER_1.getStatus().setAvatar(avatar);
			
			for (Avatar a : allAvatars) a.select(false);
			avatar.select(true);
		}
		
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	private static Avatar[] icons = Avatar.get(AvatarType.PLAYER);
	
	public IdentificationState(Window window) {
		super(window, 1);
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		createPanel(new BorderLayout(), window.getDimension(), Color.YELLOW);
		
		//headline modification
		header.addTitleLine("Let us know");
		header.addTitleLine("who you are!");
		
		//nickname
		JPanel namePane = new JPanel(new GridBagLayout());
		namePane.setPreferredSize(Percentage.createDimension(panes[0].getPreferredSize(), 100, 25));
		namePane.setOpaque(false);
		
		JLabel nicnameLab = new JLabel("Nickname");
		nicnameLab.setForeground(Color.WHITE);
		nicnameLab.setFont(LABEL_FONT);
		constraints.insets = new Insets(0, 5, 10, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		namePane.add(nicnameLab, constraints);
		
		JTextField nameField = new JTextField();
		//set default text as player's name
		String defText = !Participant.PLAYER_1.getStatus().isGuest() ? Participant.PLAYER_1.getStatus().getNickname() : "";
		nameField.setText(defText);
		nameField.setPreferredSize(new Dimension(panes[0].getPreferredSize().width / 2, 25));
		nameField.setHorizontalAlignment(JTextField.LEFT);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			private PlayerStatus status = Participant.PLAYER_1.getStatus();
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if (nameField.getText().equals(""))	status.generateName();
				else status.setNickname(nameField.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				status.setNickname(nameField.getText());
			}
			
			public void changedUpdate(DocumentEvent arg0) {}
		});
		
		constraints.gridy = 1;
		namePane.add(nameField, constraints);
		
		JLabel avatarLab = new JLabel("My avatar");
		avatarLab.setForeground(Color.WHITE);
		avatarLab.setFont(LABEL_FONT);
		constraints.gridy = 2;
		constraints.insets = new Insets(50, 5, -40, 5);
		namePane.add(avatarLab, constraints);
		
		panel.add(namePane, BorderLayout.NORTH);
		
		//avatars
		JPanel avatarPane = new JPanel(new GridBagLayout());
		avatarPane.setPreferredSize(Percentage.createDimension(panes[0].getPreferredSize(), 100, 45));
		avatarPane.setOpaque(false);
		
		//set default selection if player hasn't already chosen an avatar before
		if (icons.length > 0 && Participant.PLAYER_1.getStatus().getAvatar() == null) {
			icons[0].select(true);
			Participant.PLAYER_1.getStatus().setAvatar(icons[0]);
		}
		
		//select the avatar the player has already choesn
		else Participant.PLAYER_1.getStatus().getAvatar().select(true);
		
		int columns = 3;
		constraints.insets = new Insets(5, 5, 5, 5);
		
		outerLoop:
		for (int y = 0, i = 0; y < icons.length / columns; y++) {
			constraints.gridy = y;
			
			for (int x = 0; x < columns; x++) {
				constraints.gridx = x;
				icons[i].addMouseListener(new AvatarListener(icons, i));
				avatarPane.add(icons[i], constraints);
				
				//increment i if possible
				if (icons.length > i + 1) i++;
				else break outerLoop;
			}
		}
		
		panel.add(avatarPane, BorderLayout.CENTER);
		
		//buttons
		guide.setTarget(Flow.BACK, window, Substate.GAME_PICKER);
		guide.setTarget(Flow.NEXT, window, Substate.GAME_MODE);
	}
}