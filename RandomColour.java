
import java.awt.Color;
import java.util.*;
public class RandomColour{
	private int length;
	private Color [] myColors;
	private int num = -1;
	public RandomColour(int length) {
		//System.out.println(length);
		this.length = length;
		this.myColors= new Color[length];
		populateColors();
	}
	public Color getNext() {
		if(num+1 == length) num = -1;
		return myColors[++num];
	}
	private int Range = 120;
	private void populateColors(){
		Random r = new Random();
		///0 -> Red, 1->Green, 2->Blue
		///Choosing 135-255 for dominant, 55-175 for secondary, 0-110 for third.
		///I really need a better way of ordering 3 elements. 
		int dc = r.nextInt(6);
		Color root = new Color(1, 2, 3);
        switch (dc) {
			//GBR
			case 0: root = new Color(r.nextInt(Range)+0, r.nextInt(Range)+135, r.nextInt(Range)+70);
					break;
			//RGB 
            case 1: root = new Color(r.nextInt(Range)+135, r.nextInt(Range)+70, r.nextInt(Range)+0);
					break;
			//RBG
            case 2: root = new Color(r.nextInt(Range)+135, r.nextInt(Range)+0, r.nextInt(Range)+70);
					break;
			//BRG
            case 3: root = new Color(r.nextInt(Range)+70, r.nextInt(Range)+0, r.nextInt(Range)+135);
					break; 
			//BGR
			case 4: root = new Color(r.nextInt(Range)+0, r.nextInt(Range)+70, r.nextInt(Range)+135);
					break;
			//GRB
			case 5: root = new Color(r.nextInt(Range)+70, r.nextInt(Range)+135,r.nextInt(Range)+0);
					break;
		}
		for(int i = 0; i<length;i++) {
			myColors[i] = root;
		}

	}
}