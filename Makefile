CC = javac

all: Field Board AreaMap

Board: Board.java
	@$(CC) $^

Field: Field.java
	@$(CC) $^

AreaMap: AreaMap.java
	@$(CC) $^

d: all
