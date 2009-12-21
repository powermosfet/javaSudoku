/*Field class
 *
 * contains:
 **/

class Field{
	private CArray chars;
	private boolean[] legal;

	public Field(char[] chars){
		this(chars, '\0');
	}
	public Field(char[] chars, char c){
	}
	public boolean canBe(char c){
		return false;
	}
	public char[] canBe(){
		return new char[1];
	}
	public void canNotBe(char c){
	}
	public char[] canNotBe(){
		return new char[1];
	}
}
