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

	public CArray(){
		/* Creates an empty CArray */
		data = null;
	}//CArray
	public CArray(char c){
		/* Creates a CArray with a single character */
		data = new char[1];
		data[0] = c;
	}//CArray
	public CArray(int size){
		/* Creates a CArray (filled with '\0')
		 * of size "size"
		 * */
		data = new char[size];
		for(char x:data){
			x = '\0';
		}//for
	}//CArray
	public CArray(String[] chars){
		/* Creates a CArray from an array of Strings.
		 * uses the first character in each String.
		 * */
		data = new char[chars.length];
		for(int i = 0; i < chars.length; i++){
			if(chars[i].length() >= 1){
				data[i] = chars[i].charAt(0);
			}else{
				data[i] = '\0';
			}
		}//for
	}//CArray
	public CArray(char[] chars){
		/* Creates a CArray from a char array*/
		data = chars;
	}//CArray
	public CArray(CArray original){
		/* A copy constructor.
		 * Creates a CArray identical to original
		 **/
		data = new char[original.length()];
		for(int i = 0; i < original.length(); i++){
			data[i] = original.get(i);
		}//for
	}//CArray
	public String toString(){
		/* Returns a String representation of the array */
		if(data == null) return "null";
		String s = "[ ";
		for(char c : data){
			String adds = (c != '\0')? c+" " : "\\0 ";
			s += adds;
		}//for
		s += "]";
		return s;
	}//toString
	public char getChar(){
		/* Returns the character if the array
		 * contains only one. otherwise
		 * return '\0'
		 * */
		if(data != null && data.length == 1){
			return data[0];
		}else{
			return '\0';
		}
	}//getChar
	public char get(int i){
		/* Returns the specified character */
		if(i < 0 || i >= data.length){
			System.err.println("ERROR! CArray.get index out of range");
			System.exit(-1);
		}//if
		return data[i];
	}//get
	public void set(int i, char c){
		/* set the character at the given index
		 * to c
		 * */
		if(data == null || i < 0 || i >= data.length){
			System.out.println("ERROR! CArray.set index out of range");
			System.exit(-1);
		}//if
		data[i] = c;
	}//set
	public char[] getCharArray(){
		/* Returns the data as a char array */
		return data;
	}//getCharArray
	public void unique(){
		/* Remove all duplicate items from array */
		if(data == null) return;
		CArray a = new CArray();
		for(char c : data){
			if(!a.has(c)) a.add(c);
		}
		data = a.getCharArray();
	}
	public void sort(){
		/* Sorts the array and deletes
		 * duplicate entries
		 * */
		if(data == null) return;					//Don't sort if it's empty
		Arrays.sort(data);
		unique();
	}//sort
	public boolean has(char c){
		/* Checks if the array contains character c */
		if(data == null) return false;		//It doesn't contain the character if it's empty
		for(char x:data){					//Check each character
			if(x == c) return true;
		}//for
		return false;
	}//has
	public boolean hasAll(CArray chars){
		if(data == null) return false;
		for(char c : chars.getCharArray()){
			boolean hasCharacter = false;
			for(char cThis : data){
				if(cThis == c) hasCharacter = true;
			}
			if(!hasCharacter) return false;
		}
		return true;
	}
	public int length(){
		/* Returns length of array */
		return (data == null)? 0 : data.length;
	}//length
	public void add(char c){
		/* Adds a single character to array */
		int len = (data == null)? 0 : data.length;	//Get number of characters in old array
		char[] bigger = new char[len + 1];			//Create new array that is one bigger
		for(int i = 0; i < len; i++){
			bigger[i] = data[i];					//Copy existing data, if any
		}//for
		bigger[len] = c;							//Add the new character at the last position
		data = bigger;
	}//add
	public void add(CArray other){
		/* Combines this and other to one big CArray */
		if(data == null){							//If this CArray is empty, just overwrite it
			data = other.getCharArray();
		}//if
			char[] bigger = new char[data.length + other.length()];
			for(int i = 0; i < data.length; i++){	//Create new array big enough to hold both arrays' data
				bigger[i] = data[i];				//Copy over the data from this CArray
			}//for
			for(int i = 0; i < other.length(); i++){
				bigger[data.length + i] = other.get(i);
			}//for
			data = bigger;
	}//add
	public boolean del(char c){
		/* Deletes all occurrences of c in array */
		if(data == null) return false;
		int hits = 0;
		char[] smaller;
		for(char x:data){
			if(x == c){				//Search for c in data
				hits++;
			}//if
		}//for
		if(hits == data.length){
			data = null;
			return true;
		}else if(hits > 0){
			smaller = new char[data.length - hits];
			int i = 0;					//Allocate new array
			for(char x:data){
				if(x != c){
					smaller[i++] = x;	//Add all characters that are not c
				}//if
			}//for
			data = smaller;
			return true;
		}else{
			return false;
		}
	}//del
	public boolean del(CArray other){
		/* Deletes every instance of
		 * every character in other
		 * from array
		 * */
		if(other.isEmpty()) return false;	//nothing to delete
		boolean hasDeleted = false;
		for(char x:other.getCharArray()){	//For each character in other
			if(del(x)) hasDeleted = true;	//delete it from this
		}//for
		return hasDeleted;					//did we delete anything?
	}//del
	public void merge(CArray other){
		/* Adds all characters from other that isn't
		 * already there. It also sorts the array
		 * */
		add(other);
		unique();
	}//merge
	public boolean isEmpty(){
		/* Checks if the array is empty */
		return (data == null);
	}//isEmpty
	public void empty(){
		/* Deletes all content */
		data = null;
	}//empty
	public boolean equals(CArray other){
		if(isEmpty() && other.isEmpty()) return true;
		if(isEmpty() || other.isEmpty()) return false;
		if(data.length != other.length()) return false;
		sort();
		other.sort();
		for(int i = 0; i < data.length; i++){
			if(data[i] != other.get(i)) return false;
		}
		return true;
	}
}

