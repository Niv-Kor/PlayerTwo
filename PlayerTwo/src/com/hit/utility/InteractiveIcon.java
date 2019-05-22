package com.hit.utility;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.hit.utility.files.ImageHandler;

public class InteractiveIcon extends JLabel implements MouseListener
{
	private static final long serialVersionUID = -5905973344666262437L;
	
	private ImageIcon icon, hoverIcon;
	private Callable<?> func;
	private boolean enableFunction, enableHover;
	
	public InteractiveIcon(String iconPath) {
		this(ImageHandler.loadIcon(iconPath));
	}
	
	public InteractiveIcon(ImageIcon icon) {
		addMouseListener(this);
		this.enableFunction = true;
		this.enableHover = true;
		setIcon(this.icon = icon);
	}
	
	public void mouseEntered(MouseEvent arg0) {
		if (hoverIcon != null && enableHover) super.setIcon(hoverIcon);
		revalidate();
		repaint();
	}

	public void mouseExited(MouseEvent arg0) {
		if (getIcon() != icon) super.setIcon(icon);
		revalidate();
		repaint();
	}

	public void mousePressed(MouseEvent arg0) {
		if (func != null && enableFunction) {
			try { func.call(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public void setIcon(String iconPath) {
		setIcon(ImageHandler.loadIcon(iconPath));
	}
	
	public void enableHover(boolean flag) {
		enableHover = flag;
	}
	
	public void setHoverIcon(String iconPath) {
		setHoverIcon(ImageHandler.loadIcon(iconPath));
	}
	
	public void setHoverIcon(ImageIcon icon) {
		hoverIcon = icon;
	}
	
	public void enableFunction(boolean flag) {
		enableFunction = flag;
	}

	public void setFunction(Callable<?> f) { func = f; }
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}