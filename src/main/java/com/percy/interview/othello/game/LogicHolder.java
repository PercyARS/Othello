package com.percy.interview.othello.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.percy.interview.othello.game.coordinates.Coordinate;
import com.percy.interview.othello.game.coordinates.manager.CoordManager;

public class LogicHolder {
	Board board;
	Score score;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	CoordManager manager;
	List<Coordinate> latestXAvailaleMoves;
	List<Coordinate> latestOAvailaleMoves;
	BoardNeighbourSearcher searcher;

	
	public LogicHolder(Score score, Board board, List<Coordinate> xInitial, List<Coordinate> oInitial, CoordManager manager) {
		this.board = board;
		this.score = score;
		this.manager = manager;
		this.searcher = new BoardNeighbourSearcher(board, manager, this);
		latestXAvailaleMoves = null;
		latestOAvailaleMoves = null;
		initGame(xInitial, oInitial);
	}
	
	void initGame(List<Coordinate> xInitial, List<Coordinate> oInitial) {
		for (Coordinate xCoord: xInitial) {
			if (isTaken(xCoord)) {
				throw new RuntimeException("Initial position can not overlap: " + xCoord);
			}else {
				playerTake(xCoord.getX(), xCoord.getY(), Player.X);
			}
		}
		for (Coordinate oCoord: oInitial) {
			if (isTaken(oCoord)) {
				throw new RuntimeException("Initial position can not overlap: " + oCoord);
			}else {
				playerTake(oCoord.getX(), oCoord.getY(), Player.O);
			}
		}
	}
	
	boolean isTaken(Coordinate coord) {
		return board.checkFill(coord) != Board.EMPTY;
	}
	

	void playerTake(int x, int y, Player player) {
		Coordinate coord = manager.getCoordinate(x, y);
		short currentValue = board.checkFill(coord);
		switch(currentValue) {
		case Board.xFilled:
			switch (player) {
			case O:
				score.decrementX(1);
				score.incrementO(1);
				logger.debug("Coord {} Taken From X By O", coord);
				break;
			case X:
				logger.warn("X Takes on its own, Coord {} current value {}", coord, currentValue);
			}
			break;
		case Board.oFilled:
			switch (player) {
			case X:
				score.decrementO(1);
				score.incrementX(1);
				logger.debug("Coord {} Taken From O By X", coord);
				break;
			case O:
				logger.error("O Takes on its own, Coord {} current value {}", coord, currentValue);
			}
			break;
		case Board.EMPTY:
			logger.debug("Coord {} Taken First Time By {}", coord, player);
			switch (player) {
			case X:
				score.incrementX(1);
				break;
			case O:
				score.incrementO(1);
				break;
			}
			break;
		default:
			throw new RuntimeException("Invalid field value at: " + coord + " value: " + currentValue);
		}
		board.fill(player, coord);
	}
	
	/*
	 * Update the available moves for each player
	 * and return false if game is over
	 */
	public boolean updateStateFalseIfOver() {
		int xScore = score.getXScore();
		int oScore = score.getOScore();
		//all pieces being placed
		if (xScore + oScore == board.getTotal()) {
			logger.info("Board Full, Game Over");
			return false;
		}
		latestXAvailaleMoves = singularSearch(Player.X);
		if (latestXAvailaleMoves.size() > 0) {
			//flush o moves if x is going to move
			latestOAvailaleMoves = null;
			return true;
		}
		//maintain the latest o available moves since x is not going to move
		latestOAvailaleMoves = singularSearch(Player.O);
		if (latestOAvailaleMoves.size() == 0) {
			return false;
		}else {
			return true;
		}
}


	public void evaluateMove(Player player, Coordinate playerMove) {
		logger.info("Evaluate Player {} Move Coord {} With Flipping Enabled", player, playerMove);
		searcher.isValidMove(playerMove.getX(), playerMove.getY(), player, true);
		
	}

	/*
	 * Single thread brute force search
	 */
	List<Coordinate> singularSearch(Player player){
		List<Coordinate> moves = new ArrayList<Coordinate>();
		for (int i = 0; i < board.getSize(); i++) {
			for (int j = 0; j < board.getSize(); j++) {
				Coordinate coord = manager.getCoordinate(i, j);
				//only look for empty spot
				if (board.checkFill(coord) == Board.EMPTY) {
					if (searcher.isValidMove(i, j, player, false)) {
						logger.debug("Valid location {} found for {}", coord, player);
						moves.add(coord);
					}
				}
			}
		}
		return moves;
	}
	
	/*
	 * Return cached version if possible
	 */
	public List<Coordinate> getAvailableMoves(Player player) {
		List<Coordinate> availableMoves = null;
		switch (player) {
		case O:
			if (latestOAvailaleMoves != null) {
				availableMoves =  latestOAvailaleMoves;
			}
			break;
		case X:
			if (latestXAvailaleMoves != null) {
				availableMoves =  latestXAvailaleMoves;
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid player: " + player);
		}
		if (availableMoves == null) {
			availableMoves = singularSearch(player);
		}
		logger.info("Found all the available {} moves {}", player, Arrays.toString(availableMoves.toArray()));
		return availableMoves;
	}

}


class BoardNeighbourSearcher{
	private Board board;
	private CoordManager manager;
	private LogicHolder holder;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public BoardNeighbourSearcher(Board board, CoordManager manager, LogicHolder holder) {
		this.board = board;
		this.manager = manager;
		this.holder = holder;
	}
	

	//check all adjacent spots and find if its ok to move
	//if ok, flip the flippable opponent
	public boolean isValidMove(int x, int y, Player player, boolean doFlip) {
		boolean isValid = false;
		short own;
		switch(player){
		case O:
			own = Board.oFilled;
			break;
		case X:
			own = Board.xFilled;
			break;
		default:
			throw new IllegalArgumentException("Unrecognizable Player: " + player.toString()); 
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int current_x = x + i;
				int current_y = y + j;
				//TODO: 0-0 condition
				//check if the neighbour exists on board
				if (board.isValidIndex(current_x, current_y)) {
					short neighbourVal = board.checkFill(manager.getCoordinate(current_x, current_y));
					//check if the neighbour is empty or its own
					if (neighbourVal == Board.EMPTY || neighbourVal == own) {
						continue;
					}
					//check the same direction
					else {
						boolean done = false;
						while (!done) {
							current_x+=i;
							current_y+=j;
							if (board.isValidIndex(current_x, current_y)) {
								short nextInLineVal = board.checkFill(manager.getCoordinate(current_x, current_y));
								//right direction
								if (nextInLineVal == own) {										
									done = true;
									isValid = true;
									//if no intention to flip, return directly
									if (doFlip == false)
										return isValid;
									//otherwise, flip all the opponents on the way to this spot
									else {
										//take current first
										holder.playerTake(x, y, player);
										//trace back to initial x and y
										while (true) {
											current_x-=i;
											current_y-=j;
											if (current_x == x && current_y == y) {
												break;
											}else {
												logger.trace("Flip {}-{}", current_x, current_y);
												holder.playerTake(current_x, current_y, player);
											}
										}
									}
								}
							}
							//no match of own was found in the line but out of bound 
							else {
								done = true;
							}
						}
					}
				}
				
			}
		}
		return isValid;
	}
}
