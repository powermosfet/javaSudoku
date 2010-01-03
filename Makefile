CC = javac

all: Field Board AreaMap JavaSudoku

JavaSudoku: JavaSudoku.java
	@$(CC) $^

Board: Board.java
	@$(CC) $^

Field: Field.java
	@$(CC) $^

AreaMap: AreaMap.java
	@$(CC) $^

CArray: CArray.java
	@$(CC) $^

d: all
	xterm -e "java JavaSudoku hard.sudoku ; read"

clean:
	rm *.class
