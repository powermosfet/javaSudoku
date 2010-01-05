class JavaSudoku{
	private static enum Action { solve, print }
	private static void usage(){
		System.out.println("Usage: java JavaSudoku [-m mapfile.map] [-p] [-v] [-s sudokustring] puzzle.sudoku\n");
		System.out.println("-m: Use custom map file");
		System.out.println("-p: Print the sudoku, don't try to solve it");
		System.out.println("-v: Be verbose");
		System.out.println("-s: Load a standard 9x9 sudoku from a string argument");
		System.exit(-1);
	}
	public static void main(String[] args){
		Board b = null;
		Action action = Action.solve;
		String puzzle = "";
		String puzzleString = "";
		String map = "";
		boolean verbose = false;
		/* Process arguments */
		for(int i = 0; i < args.length; i++){	
			if(args[i].equals("-m")){           
				if(i+1 < args.length){          
					map = args[++i];            
				}else{                          
					usage();                    
				}                               
			}else if(args[i].equals("-s")){           
				if(i+1 < args.length){          
					puzzleString = args[++i];            
				}else{                          
					usage();                    
				}                               
			}else if(args[i].equals("-p")){     
				action = Action.print;          
			}else if(args[i].equals("-v")){
				verbose = true;
			}else{                              
				puzzle = args[i];               
			}                                   
		}                                       
		if(puzzle.equals("") && puzzleString.equals("")) usage();
		if(puzzleString.equals("")){
			b = new Board(puzzle, map);
			puzzle = "\"" + puzzle + "\"";
		}else{
			b = new Board(puzzleString);
			puzzle = "sudoku string \"" + puzzleString + "\"";
		}
		switch(action){
			case print:
				System.out.println(b);
				break;
			case solve:
				System.out.println("Solving " + puzzle + "\n" + b);
				Board solved = b.solve(verbose);
				if(solved != null){
					b = solved;
					System.out.println("Success!");
				}else{
					System.out.println("FAILED!");
				}
				System.out.println(b);
				break;
		}
	}
}
