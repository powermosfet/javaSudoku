class Field{
	private CArray chars;
	private CArray legal;

	public Field(CArray charSet){
		this(charSet, '\0');
	}
	public Field(CArray charSet, char c){
		chars = new CArray();
		if(c != '\0'){
			if(charSet.has(c)){
				chars = charSet;
				legal = new CArray(c);
			} else {
				System.out.println("ERROR! Could not create Field. \'" + c + "\' is not part of the character set.");
				System.exit(-1);
			}
		} else {
			chars = charSet;
			legal = charSet.copy();
		}
	}
	public void define(char c){
		if(chars.has(c)){
			legal = new CArray(c);
		}else{
			System.err.println("ERROR! could not define field as \'" + c + "\'. It is not a legal character");
			System.exit(-1);
		}
	}
	public char defined(){
		return legal.getChar();
	}
	public boolean canBe(char c){
		return legal.has(c);
	}
	public CArray canBe(){
		return legal;
	}
	public boolean canNotBe(CArray c){
		return legal.del(c);
	}
	public boolean canNotBe(char c){
		return legal.del(c);
	}
	public CArray canNotBe(){
		CArray returnValue = chars.copy();
		returnValue.del(legal);
		return returnValue;
	}
}
