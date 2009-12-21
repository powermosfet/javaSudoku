/*CArray class
 *
 * contains:
 **/

import java.util.Arrays;

class CArray{
	private char[] data;

	public CArray(int size){
		data = new char[size];
		for(char x:data){
			x = '\0';
		}
	}
	public CArray(char[] chars){
		data = chars;
	}
	public char getChar(){
		if(this.data.length == 1){
			return this.data[0];
		} else {
			return '\0';
		}
	}
	public char[] getCharArray(){
		return this.data;
	}
	public void sort(){
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
		boolean has = false;
		for(char x:this.data){
			if(x == c){
				has = true;
			}
		}
		return has;
	}
	public int length(){
		return this.data.length;
	}
	public void add(char c){
		char[] bigger = new char[this.data.length + 1];
		for(int i = 0; i < this.data.length; i++){
			bigger[i] = this.data[i];
		}
		bigger[this.data.length] = c;
		this.data = bigger;
	}
	public void add(CArray other){
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
		for(char x:other.getCharArray()){
			this.del(x);
		}
	}
	public void merge(CArray other){
		this.add(other);
		this.sort();
	}
}

