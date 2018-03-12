package com.percy.interview.othello.game.coordinates.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.percy.interview.othello.game.coordinates.Coordinate;

public class NumAlphaCoordManager implements CoordManager{
	int size;
	char[] xAxisNames;
	char[] yAxisNames;
	static final char Y_AXIS_STARTING = '1';
	static final char X_AXIS_STARTING = 'a';
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Map<String, Coordinate> cachedCoords;
	
	public NumAlphaCoordManager() {
		this.cachedCoords = new HashMap<String, Coordinate>();
	}
	
	
	/*
	 * Set up frames for 2 axis
	 */
	public void init(int size) {
		this.size = size;
		xAxisNames = new char[size];
		yAxisNames = new char[size];
		for (int i = 0; i < size; i++) {
			yAxisNames[i] = (char)(i + '1');
			xAxisNames[i] = (char) ('a' + i);
		}
	}
	
	
	/*
	 * Initial Coordinates
	 */
	public Coordinate getCoordinate(String coordStr) {
		if (!validateCoord(coordStr))
			return null;
		char[] ch = coordStr.toLowerCase().toCharArray();
		int xIndex;
		int yIndex;
		if (Character.isDigit(ch[0])) {
			yIndex = ch[0] - Y_AXIS_STARTING;
			xIndex = ch[1] - X_AXIS_STARTING;
		}
		else {
			xIndex = ch[0] - X_AXIS_STARTING;
			yIndex = ch[1] - Y_AXIS_STARTING;

		}
		Coordinate coord = getCoordinate(xIndex, yIndex);
		logger.trace("Coordinate {} Created from String {}", coord, coordStr);
		return coord;
	}

	public Coordinate getCoordinate(int x, int y) {
		String identity = String.format("[%d]-[%d]", x, y);
		Coordinate cached = cachedCoords.get(identity);
		if (cached == null) {
			cached = new Coordinate(x, y);
			cachedCoords.put(identity, cached);
			logger.trace("New Coordinates Created {}", cached);
		}
		return cached;
	}
	
	
	//either 3d or d3 should point to [4][2] coordinate
	public boolean validateCoord(String coordStr) {
		try {
			logger.debug("Validating Coordinate String {}", coordStr);
			coordStr = coordStr.toLowerCase();
			Pattern p = Pattern.compile("\\p{L}");
			Matcher m = p.matcher(coordStr);
			if (m.find()) {
				int alphaStartingIndex = m.start();
				String alphaSubString;
				char alphaChar;
				String numSubString;
				int numInt;
				//if coordinate start with alphabet
				if (alphaStartingIndex == 0) {
					alphaSubString = coordStr.substring(0,1);
					numSubString = coordStr.substring(1);
				}else {
					numSubString = coordStr.substring(0, alphaStartingIndex);
					alphaSubString = coordStr.substring(alphaStartingIndex);
					if (alphaSubString.length() != 1)
						throw new IllegalArgumentException("Invalid Alphabet Substring: " + alphaSubString);
				}
				alphaChar = alphaSubString.charAt(0);
				numInt = Integer.parseInt(numSubString);
				//valid x axis - numerical 
				if (numInt < 1 || numInt > size) {
					return false;
				}
				if (alphaChar < 'a' || alphaChar > 'a' + (size -1)) {
					return false;
				}
				return true;
			}else {
				return false;
			}
		}catch(Exception ex) {
			logger.error("Unable to parse Coordinate from String {} due to {}", coordStr, ex);
			return false;
		}
	}

	public char[] getXAxisName() {
		return xAxisNames;
	}

	public char[] getYAxisName() {
		return yAxisNames;
	}
}
