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
	private Field[] field;
	private AreaMap area = null;
	private CArray charSet;
	private int size;

	public Board(String puzzle, String map){
		File puzzleFile = new File(puzzle);
		try{
			Scanner s = new Scanner(puzzleFile);
			String charSetString = "";
			if(s.hasNext()){
				charSetString = s.next();
			}else{
				System.err.println("Error parsing puzzle file \'" + puzzle + "\'.");
				System.err.println("Could not find character set");
				System.exit(-1);
			}
			charSet = new CArray(charSetString.split(","));
			size = charSet.length();
			field = new Field[size*size];
			for(int i = 0; i < size*size; i++){
				if(s.hasNext()){
					char c = s.next().charAt(0);
					if(charSet.has(c)){
						field[i] = new Field(charSet, c);
					}else{
						field[i] = new Field(charSet);
					}
				}else{
					System.err.println("Error parsing puzzle file \'" + puzzle + "\'.");
					System.err.println("Not enough fields");
					System.exit(-1);
				}
			}
		} catch(FileNotFoundException e){
			System.err.println(e);
			System.exit(-1);
		}
		if(map.equals("")){
			map = "map/" + size + ".map";
			System.out.println("Using default map file \'" + map + "\'");
		}else{
			System.out.println("Using custom map file \'" + map + "\'");
		}
		area = new AreaMap(map, size);
	}
	public Board(Board original){
		size = original.getSize();
		field = new Field[size*size];
		for(int i = 0; i < size*size; i++){
			field[i] = new Field(original.getField(i));
		}
		area = new AreaMap(original.getArea());
		charSet = new CArray(original.getCharSet());
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
	private boolean aScan(){
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
	private boolean anotherScan(){
		/* This method checks if any character is only allowed
		 * in one of the fields in its row, column or area.
		 * If that's the case, that field is set to that character
		 * */
		boolean hasChanged = false;
		for(char c : charSet.getCharArray()){
			for(int i = 0; i < size; i++){
				int row = -1;
				int col = -1;
				for(int j = 0; j < size; j++){
					if(field[size*i+j].canBe(c)){
						row = (row < 0) ? size*i+j : size*size;
					}
					if(field[size*j+i].canBe(c)){
						col = (col < 0) ? size*j+i : size*size;
					}
				}
				if(row >=0 && row < size*size && !field[row].isDefined()){
					field[row].define(c);
					hasChanged = true;
				}
				if(col >=0 && col < size && !field[col].isDefined()){
					field[col].define(c);
					hasChanged = true;
				}
			}
			for(int a : this.area.areas()){
				int area = -1;
				for(int f : this.area.getAll(a)){
					if(field[f].canBe(c)){
						if(area < 0){
							area = f;
						}else{
							area = size*size;
						}
					}
				}
				if(area >=0 && area < size*size && !field[area].isDefined()){
					field[area].define(c);
					hasChanged = true;
				}
			}
		}
		return hasChanged;
	}
	private boolean yetAnotherScan(){
		/* This method checks if any number is
		 * legal only in one row or column of an
		 * area. It then removes it from every field
		 * that is in the same row or column but not in the same area
		 * */
		/* Iterate through each area in the board */
		boolean hasChanged = false;
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
	public boolean check(boolean exit){
		/* Checks if the defined numbers
		 * in the board follow the sudoku
		 * rules
		 * */
		boolean error = false;
		for(int i = 0; i < size; i++){
			CArray row = new CArray();
			CArray col = new CArray();
			for(int j = 0; j < size; j++){
				char r = field[i*size+j].defined();
				char c = field[j*size+i].defined();
				if(r != '\0'){
					if(row.has(r)) error = true;
					row.add(r);
				}
				if(c != '\0'){
					if(col.has(c)) error = true;
					col.add(c);
				}
			}
		}
		for(int a : area.areas()){
			CArray definedChars = new CArray();
			for(int f : area.getAll(a)){
				char c = field[f].defined();
				if(c != '\0'){
					if(definedChars.has(c)) error = true;
					definedChars.add(c);
				}
			}
		}
		if(error && exit){
			System.err.println("ERROR! board is not in a legal state!");
			System.err.println(toString());
			System.exit(-1);
		}
		return error;
	}
	public boolean finished(){
		boolean isFinished = true;
		for(Field f : field){
			if(f.defined() == '\0') isFinished = false;
		}
		return isFinished;
	}
	public Board solve(){
		System.out.println("Solving...");
		return solve(-1);
	}
	public Board solve(int recurse){
		boolean hasChanged = true;
		Board solved;
		for(int i = 0; hasChanged; i++){
			hasChanged = false;
			if(aScan()) hasChanged = true;
			if(check(recurse < 0)) break;
			if(anotherScan()) hasChanged = true;
			if(check(recurse < 0)) break;
			if(yetAnotherScan()) hasChanged = true;
			if(check(recurse < 0)) break;
		}
		if(finished() && !check(false)) return this;
		if(recurse < 0){
			System.out.print("Inserting random numbers.\nRecursion level: ");
			for(int rec = 0; rec <= RECURSION_MAX; rec++){
				System.out.print((rec == 0)? "" + rec : "," + rec);
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
