class JavaSudoku{
	public static void main(String[] args){
		if(args.length == 1){
			Board testBoard = new Board(args[0], "9.map");
			System.out.println(testBoard);
			testBoard.solve(-1);
			System.out.println(testBoard);
		}
	}
}
