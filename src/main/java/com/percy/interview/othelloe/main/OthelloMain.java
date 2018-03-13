package com.percy.interview.othelloe.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.percy.interview.othello.config.Properties;
import com.percy.interview.othello.game.Board;
import com.percy.interview.othello.game.LogicHolder;
import com.percy.interview.othello.game.Score;
import com.percy.interview.othello.game.coordinates.Coordinate;
import com.percy.interview.othello.game.coordinates.manager.CoordManager;
import com.percy.interview.othelloe.main.utils.CommandLineHelper;
import com.percy.interview.othelloe.main.utils.ReflectionUtils;


public class OthelloMain 
{
    static Logger logger = LoggerFactory.getLogger(OthelloMain.class);    

    		
    	static List<Coordinate> getCoordinates(List<String> strList, CoordManager manager){
        		List<Coordinate> coordList = new ArrayList<Coordinate>();
        		for (String coordStr:strList) {
        			Coordinate coord = manager.getCoordinate(coordStr);
        			if (coord == null) {
        				throw new RuntimeException("Invalid Initial Coordinate: " + coordStr);
        			}
        			coordList.add(coord);
        		}
        		return coordList;
    	}
    
    
    	
    public static void main( String[] args )
    {
    		logger.info("Initializing...");
    		int boardSize = Properties.getIntegerProperty("board.size", 8);
    		if (boardSize < 8 )
    			throw new RuntimeException("boardSize must be greater than 8");
		String converterClazz = Properties.getStringProperty("coordinate.converter.class");
		CoordManager manager;
		try {
    			manager = ReflectionUtils.getInstanceofInterface(converterClazz, CoordManager.class);
    			manager.init(boardSize);
    		}catch(Exception ex) {
    			throw new RuntimeException("Unable to instantiate coordinate manager class from name: " + converterClazz + "\n" + ex);
    		}
    		List<String> initialX = Properties.getStringListProptery("playerX.initial", Arrays.asList("5d", "4e"));
    		List<String> initialO = Properties.getStringListProptery("playerO.initial", Arrays.asList("4d", "5e"));
    		Score score = new Score();
    		Board board = new Board(boardSize, manager.getXAxisName(), manager.getYAxisName());
    		LogicHolder logic = new LogicHolder(score, board, getCoordinates(initialX, manager), getCoordinates(initialO, manager), manager);
    		logger.info("Initialization finished");
    		CommandLineHelper cmdHelper = new CommandLineHelper(board, score, logic, manager);
    		logger.info("Game begins");
    		cmdHelper.begin();
    		logger.info("Game is over");
    		cmdHelper.cleanUp();
    		}
   }
