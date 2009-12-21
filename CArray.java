/*CArray class
 *
 * This class is a wrapper around a 
 * char array. It provides a number of
 * methods that come in handy in the
 * javaSudoku program.
 **/

import java.util.Arrays;

class CArray{
	/* The actual char array */
	private char[] data;						

	public CArray(int size){
		/* Creates an empty array (filled with '\0')
		 * of size "size"
		 * */
		data = new char[size];
		for(char x:data){
			x = '\0';
		}
	}
	public CArray(char[] chars){
		/* Creates an array with the provided data (chars) */
		data = chars;
	}
	public char getChar(){
		/* Returns the character if the array
		 * contains only one. otherwise
		 * return '\0'
		 * */
		if(this.data.length == 1){
			return this.data[0];
		} else {
			return '\0';
		}
	}
	public char[] getCharArray(){
		/* Returns the data as a char array */
		return this.data;
	}
	public void sort(){
		/* Sorts the array and deletes
		 * duplicate entries
		 * */
		Arrays.sort(this.data);
		int duplicates = 0;
		char[] sorted;
		if(this.data.length > 1){
			for(int i = 1; i < this.data.length; i++){
				if(this.data[i] == this.data[i-1]){
					duplicates++;
				}
			}
			sorted = new char[this.data.length - duplicates];
			int i = -1;
			for(char x:this.data){
				if(i >= 0 && sorted[i] != x){
					sorted[++i] = x;
				}
			}
			this.data = sorted;
		}
	}
	public boolean has(char c){
		/* Checks if the array contains character c */
		boolean has = false;
		for(char x:this.data){
			if(x == c){
				has = true;
			}
		}
		return has;
	}
	public int length(){
		/* Returns length of array */
		return this.data.length;
	}
	public void add(char c){
		/* Adds a single character to array */
		char[] bigger = new char[this.data.length + 1];
		for(int i = 0; i < this.data.length; i++){
			bigger[i] = this.data[i];
		}
		bigger[this.data.length] = c;
		this.data = bigger;
	}
	public void add(CArray other){
		/* Concatenates this and other */
		char[] bigger = new char[this.data.length + other.length()];
		for(int i = 0; i < this.data.length; i++){
			bigger[i] = this.data[i];
		}
		char[] otherChars = other.getCharArray();
		for(int i = 0; i < other.length(); i++){
			bigger[this.data.length + i] = otherChars[i];
		}
		this.data = bigger;
	}
	public void del(char c){
		/* Deletes all occurrences of c in array */
		int hits = 0;
		char[] smaller;
		for(char x:this.data){
			if(x == c){
				hits++;
			}
		}
		smaller = new char[this.data.length - hits];
		int i = 0;
		for(char x:this.data){
			if(x != c){
				smaller[i++] = x;
			}
		}
		this.data = smaller;
	}
	public void minus(CArray other){
		/* Deletes every instance of
		 * every character in other
		 * from array
		 * */
		for(char x:other.getCharArray()){
			this.del(x);
		}
	}
	public void merge(CArray other){
		/* Adds all characters from other that isn't
		 * already there. It also sorts the array
		 * */
		this.add(other);
		this.sort();
	}
}

