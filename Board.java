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
	private Field[] field;
	private AreaMap area = null;
	CArray charSet;
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
		area = new AreaMap(map, size);
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
						if(row < 0){
							row = i;
						}else{
							row = size;
						}
					}
					if(field[size*j+i].canBe(c)){
						if(col < 0){
							col = i;
						}else{
							col = size;
						}
					}
				}
				if(row >=0 && row < size){
					field[row].define(c);
					hasChanged = true;
				}
				if(col >=0 && col < size){
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
				if(area >=0 && area < size*size){
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
				int row = -1;
				int col = -1;
				/* check if it's legal in only one row or column */
				for(int f : area.getAll(a)){
					if(field[f].canBe(c)){
						row = (row < 0)? (int)(f / size) : size;
						col = (col < 0)? (int)(f % size) : size;
					}
				} /* If that's the case, remove it from 
				   * all the others in the row */ 
				if(row >= 0 && row < size){
					for(int f : area.outsidersRow(row, a)){
						if(field[f].canNotBe(c)){
							hasChanged = true;
						}
					}
				} /* ...or column */
				if(col >= 0 && col < size){
					for(int f : area.outsidersCol(col, a)){
						if(field[f].canNotBe(c)){
							hasChanged = true;
						}
					}
				}
			}
		}
		return hasChanged;
	}
	public boolean solve(int recurse){
		boolean hasChanged = true;
		System.out.print("Solving");
		for(int i = 0; hasChanged; i++){
			System.out.print(".");
			hasChanged = false;
			if(aScan()) hasChanged = true;
			//if(anotherScan()) hasChanged = true;
			//if(yetAnotherScan()) hasChanged = true;
		}
		System.out.print("\n");
		return true;
	}
	public String toString(){
		String s = "";
		for(int i = 0; i < size*size; i++){
			char c = field[i].defined();
			if(c == '\0') c = ' ';
			s += "" + c + " ";
			if(i % size == size - 1 && i+1 < size*size) s += "\n";
		}
		return s;
	}
}
