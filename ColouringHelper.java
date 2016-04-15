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

public abstract class ColouringHelper extends ColouringBase{
	
	protected int numSections; //number of sections
	protected int [] sectionColors;	//map section colours
	public PNGCanvas p; //canvas we're doing most of our work on
	int [][] visited; 
	int [][] partitions; 
	protected int maxSectionSize; 
	protected int [][] outlineArray; 

	
	protected boolean Vis(int x, int y){
		if((x<0) || x>= WIDTH || y<0 || y >= WIDTH) return true;
		if(visited[x][y] == 1) return true;
		else 				   return false;
	}
	protected void setVis(int x, int y){
		if((x<0) || x>=p.width || y<0 || y>=p.height) return;
		visited[x][y] = 1;
	}
	//populate outlineArray to have -1 at all (x,y) that PNG canvas has a black cell. 
	protected void fillOutLineArray(){
		for(int i = 0; i<WIDTH;i++){
			for(int j = 0; j<HEIGHT;j++){
				if(p.GetPixel(i, j).equals(BLACK)) outlineArray[i][j] = -1;
			}
		}
	}
	
	/* Make all pixels black or white. */
	protected void convertToBinary() {
		for(int i =0;i<p.width;i++){
			for(int j =0;j<p.height;j++){
				if(p.intensity(i, j) > 200) p.SetPixel(i, j, WHITE); //white
				else 						p.SetPixel(i, j, BLACK); 
			}
		}
	}
	/* Move values onto PNG canvas */
	protected void PopCanv(int [][] c){
		for(int i = 0; i<c.length;i++){
			for(int j = 0;j<c[0].length;j++){
				p.SetPixel(i, j, new Color(c[i][j], c[i][j], c[i][j]));
			}
		}
	}
	protected void getLines(){
		for(int i = 0; i<WIDTH; i++){
			for(int j =0; j<HEIGHT; j++){
				if(outlineArray[i][j] == -1) partitions[i][j] = -1;
			}
		}
	}
	protected int findSection(int x, int y){
		for(int i = 0; true; i++){
			if(getPartVal(x+i, y) != -2 &&  getPartVal(x+i, y) != -1 ) return getPartVal(x+i, y);

			if(getPartVal(x-i, y) != -2 &&  getPartVal(x-i, y) != -1 ) return getPartVal(x-i, y);
				
			if(getPartVal(x, y+i) != -2 &&  getPartVal(x, y+i) != -1 ) return getPartVal(x, y+i);
				
			if(getPartVal(x, y-i) != -2 &&  getPartVal(x, y-i) != -1 ) return getPartVal(x, y-i);  
		}
	}
	protected int getPartVal(int x, int y){
		if((x<0) || x>=WIDTH || y<0 || y>=HEIGHT) return -2;
		return partitions[x][y];
	}
	protected void PartitionImage(){
		visited = new int[WIDTH][HEIGHT];
		int sectionId = -1;
		for(int i = 0; i<WIDTH;i++)
			for(int j = 0; j<HEIGHT; j++){
				if(visited[i][j] == 0 && !(p.GetPixel(i,j).equals(BLACK))) {
					getPartition(i, j, ++sectionId, visited);
				}
				visited[i][j] = 1;
			}
		this.numSections = sectionId+1;
		getLines();
	}
	protected void getPartition(int x, int y, int sectionID, int [][] visited) {
		Queue<Point> bfsQue = new LinkedList<Point>();
		Color target = p.GetPixel(x, y);
		Point temp;
		int count = 0;
		bfsQue.add(new Point(x,y));
		while(bfsQue.peek() != null){
			temp = bfsQue.poll();
			count++;
			x = temp.x;
			y = temp.y;
			visited[x][y] = 1;
			//System.out.println("in call number" + sectionID + " currently at " + x + ", " + y);
			partitions[x][y] = sectionID;
			if(x+1 <= p.width && target.equals(p.GetPixel(x+1, y)) && !Vis(x+1, y)) {
				setVis(x+1, y);
				bfsQue.add(new Point(x+1, y));
			}
			if(x != 0 && target.equals(p.GetPixel(x-1, y))&& !Vis(x-1, y)) {
				setVis(x-1, y);
				bfsQue.add(new Point(x-1, y));
			}
			if((y!=0 && target.equals(p.GetPixel(x, y-1)) && !Vis(x, y-1))){
				setVis(x, y-1);
				bfsQue.add(new Point(x, y-1));
			}
			if((y+1 <= HEIGHT && target.equals(p.GetPixel(x, y+1)) && !Vis(x, y+1))) {
				setVis(x, y+1);
				bfsQue.add(new Point(x, y+1));
			}
		}
		if(count>this.maxSectionSize)
		this.maxSectionSize = count;
	} 
	
	
	/* Prints an error message and exits (intended for user errors) */
	protected static void ErrorExit(String errorMessage, Object... formatArgs){
		System.err.printf("ERROR: " + errorMessage + "\n",formatArgs);
		System.exit(0);
	}
	
	
	/* Throws a runtime error (intended for logic errors) */
	protected static void ErrorAbort(String errorMessage, Object... formatArgs){
		throw new Error(String.format(errorMessage,formatArgs));
	}
	
	protected static Color[][] load_image(String image_filename){
		BufferedImage inputImage = null;
		try{
			System.err.printf("Reading image from %s\n",image_filename);
			inputImage = ImageIO.read(new File(image_filename));
		} catch(java.io.IOException e){
			ErrorExit("Unable to open %s: %s\n",image_filename,e.getMessage());
		}
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		Color[][] imagePixels = new Color[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				imagePixels[x][y] = new Color(inputImage.getRGB(x,y));
		return imagePixels;
	}
	protected static int[][] ColoursToIntensities(Color[][] inputPixels){
		int width = inputPixels.length;
		int height = inputPixels[0].length;
		int[][] intensities = new int[width][height];
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				intensities[x][y] = (inputPixels[x][y].getRed()+inputPixels[x][y].getGreen()+inputPixels[x][y].getBlue())/3;
		return intensities;
	}
	protected static void save_image(Color[][] imagePixels, String image_filename){
		int width = imagePixels.length;
		int height = imagePixels[0].length;
		BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				outputImage.setRGB(x,y,imagePixels[x][y].getRGB());
		try{
			ImageIO.write(outputImage, "png", new File(image_filename));
		}catch(java.io.IOException e){
			ErrorExit("Unable to write %s: %s\n",image_filename,e.getMessage());
		}
		System.err.printf("Wrote a %d by %d image\n",width,height);
	}
	
}