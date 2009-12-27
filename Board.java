/*Board class
 *
 * contains:
 * - Field objects for all fields in the sudoku board
 * - An AreaMap that contains a map of the different areas
 * - solve method
 * - toString method
 * - And more!
 **/

import java.lang.* ;

class Board{
	private Field[] field;
	private AreaMap area;
	private int size;

	public Board(CArray chars, CArray puzzle){
		this(chars, puzzle, "" + chars.length() + ".map");
	}
	public Board(CArray chars, CArray puzzle, String mapFile){
		int n = chars.length();
		int m = puzzle.length();
		if( n*n != m ){
			System.out.println("Board size does not match number of characters");
			System.exit(-1);
		}
		size = n;
		area = new AreaMap(mapFile);
	}
	private void aScan(){
	}
	private void anotherScan(){
	}
	public boolean solve(int recurse){
		return false;
	}
	public String toString(){
		return new String("");
	}
}
