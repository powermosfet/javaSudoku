class JavaSudoku{
	private static enum Action { solve, print }
	private static void usage(){
		System.out.println("Usage: java JavaSudoku [-m mapfile.map] [-p] puzzle.sudoku");
		System.exit(-1);
	}
	public static void main(String[] args){
		Action action = Action.solve;
		String puzzle = "";
		String map = "";
		for(int i = 0; i < args.length; i++){		//Process arguments ////
			if(args[i].equals("-m")){                                     //
				if(i+1 < args.length){                                    //
					map = args[++i];                                      //
				}else{                                                    //
					usage();                                              //
				}                                                         //
			}else if(args[i].equals("-p")){                               //
				action = Action.print;                                    //
			}else{                                                        //
				puzzle = args[i];                                         //
			}                                                             //
		}                                                               ////
		if(puzzle.equals("")) usage();
		Board b = new Board(puzzle, map);
		switch(action){
			case print:
				System.out.println(b);
				break;
			case solve:
				System.out.println(b);
				Board solved = b.solve();
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
