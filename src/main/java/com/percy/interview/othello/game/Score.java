package com.percy.interview.othello.game;

import java.util.concurrent.atomic.AtomicInteger;

public class Score {
	AtomicInteger xScore;
	AtomicInteger oScore;
	
	public Score() {
		xScore = new AtomicInteger(0);
		oScore = new AtomicInteger(0);
	}
	
	public void incrementX(int count) {
		xScore.addAndGet(count);
	}
	
	public void decrementX(int count) {
		xScore.addAndGet(-count);
	}
	
	public void incrementO(int count) {
		oScore.addAndGet(count);
	}
	
	public void decrementO(int count) {
		oScore.addAndGet(-count);
	}
	
	public int getXScore() {
		return xScore.get();
	}
	
	public int getOScore() {
		return oScore.get();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[PlayerX - ").append(xScore.get()).append(",PlayerO - ").append(oScore.get()).append("]");
		return sb.toString();
	}
	
}
