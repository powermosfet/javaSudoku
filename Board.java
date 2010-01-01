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
		Scanner s = new Scanner(puzzle);
		String charSetString = "";
		if(s.hasNext()){
			charSetString = s.next();
		}else{
			System.out.println("Error parsing puzzle file \'" + puzzle + "\'.");
			System.exit(-1);
		}
		charSet = new CArray(charSetString.split(","));
		size = charSet.length();
		for(int i = 0; i < size * size; i++){
			if(s.hasNext()){
				char c = s.next().charAt(0);
				field[i] = (charSet.has(c)) ? new Field(charSet, c) : new Field(charSet);
			}else{
				System.out.println("Error parsing puzzle file \'" + puzzle + "\'.");
				System.exit(-1);
			}
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
		/* for every field in the board */
		for(int i = 0; i < field.length; i++){
			int rowOffset = (int)(i / size) * size;
			int colOffset = (int)(i % size);
			CArray defined = new CArray();
			for(int j = 0; i < size; i++){
				/* Collect defined characters in row */
				if((rowOffset+j != i) && (field[rowOffset+j].defined() != '\0')){
					defined.add(field[rowOffset+j].defined());
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
			defined.sort();
			if(field[i].canNotBe(defined)){
				changed = true;
			}
		}
		return changed;
	}
	private void anotherScan(){
		/* This method checks if any number is
		 * legal only in one row or column of an
		 * area. It then removes it from every field
		 * that is in the same row or column but not in the same area
		 * */
		/* Iterate through each area in the board */
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
						field[f].canNotBe(c);
					}
				} /* ...or column */
				if(col >= 0 && col < size){
					for(int f : area.outsidersCol(col, a)){
						field[f].canNotBe(c);
					}
				}
			}
		}
	}
	public boolean solve(int recurse){
		return false;
	}
	public String toString(){
		return new String("");
	}
}
