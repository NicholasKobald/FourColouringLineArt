/*
4-Colouring Program

Usage: java Colouring input.png outputname.png

Should 4-colour reasonably high resolution line art such that no adjacent sections of the image have the same colour.
Input must be a PNG.

Nicholas Kobald 16/04/15
*/

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Point;
import java.util.*;

public abstract class ColouringBase {

	Color BLACK = new Color(0, 0, 0);
	Color WHITE = new Color(255, 255, 255);
	Color RED = new Color(255, 33, 33);
	Color BLUE = new Color(33, 255, 33);
	Color GREEN = new Color(33, 33, 255);
	Color PURPLE = new Color(160, 32, 240);

	protected int WIDTH;
	protected int HEIGHT;

	abstract boolean FourColourImage(LinkedList[] al, int sect);
	abstract void ColourRandomly(int [][] nl);
	abstract int [][] FindEdges(int [][] noLines);
}
