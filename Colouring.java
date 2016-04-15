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
public class Colouring extends ColouringHelper{
	
	int [][] P;
	int [] used; 

	/*Create adjacencyMatrix*/ 
	public int [][] FindEdges(int [][] noLines) {
		System.out.println(numSections);
		int [][] adj = new int[numSections][numSections];
		for(int i =0;i<WIDTH;i++){
			for(int j =0; j<HEIGHT;j++){
				if(i != 0 && noLines[i][j] != noLines[i-1][j]) {
					adj[noLines[i-1][j]][noLines[i][j]] += 1;
					adj[noLines[i][j]][noLines[i-1][j]] += 1; 
				} else if(i < WIDTH-1 && noLines[i][j] != noLines[i+1][j]) {
					adj[noLines[i+1][j]][noLines[i][j]] += 1;
					adj[noLines[i][j]][noLines[i+1][j]] += 1; 
				} else if(j != 0 && noLines[i][j] != noLines[i][j-1]){
					adj[noLines[i][j]][noLines[i][j-1]] +=1;
					adj[noLines[i][j-1]][noLines[i][j]] +=1;
				} else if(j < HEIGHT-1 && noLines[i][j] != noLines[i][j+1]){
					adj[noLines[i][j]][noLines[i][j+1]] += 1;
					adj[noLines[i][j+1]][noLines[i][j]] += 1; 
				}
			}
		}
		return adj;
	}
	/* convert to Binary image, get outlines, get partitions*/
	private void ProcessImage(int[][] intensities){
		PopCanv(intensities);
		convertToBinary();
		fillOutLineArray();
		System.out.println("Width, Height is " + WIDTH +", " +  HEIGHT);
		PartitionImage();
	}
	public Colouring(Color [][] inputPixels, String out) {
		this.WIDTH = inputPixels.length;
		this.HEIGHT = inputPixels[0].length;
		p = new PNGCanvas(WIDTH, HEIGHT);
		int numpix = WIDTH*HEIGHT;
		int[][] intensities = ColoursToIntensities(inputPixels);
		outlineArray = new int[WIDTH][HEIGHT];
		partitions = new int[WIDTH][HEIGHT];
		ProcessImage(intensities); 
		System.out.println("We have " + numSections + " partitions.");
		int [][] nl = removeLines();
		int [][] adjMatrix = FindEdges(nl);
		System.out.println("Attempting to 4-color..");
		goColour(adjMatrix, nl);
		save_image(p.pixels, out);
	}
	/*Create new array, NoLines, in which lines are erroded with nearest sections values */
	private int [][] removeLines(){
		int [][] noLines = new int[WIDTH][HEIGHT];
		for(int i = 0; i<WIDTH;i++){
			for(int j =0;j<HEIGHT;j++){
				noLines[i][j] = partitions[i][j]; 
			}
		}
		for(int i = 0; i<WIDTH;i++){
			for(int j =0;j<HEIGHT;j++){
				if(partitions[i][j] == -1) { 
					noLines[i][j] = findSection(i, j); 
				}
			}
		}
		return noLines;	
	}
	private void goColour(int [][] adjacencyMatrix, int [][] noLines) {
		sectionColors = new int[numSections];
		P = new int[4][numSections];
		used = new int[numSections]; 
		
		LinkedList [] adjList = createAdjList(adjacencyMatrix);
		if(!FourColourImage(adjList, 0)) System.out.println("Four color failed");
		mapColors(noLines);
	}
	private LinkedList [] createAdjList(int [][] adjacencyMatrix) {
		@SuppressWarnings("unchecked") LinkedList<Integer> [] adjList = new LinkedList[adjacencyMatrix.length];
		for(int i = 0; i<adjList.length;i++){
			for(int j = 0; j<adjList.length;j++){
				if(adjacencyMatrix[i][j] >= 1) {
					if(adjList[i] == null) {
						adjList[i] = new LinkedList<Integer>();
						adjList[i].addLast(j);
					} else { 
						adjList[i].addLast(j) ; 
					}
				}
			}
		}
		return adjList;
	} 
	/*map 4colouring onto image*/
	private void mapColors(int [][] noLines){
		Color [] mapping = {WHITE, RED, BLUE, GREEN, PURPLE};
		int helper = 0;
		for(int i =0; i<WIDTH; i++){
			for(int j = 0; j<HEIGHT; j++){
				if(partitions[i][j] == -1)  { p.SetPixel(i, j, BLACK);
				} else { 
					helper = sectionColors[noLines[i][j]];
					p.SetPixel(i, j, mapping[helper]);
				}
			}
		}
	}
	/*fills up sectionColors with the mapping of each pixel to a colour */ 
	protected boolean FourColourImage(LinkedList [] al,  int sect) {
		if (sect == numSections) return true;
		if (sectionColors[sect] != 0) throw new Error();
		
		int [] seen = new int[5]; 
		for(int i =0; i<al[sect].size(); i++) seen[sectionColors[(int)al[sect].get(i)]] = 1;
		
		int near;
		boolean skip = false;
		for(int j = 1; j<5; j++) { 
			if(seen[j]==0) {
				sectionColors[sect] = j;
				for(int i =0; i<al[sect].size(); i++) {
					near = (int)(al[sect].get(i));
					if(P[j-1][near] == 0) {
						P[j-1][near] = 1;
						used[near]++;
						if(used[near]==4) skip = true; 
					} else {
						P[j-1][near]++; 
					}
				}
				
				if(!skip && FourColourImage(al, sect+1)) return true;
				skip = false;
				
				for(int i =0; i<al[sect].size(); i++) {
					near = (int)al[sect].get(i);
					if(P[j-1][near] ==1) {
						P[j-1][near] = 0;
						used[near]--;
					} else {
						P[j-1][near] -= 1; 
					}
				}
			}
		}
		
		sectionColors[sect] = 0;
		return false;
	}
	/* Random Colour Business Below, used for Visualizing Bugs*/ 
	public void ColourRandomly(int [][] nl){
		ColourCanvas(nl);
	}
	private void ColourCanvas(int [][] noLines){
		for(int i=0; i<visited.length;i++){
			for(int j=0;j<visited[0].length;j++){
				visited[i][j] = 0;
				partitions[i][j] = noLines[i][j]; 
			}
		}
		this.numSections = -1;
		for(int i = 0; i<partitions.length;i++)
			for(int j = 0; j<partitions[0].length; j++)
				if(visited[i][j] == 0) colourSection(i, j, new RandomColour(this.maxSectionSize), ++numSections);
			
	}
	private void colourSection(int x, int y, RandomColour r, int numSections){
		Queue<Point> bfsQue = new LinkedList<Point>();
		int target = partitions[x][y];
		Point temp;
		bfsQue.add(new Point(x,y));
		while(bfsQue.peek() != null){
			temp = bfsQue.poll();
			x = temp.x;
			y = temp.y;
			setVis(x, y);
			p.SetPixel(x, y, r.getNext());
			if(x+1 < p.width&& target == partitions[x+1][y] && !Vis(x+1, y)) {
				setVis(x+1, y);
				bfsQue.add(new Point(x+1, y));
			}
			if(x != 0 && target == partitions[x-1][y]&& !Vis(x-1, y)) {
				setVis(x-1, y);
				bfsQue.add(new Point(x-1, y));
			}
			if((y!=0 && target == partitions[x][y-1] && !Vis(x, y-1))){
				setVis(x, y-1);
				bfsQue.add(new Point(x, y-1));
			}
			if((y+1<p.height-1 && target == partitions[x][y+1] && !Vis(x, y+1))) {
				setVis(x, y+1);
				bfsQue.add(new Point(x, y+1));
			}
		}
	}
	/* main */ 
	public static void main(String[] args){
		if (args.length < 1){
			System.out.printf("Usage: Colouring <input image>.png <output image>.png\n");
			return;
		}
		String input_filename = args[0];
		if (!input_filename.toLowerCase().endsWith(".png"))
			ErrorExit("Input file must be a PNG image.\n");
			
		String output_filename = null;
		if (args.length > 1)
			output_filename = args[1];
		else
			output_filename = input_filename.substring(0,input_filename.length()-4)+"_output.png";
		Color[][] inputPixels = load_image(input_filename);
		Colouring myArt = new Colouring(inputPixels, output_filename); 
	}
}

