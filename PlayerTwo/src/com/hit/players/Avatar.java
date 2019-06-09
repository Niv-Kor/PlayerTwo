package com.hit.players;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javaNK.util.files.ImageHandler;
import javaNK.util.math.RNG;
import javaNK.util.math.Range;

public class Avatar extends JLabel
{
	public static enum AvatarType {
		PLAYER,
		COMPUTER;
	}
	
	private static final long serialVersionUID = 3949656134627694159L;
	private static final String PATH = "avatars/";
	private static final String FILE_TYPE = ".png";
	public static final int BIT_SIZE = 64;
	private static final int COMP_BIT_SIZE = 16;
	private static final Color DEFAULT_SELECTED_COLOR = new Color(0, 228, 255, 200);
	private static final Color DEFAULT_UNSELECTED_COLOR = new Color(110, 201, 235, 50);
	private static final Border SELECTED_BORDER = new MatteBorder(2, 2, 2, 2, DEFAULT_SELECTED_COLOR);
	private static final Border UNSELECTED_BORDER = new MatteBorder(2, 2, 2, 2, DEFAULT_UNSELECTED_COLOR);
	
	private static Avatar[] humanAvatars, compAvatars;
	private Color unselectColor, selectColor;
	private String id;
	
	public Avatar(AvatarType type) {
		super(((Avatar) RNG.select(get(type))).getIcon());
		initVars();
	}
	
	public Avatar(Avatar other) {
		this((ImageIcon) other.getIcon());
	}
	
	public Avatar(String name) {
		this(ImageHandler.loadIcon(PATH + name + FILE_TYPE));
	}
	
	private Avatar(ImageIcon icon) {
		super(icon);
		initVars();
	}
	
	private void initVars() {
		setBorder(UNSELECTED_BORDER);
		this.selectColor = DEFAULT_SELECTED_COLOR;
		this.unselectColor = DEFAULT_UNSELECTED_COLOR;
		this.id = extractID(ImageHandler.getPath(getIcon()));
	}

	/**
	 * Transform the avatar's border to selected or unselected mode
	 * @param flag - true to select or false to deselect
	 */
	public void select(boolean flag) {
		if (flag) {
			//selectColor was modified so a new border needs to be created
			if (!selectColor.equals(DEFAULT_SELECTED_COLOR)) {
				Insets thicknessValues = ((MatteBorder) SELECTED_BORDER).getBorderInsets();
				Border tempBorder = new MatteBorder(thicknessValues.top,
												  	thicknessValues.left,
												  	thicknessValues.bottom,
												  	thicknessValues.right,
												  	selectColor);
				setBorder(tempBorder);
			}
			else setBorder(SELECTED_BORDER);
		}
		else {
			//unselectColor was modified so a new border needs to be created
			if (!unselectColor.equals(DEFAULT_UNSELECTED_COLOR)) {
				Insets thicknessValues = ((MatteBorder) UNSELECTED_BORDER).getBorderInsets();
				Border tempBorder = new MatteBorder(thicknessValues.top,
												  	thicknessValues.left,
												  	thicknessValues.bottom,
												  	thicknessValues.right,
												  	unselectColor);
				setBorder(tempBorder);
			}
			else setBorder(UNSELECTED_BORDER);
		}
	}
	
	/**
	 * Change the color of the selection border
	 * @param color - The new color
	 */
	public void setSelectColor(Color color) {
		selectColor = color;
	}
	
	/**
	 * Change the color of the unselected border
	 * @param color - The new color
	 */
	public void setUnselectColor(Color color) {
		unselectColor = color;
	}
	
	/**
	 * Get all the avatars of the specified type that exist in resource folder.
	 * @param type - AvatarType constant to describe the type of avatars needed 
	 * @return all the avatars available
	 */
	public static Avatar[] get(AvatarType type) {
		if (humanAvatars == null || compAvatars == null) init();
		
		Avatar[] chosenList;
		Avatar[] avatars;
		
		switch(type) {
			case PLAYER: chosenList = humanAvatars; break;
			case COMPUTER: chosenList = compAvatars; break;
			default: return null;
		}
		
		avatars = new Avatar[chosenList.length];
		for (int i = 0; i < avatars.length; i++)
			avatars[i] = new Avatar(chosenList[i]);
		
		return avatars;
	}
	
