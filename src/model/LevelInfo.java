package model;

public class LevelInfo {
	private int id;
	private int oneStartargetScore;
	private int userRating;
	private int numberOfMoves;
	private int roundTime;
	private int dropSpeed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOneStartargetScore() {
		return oneStartargetScore;
	}

	public void setOneStartargetScore(int oneStartargetScore) {
		this.oneStartargetScore = oneStartargetScore;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		this.userRating = userRating;
	}

	public int getNumberOfMoves() {
		return numberOfMoves;
	}

	public void setNumberOfMoves(int numberOfMoves) {
		this.numberOfMoves = numberOfMoves;
	}

	public int getRoundTime() {
		return roundTime;
	}

	public void setRoundTime(int roundTime) {
		this.roundTime = roundTime;
	}

	public int getDropSpeed() {
		return dropSpeed;
	}

	public void setDropSpeed(int dropSpeed) {
		this.dropSpeed = dropSpeed;
	}
}