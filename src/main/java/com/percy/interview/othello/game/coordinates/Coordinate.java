package com.percy.interview.othello.game.coordinates;

public class Coordinate {
	final int x;
	final int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean equal(Coordinate coord2) {
		if (this.x == coord2.x && this.y == coord2.y)
			return true;
		else
			return false;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(x).append("]-[").append(y).append("]");
		return sb.toString();
	}
}
