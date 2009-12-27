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
		}//for
	}//CArray
	public CArray(char[] chars){
		/* Creates an array with the provided data (chars) */
		data = chars;
	}//CArray
	public char getChar(){
		/* Returns the character if the array
		 * contains only one. otherwise
		 * return '\0'
		 * */
		if(data.length == 1){
			return data[0];
		} else {
			return '\0';
		}//if
	}//getChar
	public CArray copy(){
		CArray theCopy = new CArray(data.length);
		for(int i = 0; i < data.length; i++){
			theCopy.set(i, data[i]);
		}
		return theCopy;
	}
	public void set(int i, char c){
		if(i < 0 || i >= data.length){
			System.out.println("ERROR! CArray index out of range");
			System.exit(-1);
		} else {
		data[i] = c;
		}//if
	}//set
	public char[] getCharArray(){
		/* Returns the data as a char array */
		return data;
	}//getCharArray
	public void sort(){
		/* Sorts the array and deletes
		 * duplicate entries
		 * */
		Arrays.sort(data);
		int duplicates = 0;
		char[] sorted;
		if(data.length > 1){
			for(int i = 1; i < data.length; i++){
				if(data[i] == data[i-1]){
					duplicates++;
				}//if
			}//for
			sorted = new char[data.length - duplicates];
			int i = -1;
			for(char x:data){
				if(i >= 0 && sorted[i] != x){
					sorted[++i] = x;
				}//if
			}//for
			data = sorted;
		}//if
	}//sort
	public boolean has(char c){
		/* Checks if the array contains character c */
		boolean has = false;
		for(char x:data){
			if(x == c){
				has = true;
			}//if
		}//for
		return has;
	}//has
	public int length(){
		/* Returns length of array */
		return data.length;
	}//length
	public void add(char c){
		/* Adds a single character to array */
		char[] bigger = new char[data.length + 1];
		for(int i = 0; i < data.length; i++){
			bigger[i] = data[i];
		}//for
		bigger[data.length] = c;
		data = bigger;
	}//add
	public void add(CArray other){
		/* Concatenates this and other */
		char[] bigger = new char[data.length + other.length()];
		for(int i = 0; i < data.length; i++){
			bigger[i] = data[i];
		}//for
		char[] otherChars = other.getCharArray();
		for(int i = 0; i < other.length(); i++){
			bigger[data.length + i] = otherChars[i];
		}//for
		data = bigger;
	}//add
	public void del(char c){
		/* Deletes all occurrences of c in array */
		int hits = 0;
		char[] smaller;
		for(char x:data){
			if(x == c){
				hits++;
			}//if
		}//for
		smaller = new char[data.length - hits];
		int i = 0;
		for(char x:data){
			if(x != c){
				smaller[i++] = x;
			}//if
		}//for
		data = smaller;
	}//del
	public void del(CArray other){
		/* Deletes every instance of
		 * every character in other
		 * from array
		 * */
		for(char x:other.getCharArray()){
			del(x);
		}//for
	}//minus
	public void merge(CArray other){
		/* Adds all characters from other that isn't
		 * already there. It also sorts the array
		 * */
		add(other);
		sort();
	}//merge
}

