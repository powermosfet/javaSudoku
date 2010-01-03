/* AreaMap class
 * 
 * An AreaMap object holds information about
 * the areas on a sudoku board. It loads the area
 * information from a file, allowing the user to 
 * specify the shape of the areas.
 * */

import java.io.*;
import java.util.*;

class AreaMap{
	private Integer[] area;
	private int size;

	public AreaMap(AreaMap original){
		/* Copy constructor */
		size = original.getSize();
		area = new Integer[size*size];
		for(int i = 0; i < size*size; i++){
			area[i] = original.getAreaNumber(i);
		}
	}
	public AreaMap(String mapFileName, int size){
		/* The standard constructor. It loads the map from
		 * the file mapFileName, checking that it is of size "size"
		 * */
		ArrayList<Integer> areaList = new ArrayList<Integer>();
		File mapFile = new File(mapFileName);
		try{
			Scanner s = new Scanner(mapFile);				//Add all Integers in file to areaList
			while(s.hasNextInt()){
				areaList.add(s.nextInt());
			}
		} catch(FileNotFoundException e){
			System.err.println("Could not find map file \'" + mapFileName + "\'");
			System.exit(-1);
		}
		area = new Integer[areaList.size()];				//Convert areaList to array
		area = areaList.toArray(area);
		this.size = (int)Math.sqrt(area.length);			//Check if the size of the map is correct
		if(this.size != size){
			System.err.println("Size of map file does not match size of board");
			System.exit(-1);
		}
	}
	public int getAreaNumber(int i){
		/* Return the areanumber of the field
		 * at index i
		 * */
		return area[i];
	}
	public int getSize(){
		/* Return the size of the map */
		return size;
	}
	public Integer[] areas(){
		/* Return a list of all areanumbers */
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int f : area){
			if(!(list.contains(f))){				//Add all areanumbers that not already in list
				list.add(f);
			}
		}
		Integer[] i = new Integer[list.size()];		//Convert the list to array
		return list.toArray(i);						//and return the array
	}
	public Integer[] outsidersRow(int row, int area){
		/* Return a list of all field indexes 
		 * in row "row" that are not in area "area"
		 * */
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){				//Scan through row
			if(this.area[size*row+i] != area){		//if the field is not in area
				list.add(size*row+i);				//add it
			}
		}
		Integer[] i = new Integer[list.size()];		//Convert to array
		return list.toArray(i);						//and return the array
	}
	public Integer[] outsidersCol(int col, int area){
		/* Return a list of all field indexes 
		 * in col "col" that are not in area "area"
		 * */
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){				//Scan through column
			if(this.area[size*i+col] != area){		//If the field is not in area
				list.add(size*i+col);				//add it
			}
		}
		Integer[] i = new Integer[list.size()];		//Convert to array
		return list.toArray(i);						//and return the array
	}
	public int[] getAll(int areaNumber){
		/* Returns the index of all fields in 
		 * area areaNumber
		 * */
		int[] indexes = new int[size];
		int j = 0;
		for(int i = 0; i < size*size; i++){			//Scan through the map
			if(area[i] == areaNumber){				//If the field is in the correct area
				indexes[j++] = i;					//add it
				if(j >= size) break;				//there should never be more fields in an array than "size"
			}
		}
		return indexes;
	}
	public Integer[] get(int field){
		/* Return the index of all fields in 
		 * "field"'s area, except those in the same
		 * row or column as field
		 * */
		int currentArea = area[field];									//Get the area of field
		int currentRow = (int)(field / size);							//Get the row of field
		int currentCol = (int)(field % size);							//Get the column of field
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size*size; i++){								//Scan through the map
			if(area[i] == currentArea && (int)(i / size) != currentRow	//if i is in field's area but not it's
					&& (int)(i % size) != currentCol){					//row or column,
				list.add(i);											//add it
			}
		}
		Integer[] i = new Integer[list.size()];							//Convert the list to array
		return list.toArray(i);											//and return the array
	}
}
