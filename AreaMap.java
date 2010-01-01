/*BlaBla*/

import java.io.*;
import java.util.*;

class AreaMap{
	private Integer[] area;
	private int size;

	public AreaMap(String mapFileName, int size){
		ArrayList<Integer> areaList = new ArrayList<Integer>();
		File mapFile = new File(mapFileName);
		try{
			Scanner s = new Scanner(mapFile);
			while(s.hasNextInt()){
				areaList.add(s.nextInt());
			}
		} catch(FileNotFoundException e){
			System.err.println(e);
			System.exit(-1);
		}
		this.size = (int)Math.sqrt(area.length);
		if(this.size != size){
			System.err.println("Size of map file does not match size of board");
			System.exit(-1);
		}
		Integer[] i = new Integer[areaList.size()];
		area = areaList.toArray(i);
	}
	public Integer[] areas(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int f : area){
			if(!(list.contains(f))){
				list.add(f);
			}
		}
		Integer[] i = new Integer[list.size()];
		return list.toArray(i);
	}
	public Integer[] outsidersRow(int row, int area){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){
			if(this.area[size*row+i] != area){
				list.add(size*row+i);
			}
		}
		Integer[] i = new Integer[list.size()];
		return list.toArray(i);
	}
	public Integer[] outsidersCol(int col, int area){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){
			if(this.area[size*col+i] != area){
				list.add(size*col+i);
			}
		}
		Integer[] i = new Integer[list.size()];
		return list.toArray(i);
	}
	public Integer[] getAll(int number){
		int currentArea = area[number];
		int currentRow = (int)(number / size);
		int currentCol = (int)(number % size);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size*size; i++){
			if(area[i] == currentArea){
				list.add(i);
			}
		}
		Integer[] i = new Integer[list.size()];
		return list.toArray(i);
	}
	public Integer[] get(int number){
		int currentArea = area[number];
		int currentRow = (int)(number / size);
		int currentCol = (int)(number % size);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < size*size; i++){
			if(area[i] == currentArea && (int)(i / size) != currentRow
					&& (int)(i % size) != currentCol){
				list.add(i);
			}
		}
		Integer[] i = new Integer[list.size()];
		return list.toArray(i);
	}
}
