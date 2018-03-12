package com.percy.interview.othello.game.coordinates.manager;

import com.percy.interview.othello.game.coordinates.Coordinate;

/*
 * Convert different format of indices to a generic form such 
 * that 3A will become Coordinate(3,1)
 */
public interface CoordManager {
	public void init(int size);
	public char[] getXAxisName();
	public char[] getYAxisName();
	public Coordinate getCoordinate(String coordStr);
	public Coordinate getCoordinate(int x, int y);

}
