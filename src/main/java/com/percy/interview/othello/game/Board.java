package com.percy.interview.othello.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.percy.interview.othello.config.Properties;
import com.percy.interview.othello.game.coordinates.Coordinate;

public class Board {
	final int size;
	final int totalPieces;
	final char[] xAxisNames;
	final char[] yAxisName;
	final short[][] contents;
	static final short EMPTY = 0;
	static final short xFilled = 1;
	static final short oFilled = 2;
	static final String LEFT_PIECE_SEPARATOR = Properties.getStringProperty("board.left.separator", "[");
	static final String RIGHT_PIECE_SEPARATOR = Properties.getStringProperty("board.right.separator", "]");

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Board(int size, char[] xAxisNames, char[] yAxisName) {
		this.size = size;
		this.totalPieces = size * size;
		this.contents = new short[size][size];
		this.xAxisNames = xAxisNames;
		this.yAxisName = yAxisName;
	}

	public int getSize() {
		return size;
	}
	
	public int getTotal() {
		return totalPieces;
	}
	
	/*
	 * Fill without regard to current value
	 */
	public void fill(Player player, Coordinate coord) {
		int x = coord.getX();
		int y = coord.getY();
		switch(player) {
		case O:
			contents[x][y] = oFilled;
			break;
		case X:
			contents[x][y] = xFilled;
			break;
		}
		logger.debug("Coord {} taken by {}", coord, player);
	}
	
	public short checkFill(Coordinate coord) {
		short field = contents[coord.getX()][coord.getY()];
		return field;
	}
	
	public String pieceStrValue(short value) {
		switch (value) {
		case EMPTY:
			return " ";
		case oFilled:
			return Player.O.name();
		case xFilled:
			return Player.X.name();
		default:
			throw new RuntimeException("Unable to recognize piece value: " + value);
		}
	}
	
	public boolean isValidIndex(int x, int y) {
		return x >= 0 && x <= size-1 && y >= 0 && y <= size-1;
	}
	public String toString() {
        StringBuilder printableBoard = new StringBuilder("");
        for (int i = 0; i < size; ++i) {
        		printableBoard.append(yAxisName[i]).append(" ");
        		for (int j = 0; j < size; ++j) {
            		printableBoard.append(LEFT_PIECE_SEPARATOR).append(pieceStrValue(contents[j][i])).append(RIGHT_PIECE_SEPARATOR);
        		}
        		printableBoard.append(System.lineSeparator());
        		if (i == size - 1) {
        			printableBoard.append("  ");
        			for (int j = 0; j < size; ++j) {
        				printableBoard.append(" ").append(xAxisNames[j]).append(" ");
        			}
        		}
        }
        return printableBoard.toString();
	}
	
	
}
