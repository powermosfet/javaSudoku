/*BlaBla*/

import java.io.*;
import java.util.*;

class AreaMap{
	private int[] area;
	int size;

	public AreaMap(String mapFile){
		File f = new File(mapFile);
		Scanner fScanner = new Scanner(f);
		try{
			if(fScanner.hasNextInt()){
				this.size = fScanner.nextInt();
			} else {
				System.err.println("Error parsing map file \'" + mapFile + "\'");
				System.exit(-1);
			}
		} catch(IOException e){
			System.err.println(e);
			System.exit(-1);
		}
	}
	public Integer[] exceptRow(int field){
		int currentArea = this.area[field];
		int currentRow = (int)(field / this.size);
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		for(int i = 0; i < this.area.length; i++){
			if(this.area[i] == currentArea && (int)(i / this.size) != currentRow){
				returnList.add(i);
			}
		}
		Integer[] r = new Integer[returnList.size()];
		return returnList.toArray(r);
	}
	public Integer[] exceptCol(int field){
		int currentArea = this.area[field];
		int currentCol = (int)(field % this.size);
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		for(int i = 0; i < this.area.length; i++){
			if(this.area[i] == currentArea && (int)(i % this.size) != currentCol){
				returnList.add(i);
			}
		}
		Integer[] r = new Integer[returnList.size()];
		return returnList.toArray(r);
	}
}
