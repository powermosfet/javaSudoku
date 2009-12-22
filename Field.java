/*Field class
 *
 * contains:
 **/

class Field{
	private CArray chars;
	private CArray legal;

	public Field(CArray chars){
		this(chars, '\0');
	}
	public Field(CArray charSet, char c){
		if(c != '\0'){
			if(chars.has(c)){
				chars = charSet;
				legal = new CArray(1);
				legal.set(0, c);
			} else {
				System.out.println("ERROR! Could not create Field. \'" + c + "\' is not part of the character set.");
				System.exit(-1);
			}
		} else {
			chars = charSet;
			legal = charSet;
		}
	}
	public boolean canBe(char c){
		return this.legal.has(c);
	}
	public CArray canBe(){
		return this.legal;
	}
	public void canNotBe(char c){
		this.legal.del(c);
	}
	public CArray canNotBe(){
		CArray returnValue = this.chars.copy();
		returnValue.del(this.legal);
		return returnValue;
	}
}
