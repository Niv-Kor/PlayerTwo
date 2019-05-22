package com.hit.utility;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;

import javax.swing.JLabel;

public class OptionLabel extends JLabel implements MouseListener
{
	private static final long serialVersionUID = 3440174835250357589L;
	private final static Color DEF_HOVER_COLOR = Color.ORANGE;
	private final static Color DEF_SELECT_COLOR = new Color(88, 178, 255);
	
	private Color originColor, hoverColor, selectColor;
	private Color backupColor;
	private Callable<?> func;
	private boolean enabled, clicked, enableSelection, enableHover;
	private boolean[] backupColorSettings;
	
	public OptionLabel() {
		super();
		init();
	}
	
	public OptionLabel(String name) {
		super(name);
		init();
	}
	
	public OptionLabel(String name, Callable<Void> func) {
		this(name);
		this.func = func;
	}
	
	private void init() {
		addMouseListener(this);
		this.hoverColor = DEF_HOVER_COLOR;
		this.selectColor = DEF_SELECT_COLOR;
		this.enableSelection = true;
		this.enableHover = true;
		this.backupColorSettings = new boolean[2];
		this.enabled = true;
	}
	
	public void mouseEntered(MouseEvent arg0) {
		if (enableHover) super.setForeground(hoverColor);
	}
	
	public void mouseExited(MouseEvent arg0) {
		if (!clicked || !enableSelection) super.setForeground(originColor);
		else super.setForeground(selectColor);
	}

	public void mousePressed(MouseEvent arg0) {
		clicked = !clicked;
		if (enableSelection) super.setForeground(selectColor);
		
		if (func != null) {
			try { func.call(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public void enableHoverColor(boolean flag) {
		enableHover = flag;
	}
	
	public void enableSelectionColor(boolean flag) {
		enableSelection = flag;
	}

	public void setForeground(Color color) {
		super.setForeground(color);
		originColor = color;
	}
	
	public void release() {
		clicked = false;
		setForeground(originColor);
	}
	
	@Override
	public void enable(boolean flag) {
		if (!enabled && flag) {
			setForeground(backupColor);
			enableHoverColor(backupColorSettings[0]);
			enableSelectionColor(backupColorSettings[1]);
			
		}
		else if (enabled && !flag) {
			backupColor = originColor;
			setForeground(Color.GRAY);
			backupColorSettings[0] = enableHover;
			backupColorSettings[1] = enableSelection;
			enableSelectionColor(false);
			enableHoverColor(false);
		}
		
		enabled = flag;
	}
	
	public Color getHoverColor() { return hoverColor; }
	public Color getSelectColor() { return selectColor; }
	public Callable<?> getFunction() { return func; }
	public void setFunction(Callable<?> f) { func = f; }
	public void setHoverColor(Color c) { hoverColor = c; }
	public void setSelectColor(Color c) { selectColor = c; }
	public boolean isClicked() { return clicked; }
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}