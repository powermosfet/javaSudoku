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
	private boolean scanOne(){
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
	private boolean scanThree(){
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
	private boolean scanFour(){
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
				hasChanged = scanFourSubScan(subSetIndex);
			}
		}
		return hasChanged;
	}
	private boolean scanFourSubScan(int[] subSetIndex){
		boolean hasChanged = false;
		int subSetSize = subSetIndex.length;
		CArray subSet = new CArray();
		ArrayList<Integer> hiddenTwins; 
		ArrayList<Integer> regularTwins; 
		CArray whatShouldICallThis; 
		for(int i : subSetIndex)
			subSet.add(charSet.get(i));
		for(int i = 0; i < size; i++){
			hiddenTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : ROW[i]){
				if(field[f].canBeAllOf(subSet)){
					hiddenTwins.add(f);
					whatShouldICallThis.merge(field[f].canBe());
				}
			}
			if(hiddenTwins.size() == subSetSize){
				whatShouldICallThis.del(subSet);
				Integer[] x = new Integer[hiddenTwins.size()];
				for(int f : hiddenTwins.toArray(x)){
					if(field[f].canNotBe(whatShouldICallThis)) hasChanged = true;
				}
			}
			hiddenTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : COL[i]){
				if(field[f].canBeAllOf(subSet)){
					hiddenTwins.add(f);
					whatShouldICallThis.merge(field[f].canBe());
				}
			}
			if(hiddenTwins.size() == subSetSize){
				whatShouldICallThis.del(subSet);
				Integer[] x = new Integer[hiddenTwins.size()];
				for(int f : hiddenTwins.toArray(x)){
					if(field[f].canNotBe(whatShouldICallThis)) hasChanged = true;
				}
			}
		}
		for(int a : area.areas()){
			hiddenTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : area.getAll(a)){
				if(field[f].canBeAllOf(subSet)){
					hiddenTwins.add(f);
					whatShouldICallThis.merge(field[f].canBe());
				}
			}
			if(hiddenTwins.size() == subSetSize){
				whatShouldICallThis.del(subSet);
				Integer[] x = new Integer[hiddenTwins.size()];
				for(int f : hiddenTwins.toArray(x)){
					if(field[f].canNotBe(whatShouldICallThis)) hasChanged = true;
				}
			}
		}
		/* Scan each row, column and area for regular twins, triplets and so on */
		for(int i = 0; i < size; i++){
			regularTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : ROW[i]){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					whatShouldICallThis.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				System.err.println("Found twins of subset " + subSet + " in row " + i + "\n" + toString());
				for(int f : ROW[i]){
					System.err.print("Field " + f + ": " + field[f].canBe() + " => ");
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
					System.err.println("Field " + f + ": " + field[f].canBe());
				}
				if(hasChanged) return true;
			}
			regularTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : COL[i]){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					whatShouldICallThis.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				System.err.println("Found twins of subset " + subSet + " in column " + i + "\n" + toString());
				for(int f : COL[i]){
					System.err.print("Field " + f + ": " + field[f].canBe() + " => ");
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
					System.err.println(field[f].canBe());
				}
				System.err.println("hasChanged = " + hasChanged);
				if(hasChanged) return true;
			}
		}
		for(int a : area.areas()){
			regularTwins = new ArrayList<Integer>();
			whatShouldICallThis = new CArray();
			for(int f : area.getAll(a)){
				if(field[f].isDefined() && subSet.has(field[f].defined())){
					regularTwins = new ArrayList<Integer>();
					break;
				}
				if(subSet.hasAll(field[f].canBe())){
					whatShouldICallThis.merge(field[f].canBe());
					regularTwins.add(f);
				}
			}
			if(regularTwins.size() == subSetSize){
				System.err.println("Found twins of subset " + subSet + " in area " + a + "\n" + toString());
				for(int f : area.getAll(a)){
					System.err.print("Field " + f + ": " + field[f].canBe() + " => ");
					if(!regularTwins.contains(f)){
						if(field[f].canNotBe(subSet)) hasChanged = true;
					}
					System.err.println("Field " + f + ": " + field[f].canBe());
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
		System.out.println("Solving...");
		return solve(-1);
	}
	public Board solve(int recurse){
		boolean hasChanged = true;
		Board solved;
		/* Loop until there's nothing more to do */
		while(hasChanged){
			hasChanged = false;
			System.err.print("scan [1] ");
			while(scanOne()){
				System.out.print("+");
				hasChanged = true;
			}
			System.err.print("-\nscan [2] ");
			while(scanTwo()){
				System.out.print("+");
				hasChanged = true;
			}
			System.err.print("-\nscan [3] ");
			while(scanThree()){
				System.out.print("+");
				hasChanged = true;
			}
			System.err.print("-\n");
		}
		System.err.print("scan [4] ");
		while(scanFour()){
			System.out.print("+");
			hasChanged = true;
		}System.err.print("-\n");
		if(check()) return null;
		/* are we done? */
		if(finished()) return this;
		if(recurse < 0){
			System.out.print("Inserting random numbers.\nRecursion level: ");
			for(int rec = 0; rec <= RECURSION_MAX; rec++){
				System.out.print(((rec == 0)? "":",") + rec);
				for(int i = 0; i < size*size; i++){
					if(!field[i].isDefined()){
						Board copy = new Board(this);
						CArray choices = new CArray(copy.getField(i).canBe());
						for(char c : choices.getCharArray()){
							copy.getField(i).canBe(choices);
							copy.getField(i).define(c);
							solved = copy.solve(rec);
							if(solved != null){
								System.out.println("");
								return solved;
							}
						}
						copy.getField(i).canBe(choices);
					}
				}
			}
			System.out.println("");
		}else if(recurse > 0){
			Board copy = new Board(this);
			for(int i = 0; i < size*size; i++){
				if(field[i].isDefined()) continue;
				CArray choices = new CArray(field[i].canBe());
				for(char c : choices.getCharArray()){
					copy.getField(i).canBe(choices);
					copy.getField(i).define(c);
					solved = copy.solve(recurse - 1);
					if(solved != null) return solved;
				}
				copy.getField(i).canBe(choices);
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
