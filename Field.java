/* Field class
 *
 * A Field object represents a single field on the sudoku board.
 * It contains a CArray of the character set used in the puzzle,
 * and a CArray of the characters that are legal in this field
 * */

class NoLegalCharactersException extends Exception {
	public NoLegalCharactersException(String msg){
		super(msg);
	}
}

class Field{
	private final CArray charSet;
	private CArray legal;

	public Field(Field original){
		/* Copy constructor. creates a new field
		 * with identical data to original
		 * */
		charSet = new CArray(original.getCharSet());
		legal = new CArray(original.getLegal());
	}
	public Field(CArray charSet){
		/* Create an undefined Field. all characters
		 * in charSet are legal. */
		this(charSet, '\0');
	}
	public Field(CArray charSet, char c){
		/* Initializes the Field. If c
		 * is '\0', the field is undefined. If not, 
		 * the field is defined to c
		 * */
		this.charSet = charSet;				//charSet is final, and can be assigned directly
		if(c == '\0'){						//If c is '\0', create an undefined field
			legal = new CArray(charSet);	//legal will be modified, so we need to make a copy.
		}else{
			if(!charSet.has(c)){
				System.out.println("ERROR! Could not create Field. \'" + c + "\' is not part of the character set.");
				System.exit(-1);
			}
			legal = new CArray(c);			//create CArray with the single char c
		}
	}
	public CArray getCharSet(){
		/* Return charSet */
		return charSet;
	}
	public CArray getLegal(){
		/* Return legal */
		return legal;
	}
	public void define(char c){
		/* Reduce legal to contain only
		 * the single character c
		 * */
		if(legal.has(c)){					//Check that c is allowed in this field
			legal = new CArray(c);
		}else{
			System.err.println("ERROR! could not define field as \'" + c + "\'. It is not a legal character");
			System.err.println("Legal characters are " + legal);
			System.exit(-1);
		}
	}
	public boolean isDefined(){
		return (legal.length() == 1);
	}
	public char defined(){
		/* Check if this field is defined */
		return legal.getChar();
	}
	public boolean canBeAllOf(CArray chars){
		for(char c : chars.getCharArray())
			if(!canBe(c)) return false;
		return true;
	}
	public void canBe(CArray legal) throws NoLegalCharactersException{
		/* Set the characters allowed in this field */
		this.legal = legal;
		if(legal.isEmpty()) throw new NoLegalCharactersException("Field has no legal characters");
	}
	public boolean canBe(char c){
		/* Check if character c is allowed in this field */
		return legal.has(c);
	}
	public CArray canBe(){
		/* Returns a list of characters that are allowed in this field */
		return legal;
	}
	public boolean canNotBe(CArray c) throws NoLegalCharactersException{
		/* Remove all characters in CArray c from legal */
		boolean returnValue = legal.del(c);
		if(legal.isEmpty()) throw new NoLegalCharactersException("Field has no legal characters");
		return returnValue;
	}
	public boolean canNotBe(char c) throws NoLegalCharactersException{
		/* remove the character c from legal */
		boolean returnValue = legal.del(c);
		if(legal.isEmpty()) throw new NoLegalCharactersException("Field has no legal characters");
		return returnValue;
	}
	public String toString(){
		if(legal.isEmpty()){
			System.err.println("ERROR!");
			return "ERROR! ";
		}
		if(isDefined())
			return "" + defined() + " ";
		else
			return "  ";
	}
}
