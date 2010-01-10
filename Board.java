/*Board class
 *
 * A Board object represents the sudoku board.
 * It contains an array of Field objects, and an AreaMap.
 *
 * The solve() method runs the different scan-methods 
 * in a loop until there's nothing more to change.
 * If the board is not finished by then, it picks the first
 * field with more than one option, and calls itself for 
 * each of those options. This way, each possibility is 
 * tested, recursively.
 *
 * The solve() method returns a new Board object if the 
 * solve was successful. Otherwisw it returns null.
 * */

import java.io.* ;
import java.util.* ;

class Board{
	private final int[][] ROW;
	private final int[][] COL;
	private Field[] field;
	private AreaMap area = null;
	private CArray charSet;
	private int size;

	public Board(String puzzleString){
		if(puzzleString.length() != 81){
			System.err.println("Could not parse puzzle string");
			System.exit(-1);
		}
		size = 9;
		field = new Field[81];
		area = new AreaMap("map/9.map", size);
		char[] cCharSet = {'1','2','3','4','5','6','7','8','9'};
		charSet = new CArray(cCharSet);
		for(int i = 0; i < 81; i++){
			char c = puzzleString.charAt(i);
			if(charSet.has(c))
				field[i] = new Field(charSet, c);
			else
				field[i] = new Field(charSet);
		}
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
		/* This method looks for n equal fields with n possibilities
		 * in the same unit (row / column / area)
		 * aka subset scan
		 * */
		boolean hasChanged = false;
		for(int i = 0; i < size; i++){
			if(subSetScan(ROW[i])){
				if(check()) return false;
				hasChanged = true;
			}
			if(subSetScan(COL[i])){
				if(check()) return false;
				hasChanged = true;
			}
		}
		for(int a : area.areas()){
			if(subSetScan(area.getAll(a))){
				if(check()) return false;
				hasChanged = true;
			}
		}
		return hasChanged;
	}
	private boolean subSetScan(int[] fields) throws NoLegalCharactersException{
		boolean hasChanged = false;
		ArrayList<CArray> unique = new ArrayList<CArray>();		//list of unique fields
		ArrayList<CArray> duplicates = new ArrayList<CArray>();	//list of fields that has duplicates
		ArrayList<Integer> dupCount = new ArrayList<Integer>();	//the number of duplicates 
		int undefined = 0;										//the number of undefined fields in this row
		for(int f : fields){
			if(field[f].isDefined()) continue;
			if(field[f].canBe().isEmpty()) return true;
			undefined++;
			CArray legal = field[f].canBe();
			int pos =  -1;
			for(int i = 0; i < unique.size(); i++){
				if(unique.get(i).equals(legal)) pos = i;		//Has this subset occured before?
			}
			if(pos < 0){
				unique.add(new CArray(legal));					//add it to unique list
			}else{
				pos = -1;
				for(int i = 0; i < duplicates.size(); i++){
					if(duplicates.get(i).equals(legal)) pos = i;
				}
				if(pos < 0){
					duplicates.add(new CArray(legal));			//add it to duplicate list
					dupCount.add(2);
				}else{
					dupCount.set(pos, dupCount.get(pos) + 1);	//or count another duplicate
				}
			}
		}
		if(undefined > 2){										//If there's more than 2 undefined areas
			for(int j = 0; j < duplicates.size(); j++){			//scan through the duplicate subsets
				if(duplicates.get(j).length() == dupCount.get(j)){//if the number of identical subsets equals the size of the subset, we have a match!
					for(int f : fields){						//remove the subset from all fields that is not a duplicate of the subset
						if(!field[f].isDefined() && !field[f].canBe().equals(duplicates.get(j))){
							if(field[f].canNotBe(duplicates.get(j))) hasChanged = true;
						}
					}
				}
			}
		}
		return hasChanged;
	}
	public boolean check(){
		/* Checks if the defined numbers
		 * in the board follow the sudoku
		 * rules. Returns true if there are
		 * errors, false if the board is OK
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
		/* Checks if every field is defined */
		for(int i = 0; i < size*size; i++){
			if(field[i].isDefined() == false) return false;
		}
		return true;
	}
	public Board solve(boolean verbose, boolean bruteForce){
		/* The solve() method runs the different scan-methods 
		 * in a loop until there's nothing more to change.
		 * If the board is not finished by then, it picks the first
		 * field with more than one option, and calls itself for 
		 * each of those options. This way, each possibility is 
		 * tested, recursively.
		 *
		 * The solve() method returns a new Board object if the 
		 * solve was successful. Otherwise it returns null.
		 * */
		boolean hasChanged = true;
		Board solved, copy;
		/* Loop until there's nothing more to do */
		while(!bruteForce && hasChanged){
			hasChanged = false;
			try{
				while(scanOne()) hasChanged = true;
				if(hasChanged) continue;
				while(scanTwo()) hasChanged = true;
				if(hasChanged) continue;
				while(scanThree()) hasChanged = true;
				if(hasChanged) continue;
				while(scanFour()) hasChanged = true;
			}catch(NoLegalCharactersException e){
				return null;
			}
		}
		if(check()) return null;
		/* are we done? */
		if(finished()) return this;
		/* If we are going to be verbose, print the board in the current state */
		if(verbose){
			for(int i = 0; i < size; i++)
				System.out.print((i+1 < size)? "**":"*");
			System.out.println("\n" + toString());
		}
		/* The bruteforce part of the solve method */
		for(int i = 0; i < size*size; i++){
			if(!field[i].isDefined()){					//Pick a field that is undefined
				copy = new Board(this);
				CArray choices = new CArray(copy.getField(i).canBe());
				for(char c : choices.getCharArray()){	//Try each possibility
					copy.getField(i).define(c);
					solved = copy.solve(verbose, bruteForce);
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
			if(i%size == size-1 && i+1 < size*size) s += "\n";
		}
		return s;
	}
}
