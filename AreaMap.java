/*BlaBla*/

import java.io.*;
import java.util.*;

class AreaMap{
	private Integer[] area;
	private int boardSize;

	public AreaMap(String mapFileName, int size){
		ArrayList<Integer> areaList = new ArrayList<Integer>();
		File mapFile = new File(mapFileName);
		Scanner s = new Scanner(mapFile);
		try{
			while(s.hasNextInt()){
				area.add(s.nextInt());
			}
		} catch(IOException e){
			System.err.println(e);
			System.exit(-1);
		}
		boardSize = (int)sqrt(area.size());
		if(boardSize != size){
			System.err.println("Size of map file does not match size of board");
			System.exit(-1);
		}
		area = areaList.toArray();
	}
	public Integer[] exceptRow(int field){
		int currentArea = area[field];
		int currentRow = (int)(field / size);
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		for(int i = 0; i < area.length; i++){
			if(area[i] == currentArea && (int)(i / size) != currentRow){
				returnList.add(i);
			}
		}
		Integer[] r = new Integer[returnList.size()];
		return returnList.toArray(r);
	}
	public Integer[] exceptCol(int field){
		int currentArea = area[field];
		int currentCol = (int)(field % size);
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		for(int i = 0; i < area.length; i++){
			if(area[i] == currentArea && (int)(i % size) != currentCol){
				returnList.add(i);
			}
		}
		Integer[] r = new Integer[returnList.size()];
		return returnList.toArray(r);
	}
}
