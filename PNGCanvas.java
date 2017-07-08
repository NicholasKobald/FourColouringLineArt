import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Point;
import java.util.*;


public class PNGCanvas {

	public int width, height;
	public Color[][] pixels;

	public PNGCanvas(int width, int height){
		this.width = width;
		this.height = height;
		pixels = new Color[width][height];
	}

	public int intensity(int x, int y) {
		return (int)((GetPixel(x,y).getRed()/3)+(GetPixel(x,y).getBlue()/3)+(GetPixel(x,y).getGreen()/3));
	}

	public Color GetPixel(Point z){
		if((z.x<0) || z.x>=width || z.y<0 || z.y>=height) return null;
		return pixels[z.x][z.y];
	}

	public Color GetPixel(int x, int y){
		if((x<0) || x>=width || y<0 || y>=height) return null;
		return pixels[x][y];
	}

	public void SetPixel(int x, int y, Color colour){
		if((x<0) || x>=width || y<0 || y>=height) return;
		pixels[x][y] = colour;
	}
}
