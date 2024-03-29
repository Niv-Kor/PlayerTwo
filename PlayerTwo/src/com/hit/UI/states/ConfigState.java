package com.hit.UI.states;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import com.hit.UI.fixed_panels.GuidePanel;
import com.hit.UI.fixed_panels.HeaderPanel;
import com.hit.UI.windows.Window;
import javaNK.util.math.DimensionalHandler;

public abstract class ConfigState extends State
{
	protected HeaderPanel header;
	protected GuidePanel guide;
	protected JPanel panel;
	
	public ConfigState(Window window, int panesAmount) {
		super(window, 3);
		
		panes[0] = new HeaderPanel(window);
		this.header = (HeaderPanel) panes[0];
		
		panes[1] = new JPanel(new BorderLayout());
		int panelHeight = (int) (100 - HeaderPanel.HEIGHT_PERCENT - GuidePanel.HEIGHT_PERCENT); 
		panes[1].setPreferredSize(DimensionalHandler.adjust(window.getDimension(), 100, panelHeight));
		panes[1].setOpaque(false);
		panel = panes[1];
		
		panes[2] = new GuidePanel(window);
		this.guide = (GuidePanel) panes[2];
	}
	
	@Override
	public void insertPanels() {
		window.insertPanel(header, BorderLayout.NORTH);
		window.insertPanel(panel, BorderLayout.CENTER);
		window.insertPanel(guide, BorderLayout.SOUTH);
	}
}