	/**
	 * Get all the avatars that exist in resource folder.
	 * @return all the avatars available
	 */
	public static Avatar[] getAll() {
		Avatar[] humanAv = get(AvatarType.PLAYER);
		Avatar[] compAv = get(AvatarType.COMPUTER);
		Avatar[] both = Arrays.copyOf(humanAv, humanAv.length + compAv.length);
		
		System.arraycopy(compAv, 0, both, humanAv.length, compAv.length);
		return both;
	}
	
	private static void init() {
		//check existing avatars amount in resource folder
		int amount = 0;
		boolean exists = true;
		
		do {
			exists = ImageHandler.test(PATH + (amount + 1) + FILE_TYPE);
			amount += exists ? 1 : 0;
		}
		while (exists);
		
		//initialize the arrays
		humanAvatars = new Avatar[amount];
		compAvatars = new Avatar[amount];
		
		for (int i = 0; i < amount; i++) {
			//load icon
			humanAvatars[i] = new Avatar("" + (i + 1));
			
			//check if a computer icon exists and create it if it's not
			if (!ImageHandler.test(PATH + (amount + 1) + "C" + FILE_TYPE))
				attachCompSign((ImageIcon) humanAvatars[i].getIcon(), "" + (i + 1));
			
			//than load computer icon
			compAvatars[i] = new Avatar((i + 1) + "C");
		}
	}
	
	private static void attachCompSign(ImageIcon icon, String name) {
		//convert to BufferedImage
		BufferedImage sourceImage = (BufferedImage) icon.getImage();
		
		//load computer sign
		BufferedImage compImage = ImageHandler.loadImage(PATH + "comp_sign" + FILE_TYPE);
		
		//create result image
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		//calculate sign's area inside the new image
		Range<Integer> compArea = new Range<Integer>(BIT_SIZE - COMP_BIT_SIZE, BIT_SIZE);
		
		//build image
		int alpha, red, green, blue, pixel;
		Color tempColor;
		
		for (int y = 0; y < sourceImage.getHeight(); y++) {
			for (int x = 0; x < sourceImage.getWidth(); x++) {
				//(x, y) is inside the area of the computer in the image
				if (compArea.intersects(x) && compArea.intersects(y)) {
					int offset = BIT_SIZE - COMP_BIT_SIZE;
					tempColor = new Color(compImage.getRGB(x - offset, y - offset), true);
					
					//copy RGB/A values from computer image
					alpha = tempColor.getAlpha();
					red = tempColor.getRed();
					green = tempColor.getGreen();
					blue = tempColor.getBlue();
				}
				else { //(x, y) is outside that area
					tempColor = new Color(sourceImage.getRGB(x, y), true);
					
					//copy RGB/A values from source image
					alpha = tempColor.getAlpha();
					red = tempColor.getRed();
					green = tempColor.getGreen();
					blue = tempColor.getBlue();
				}
					
				//create pixel
				pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
				destImage.setRGB(x, y, pixel);
			}
		}
		
		ImageHandler.create(destImage, PATH + name + "C" + FILE_TYPE);
	}
	
	public ImageIcon resizeIcon(Dimension dim) {
		Image bufferedImage = ((ImageIcon) getIcon()).getImage();
		Image clone = bufferedImage.getScaledInstance(dim.width, dim.height, Image.SCALE_DEFAULT);
		return new ImageIcon(clone);
	}
	
	private String extractID(String path) {
		String fileName = path.substring(path.lastIndexOf("/") + 1);
		return fileName.substring(0, fileName.indexOf("."));
	}
	
	public String getID() { return id; }
}