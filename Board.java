/*Board class
 *
 * contains:
 * - Field objects for all fields in the sudoku board
 * - An AreaMap that contains a map of the different areas
 * - solve method
 * - toString method
 * - And more!
 **/

import java.io.* ;
import java.util.* ;

class Board{
	private final int RECURSION_MAX = 10;
	private final int[][] ROW;
	private final int[][] COL;
	private Field[] field;
	private AreaMap area = null;
	private CArray charSet;
	private int size;

	public Board(String puzzleFileName, String mapFileName){
		/* The default constructor
		 * */
		File puzzleFile = new File(puzzleFileName);
		try{
			Scanner s = new Scanner(puzzleFile);
			String charSetString = "";
			if(s.hasNext()){
				charSetString = s.next();						//Get the character set
			}else{
				System.err.println("Error parsing puzzleFileName file \'" + puzzleFileName + "\'.");
				System.err.println("Could not find character set");
				System.exit(-1);
			}
			charSet = new CArray(charSetString.split(","));		//Convert charset to CArray
			size = charSet.length();
			field = new Field[size*size];						//initialize field array
			for(int i = 0; i < size*size; i++){					//and fill it
				if(s.hasNext()){
					char c = s.next().charAt(0);
					if(charSet.has(c)){
						field[i] = new Field(charSet, c);		//with defined
					}else{
						field[i] = new Field(charSet);			//and undefined characters
					}
				}else{
					System.err.println("Error parsing puzzleFileName file \'" + puzzleFileName + "\'.");
					System.err.println("Not enough fields");
					System.exit(-1);
				}
			}
		} catch(FileNotFoundException e){
			System.err.println("Could not find sudoku file \'" + puzzleFileName + "\'");
			System.exit(-1);
		}
		if(mapFileName.equals("")){								//If no map file is specified, use default
			mapFileName = "map/" + size + ".map";				//map file for this size
			System.out.println("Using default map file \'" + mapFileName + "\'");
		}else{
			System.out.println("Using custom map file \'" + mapFileName + "\'");
		}
		area = new AreaMap(mapFileName, size);					//Initialize the AreaMap	
		/* Generate the ROW and COL arrays */
		ROW = new int[size][size];
		COL = new int[size][size];
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				ROW[i][j] = size*i + j;
				COL[i][j] = size*j + i;
			}
		}
	}
	public Board(Board original){
		/* Copy constructor */
		size = original.getSize();
		ROW = original.getROW();			//These are final
		COL = original.getCOL();			//so no "deep" copying is necessary
		field = new Field[size*size];
		for(int i = 0; i < size*size; i++){
			field[i] = new Field(original.getField(i));
		}
		area = new AreaMap(original.getArea());
		charSet = new CArray(original.getCharSet());
	}
	public int[][] getROW(){
		return ROW;
	}
	public int[][] getCOL(){
		return COL;
	}
	public int getSize(){
		return size;
	}
	public Field getField(int i){
		return field[i];
	}
	public AreaMap getArea(){
		return area;
	}
	public CArray getCharSet(){
		return charSet;
	}
	private boolean scanOne() throws NoLegalCharactersException{
		/* This method searches through each field's
		 * row, column and area for defined numbers.
		 * These numbers are removed from the current field.
		 * This process is repeated for all fields
		 * */
		boolean changed = false;
		CArray defined = new CArray();
		/* for every field in the board */
		for(int i = 0; i < size*size; i++){
			if(field[i].defined() != '\0') continue;
			defined.empty();
			int rowOffset = (int)(i / size);
			int colOffset = (int)(i % size);
			for(int j = 0; j < size; j++){
				/* Collect defined characters in row */
				if((rowOffset*size+j != i) && (field[rowOffset*size+j].defined() != '\0')){
					defined.add(field[rowOffset*size+j].defined());
				}
				/* Collect defined characters in col */
				if((size*j+colOffset != i) && (field[size*j+colOffset].defined() != '\0')){
					defined.add(field[size*j+colOffset].defined());
				}
			}
			/* Collect defined characters in area */
			for(int f:area.get(i)){
				if(field[f].defined() != '\0'){
					defined.add(field[f].defined());
				}
			}
			/*and remove them from this field*/
			if(field[i].canNotBe(defined)) changed = true;
		}
		return changed;
	}
	private boolean scanTwo(){
		/* This method checks if any character is only allowed
		 * in one of the fields in its row, column or area.
		 * If that's the case, that field is set to that character
		 * */
		boolean hasChanged = false;
		/* Check every character */
		for(char c : charSet.getCharArray()){
			/* For each row/column */
			for(int i = 0; i < size; i++){
				int row = -1;
				int col = -1;
				/* Scan through row and check 
				 * if it's legal in more than one field
				 * */
				for(int f : ROW[i]){
					if(field[f].canBe(c)){
						row = (row < 0) ? f : size*size;
					}
				}
				/* if it's legal in only one field, set
				 * that field to be that character
				 * */
				if(row >=0 && row < size*size && !field[row].isDefined()){
					field[row].define(c);
					hasChanged = true;
				}
				/* Scan through column and check 
				 * if it's legal in more than one field
				 * */
				for(int f : COL[i]){
					if(field[f].canBe(c)){
						col = (col < 0) ? f : size*size;
					}
				}
				/* if it's legal in only one field, set
				 * that field to be that character
				 * */
				if(col >=0 && col < size && !field[col].isDefined()){
					field[col].define(c);
					hasChanged = true;
				}
			}
			/* For each area */
			for(int a : this.area.areas()){
				/* Scan through area and check 
				 * if it's legal in more than one field
				 * */
				int area = -1;
				for(int f : this.area.getAll(a)){
					if(field[f].canBe(c)){
						area = (area < 0) ? f : size*size;
					}
				}
				/* if it's legal in only one field, set
				 * that field to be that character
				 * */
				if(area >=0 && area < size*size && !field[area].isDefined()){
					field[area].define(c);
					hasChanged = true;
				}
			}
		}
		return hasChanged;
	}
	private boolean scanThree() throws NoLegalCharactersException{
		/* This method checks if any number is
		 * legal only in one row or column of an
		 * area. It then removes it from every field
		 * that is in the same row or column but not in the same area
		 * */
		boolean hasChanged = false;
		/* Iterate through each area in the board */
		for(int a : area.areas()){
			/* Then, for each possible character */
			for(char c : charSet.getCharArray()){
				boolean charDefined = false;
				int row = -1;
				int col = -1;
				/* check if it's legal in only one row or column */
				for(int f : area.getAll(a)){
					if(field[f].defined() == c){
						charDefined = true;
						break;
					}
					if(field[f].canBe(c)){
						row = (row < 0)? (int)(f / size) : size;
						col = (col < 0)? (f % size) : size;
					}
				} 
				if(charDefined) break;
				/* If that's the case, remove it from 
				 * all the others in the row */ 
				if(row >= 0 && row < size){
					for(int f : area.outsidersRow(row, a)){
						if(!field[f].isDefined() && field[f].canNotBe(c)) hasChanged = true;
					}
				} /* ...or column */
				if(col >= 0 && col < size){
					for(int f : area.outsidersCol(col, a)){
						if(!field[f].isDefined() && field[f].canNotBe(c)) hasChanged = true;
					}
				}
			}
		}
		return hasChanged;
	}
	private boolean scanFour() throws NoLegalCharactersException{
		/* This method scans the board looking
		 * for twins, triplets and so on
		 * */
		boolean hasChanged = false;
		for(int subSetSize = 2; subSetSize < size; subSetSize++){				//generate subsets (of charSet) of increasing size
			int[] subSetIndex = new int[subSetSize];
			for(int i = 0; i < subSetSize; i++)
				subSetIndex[i] = i;
			hasChanged = scanFourSubScan(subSetIndex);
			int iMax = subSetSize - 1;
			int i = iMax;
			while(true){
				for(;(i >= 0)&&((subSetIndex[i] >= size-1)||(i < iMax && subSetIndex[i] >= subSetIndex[i+1]-1)); i--){}
				if(i < 0) break;
				subSetIndex[i] += 1;
				for(; i < iMax; i++){
					subSetIndex[i+1] = subSetIndex[i] + 1;
				}
				if(scanFourSubScan(subSetIndex)) hasChanged = true;
			}
		}
		return hasChanged;
	}
	private boolean scanFourSubScan(int[] subSetIndex) throws NoLegalCharactersException{
		boolean hasChanged = false;
		int subSetSize = subSetIndex.length;
		ArrayList<Integer> regularTwins; 
		CArray thingy; 
		CArray subSet = new CArray();
		for(int i : subSetIndex)
			subSet.add(charSet.get(i));
		/* Scan each row, column and area for regular twins, triplets and so on */
		for(int i = 0; i < size; i++){
			regularTwins = new ArrayList<Integer>();
			thingy = new CArray();
			for(int f : ROW[i]){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					thingy.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				for(int f : ROW[i]){
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
				}
				if(hasChanged) return true;
			}
			regularTwins = new ArrayList<Integer>();
			thingy = new CArray();
			for(int f : COL[i]){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					thingy.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				for(int f : COL[i]){
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
				}
				if(hasChanged) return true;
			}
		}
		for(int a : area.areas()){
			regularTwins = new ArrayList<Integer>();
			thingy = new CArray();
			for(int f : area.getAll(a)){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					thingy.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				for(int f : area.getAll(a)){
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
				}
				if(hasChanged) return true;
			}
		}
		return hasChanged;
	}
	public boolean check(){
		/* Checks if the defined numbers
		 * in the board follow the sudoku
		 * rules
		 * */
		for(int i = 0; i < size; i++){
			CArray row = new CArray();
			CArray col = new CArray();
			for(int f : ROW[i]){
				char r = field[f].defined();
				if(r != '\0'){
					if(row.has(r)) return true;
					row.add(r);
				}
			}
			for(int f : COL[i]){
				char c = field[f].defined();
				if(c != '\0'){
					if(col.has(c)) return true;
					col.add(c);
				}
			}
		}
		for(int a : area.areas()){
			CArray definedChars = new CArray();
			for(int f : area.getAll(a)){
				char c = field[f].defined();
				if(c != '\0'){
					if(definedChars.has(c)) return true;
					definedChars.add(c);
				}
			}
		}
		return false;
	}
	public boolean finished(){
		for(int i = 0; i < size*size; i++){
			if(field[i].isDefined() == false) return false;
		}
		return true;
	}
	public Board solve(){
		boolean hasChanged;
		Board solved, copy;
		/* Loop until there's nothing more to do */
		do{
			do{
				hasChanged = false;
				try{
					while(scanOne()) hasChanged = true;
					while(scanTwo()) hasChanged = true;
					while(scanThree()) hasChanged = true;
				}catch(NoLegalCharactersException e){
					return null;
				}
			}while(hasChanged);
			/* Try scanFour */
			/*
			try{
				hasChanged = scanFour();
			}catch(NoLegalCharactersException e){
				return null;
			}*/
		}while(hasChanged);
		if(check()) return null;
		/* are we done? */
		if(finished()) return this;
		System.err.println("******************\n" + toString());
		for(int i = 0; i < size*size; i++){
			if(!field[i].isDefined()){					//Pick a field that is undefined
				copy = new Board(this);
				CArray choices = new CArray(copy.getField(i).canBe());
				for(char c : choices.getCharArray()){	//Try each possibility
					copy.getField(i).define(c);
					solved = copy.solve();
					if(solved != null) return solved;	//Succeed
					copy = new Board(this);
				}
				break;									//or give up
			}
		}
		return null;
	}
	public String toString(){
		String s = "";
		for(int i = 0; i < size*size; i++){
			s += field[i].toString();
			if(i % size == size - 1 && i+1 < size*size) s += "\n";
		}
		return s;
	}
}
