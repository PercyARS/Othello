package com.percy.interview.othelloe.main.utils;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.percy.interview.othello.game.Board;
import com.percy.interview.othello.game.LogicHolder;
import com.percy.interview.othello.game.Player;
import com.percy.interview.othello.game.Score;
import com.percy.interview.othello.game.coordinates.Coordinate;
import com.percy.interview.othello.game.coordinates.manager.CoordManager;

public class CommandLineHelper {
	static final String NEW_LINE = System.lineSeparator();
	static final String GREETINGS = "##########Welcome to Othello!##########";
	static final String CURRENT_POSITION = NEW_LINE + "##########Current Situation##########";
	static final String CURRENT_SCORE = "Score: ";
	static final String PLAYER1_NAME_PROMPT = "Enter the name of first player: ";
	static final String PLAYER2_NAME_PROMPT = "Enter the name of second player: ";
	static final String VOID_RESPONSE = "Void response, Please try again: ";
	static final String INVALID_MOVE = "Invalid move, try again: ";
	static final String INVALID_COORD = "Invalid coordinate, try again: ";
	static final String NO_VALID_MOVE = "No valid move available, other player's turn";
	static final String GAME_OVER = "##########The Game Is Over##########" + NEW_LINE + "No further moves available";
	static final String BAR = "#####################################";
	String PLAYER1_PROMPT;
	String PLAYER2_PROMPT;

	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Console console;
	BufferedReader reader;
	Board board;
	Score score;
	LogicHolder logic;
	CoordManager manager;
	String player1Name;
	String player2Name;
	
	public CommandLineHelper(Board board, Score score, LogicHolder logic, CoordManager manager) {
		this.console = System.console();
		this.reader = new BufferedReader(new InputStreamReader(System.in));
		this.board = board;
		this.score = score;
		this.logic = logic;
		this.manager = manager;
		init();
	}
	
	
	void init() {
		writeToConsole(GREETINGS);
		player1Name = readFromConsole(PLAYER1_NAME_PROMPT);
		player2Name = readFromConsole(PLAYER2_NAME_PROMPT);
		PLAYER1_PROMPT = String.format("Player %s (%s) move:", player1Name, Player.X);
		PLAYER2_PROMPT = String.format("Player %s (%s) move:", player2Name, Player.O);
		writeSituation();
		logger.info("Player names entered {} and {}", player1Name, player2Name);
	}
	
	void writeToConsole(String line) {
		/*
		 * work around for eclipse console issue
		 */
		if (console == null) {
			System.out.printf("%s%s", line, NEW_LINE);
		}
		else {
			this.console.printf("%s%s", line, NEW_LINE);
		}
	}
	
	String readFromConsole(String prompt) {
		String ans;
		if (console == null) {
			try {
				System.out.print(prompt);
				ans = reader.readLine();
				while(ans.isEmpty()) {
					System.out.print(VOID_RESPONSE);
					ans = reader.readLine();
				}
			}catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}else {
			ans = console.readLine(prompt);
			while(ans.isEmpty()) {
				ans = console.readLine(VOID_RESPONSE);
			}
		}
		return ans;
	}
	
	void writeWinner() {
		String winnerString = "";
		if (score.getOScore() == score.getXScore()) {
			winnerString = "It's a draw";
		}
		else if (score.getOScore() > score.getXScore()) {
			winnerString = "Player " + player2Name + " wins";
		}
		else {
			winnerString = "Player " + player1Name + " wins";
		}
		writeToConsole(String.format("%s ( %d vs %d)", winnerString, score.getXScore(), score.getOScore()));
	}
	
	void writeSituation() {
		writeToConsole(CURRENT_POSITION);
		writeToConsole(board + "");
		String scoreString = score.toString().replace("PlayerX", player1Name).replaceAll("PlayerO", player2Name);
		writeToConsole(CURRENT_SCORE + scoreString + NEW_LINE + BAR + NEW_LINE);
	}
	
	/*
	 * Retrieve the valid move from player
	 */
	Coordinate getValidateCoord(String coordStr, List<Coordinate> validMoves) {
		Coordinate targetCoord = manager.getCoordinate(coordStr);
		boolean isValid = false;
		while (!isValid) {
			//if the coord doesn't exist
			if (targetCoord == null) {
				isValid = false;
				targetCoord = manager.getCoordinate(readFromConsole(INVALID_COORD));
			}
			//if the coord is not a valid move
			else {
				if (!validMoves.contains(targetCoord)) {
					isValid = false;
					targetCoord = manager.getCoordinate(readFromConsole(INVALID_MOVE));
				}else {
					isValid = true;
				}
			}
		}
		return targetCoord;
	}
	
	public void cleanUp() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void begin() {
		//Efficiently update each player's available moves
		while (logic.updateStateFalseIfOver()) {
			//Get all the available moves for X
			List<Coordinate> xValidMoves = logic.getAvailableMoves(Player.X);
			if (xValidMoves.size() > 0) {
				String player1Move = readFromConsole(PLAYER1_PROMPT);
				Coordinate xMove = getValidateCoord(player1Move, xValidMoves);
				logger.info("X moved to {}", xMove);
				logic.evaluateMove(Player.X, xMove);
				writeSituation();
			}else {
				logger.info("No valid move for X, skip");
				writeToConsole(NO_VALID_MOVE);
			}
			//Get all the available moves for O
			List<Coordinate> oValidMoves = logic.getAvailableMoves(Player.O);
			if (oValidMoves.size() > 0) {
				String player2Move = readFromConsole(PLAYER2_PROMPT);
				Coordinate oMove = getValidateCoord(player2Move, oValidMoves);
				logger.info("O moved to {}", oMove);
				logic.evaluateMove(Player.O, oMove);
				writeSituation();
			}else {
				logger.info("No valid move for O, skip");
				writeToConsole(NO_VALID_MOVE);
			}
		}
		writeToConsole(GAME_OVER);
		writeWinner();
	}

	
}
