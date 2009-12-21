CC = javac

all: Field Board AreaMap

Board: Board.java
	@$(CC) $^

Field: Field.java
	@$(CC) $^

AreaMap: AreaMap.java
	@$(CC) $^

CArray: CArray.java
	@$(CC) $^

d: all
