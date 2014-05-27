package com.ankco.numfall;

import model.LevelInfo;

public class CurrentLevel {
	private static LevelInfo currentLevel;
	private static int currScore;

	public static LevelInfo getCurrentLevel() {
		return currentLevel;
	}

	public static void setCurrentLevel(LevelInfo currentLevel) {
		CurrentLevel.currentLevel = currentLevel;
	}

	public static int getCurrScore() {
		return currScore;
	}

	public static void setCurrScore(int currScore) {
		CurrentLevel.currScore = currScore;
	}
	
}
